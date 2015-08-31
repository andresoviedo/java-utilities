package org.andresoviedo.util.tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Extension to the Java Executor to allow executing dependants task. Feautures:
 * <ul>
 * <li>Task are identified by id. Tasks can depend on the finalization of dependant tasks.</li>
 * <li>Task can get results of dependant tasks
 * </ul>
 * 
 * @author andresoviedo
 * 
 */
public class DependantTasksExecutor extends ThreadPoolExecutor {

	/**
	 * Logger
	 */
	private static final Log logger = LogFactory.getLog(DependantTasksExecutor.class);
	/**
	 * Task list identifiable by some string
	 */
	private final Map<String, Future<?>> tasks = new HashMap<String, Future<?>>();
	/**
	 * Temporary tasks results
	 */
	public static final ThreadLocal<Object[]> results = new ThreadLocal<Object[]>();

	public DependantTasksExecutor(int nThreads) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public <T> Future<T> submit(final Callable<T> task, final String taskId, final String[] dependantTasksId) {
		// TODO: tasks should not dependent on inexistent tasks neither
		// dependeing on itself

		Callable<T> wrappedTask = new DependantCallable<T>(taskId, task, dependantTasksId);

		Future<T> ret = super.submit(wrappedTask);
		synchronized (tasks) {
			tasks.put(taskId, ret);
		}

		return ret;
	}

	public void submit(final Runnable task, final String taskId, final String[] dependantTasksId) {

		Runnable wrappedTask = new Runnable() {
			@Override
			public void run() {
				Callable<Void> c = new DependantCallable<Void>(taskId, new Callable<Void>() {
					public Void call() {
						task.run();
						return null;
					}
				}, dependantTasksId);
			}
		};

		Future<?> ret = super.submit(wrappedTask);
		synchronized (tasks) {
			tasks.put(taskId, ret);
		}
	}

	class DependantCallable<T> implements Callable<T> {

		final String taskId;
		final Callable<T> originalTask;
		final String[] dependantTasksId;

		public DependantCallable(String taskId, Callable<T> originalTask, String... dependantTasksId) {
			super();
			this.taskId = taskId;
			this.originalTask = originalTask;
			this.dependantTasksId = dependantTasksId;
		}

		@Override
		public T call() throws Exception {
			logger.trace("Executing task '" + taskId + "'...");
			Object[] dependantResults = null;
			if (dependantTasksId != null && dependantTasksId.length > 0) {
				dependantResults = new Object[dependantTasksId.length];
				synchronized (tasks) {
					for (int i = 0; i < dependantTasksId.length;) {
						logger.trace("Waiting for dependant task number '" + dependantTasksId[i] + "'...");
						Future<?> dependantTask = tasks.get(dependantTasksId[i]);
						if (!dependantTask.isDone()) {
							logger.trace("Dependant task with number '" + dependantTasksId[i] + "' has not yet finalized. Waiting...");
							i = 0;
							tasks.wait(1000);
						} else {
							try {
								dependantResults[i] = dependantTask.get();
							} catch (ExecutionException ex) {
								dependantResults[i] = ex.getCause();
							}
							i++;
						}
					}
				}
			}
			T result;
			try {
				// Pasamos el resultado de las tareas dependientes
				results.set(dependantResults);
				logger.trace("Executing task '" + taskId + "' after all it's dependencies has been satisfied...");
				result = originalTask.call();
			} finally {
				// Limpieza de memoria
				results.set(null);
			}
			synchronized (tasks) {
				logger.trace("Task '" + taskId + "' finalized. Notifying any dependant task...");
				tasks.notifyAll();
			}
			logger.trace("Task '" + taskId + "' finalized!");
			return result;
		}
	}

	public static void main(String[] args) throws InterruptedException {
		DependantTasksExecutor executor = new DependantTasksExecutor(10);
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 11111");
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 11111");
				return 1;
			}
		}, "1", null);
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 22222");
				System.out.println("<< 22222 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 22222");
				return 2;
			}
		}, "2", new String[] { "1" });
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 33333");
				System.out.println("<< 33333 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 33333");
				if (true) {
					throw new IllegalArgumentException();
				}
				return 3;
			}
		}, "3", new String[] { "1" });
		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 44444");
				System.out.println("<< 44444 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 44444");
				return 4;
			}
		}, "4", new String[] { "1", "3" });

		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 55555");
				System.out.println("<< 55555 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 55555");
				return 5;
			}
		}, "5", new String[] { "4" });

		executor.submit(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				System.out.println("<< 66666");
				System.out.println("<< 66666 [" + Arrays.toString((Object[]) DependantTasksExecutor.results.get()));
				Thread.sleep((long) ((Math.random() * 5000)));
				System.out.println(">> 66666");
				return 6;
			}
		}, "6", new String[] { "5", "1", "2", "3", "4" });

		executor.shutdown();
		executor.awaitTermination(40, TimeUnit.SECONDS);
	}
}
