package org.andresoviedo.util.tasks;

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
 * <li>Task can get results of dependant tasks via <code>DependantTasksExecutor.results.get()</code>
 * </ul>
 * 
 * @author varqsoz
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
}
