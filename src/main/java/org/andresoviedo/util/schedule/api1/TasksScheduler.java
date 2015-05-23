package org.andresoviedo.util.schedule.api1;

import java.sql.Time;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.andresoviedo.util.bean.ScheduleInfoBean;
import org.andresoviedo.util.data.HashList;


public class TasksScheduler {

	public static final String PERIOD_TIME = "TIME";
	public static final String PERIOD_DAILY = "DAILY";
	public static final String PERIOD_WEEKLY = "WEEKLY";
	public static final String PERIOD_MONTHLY = "MONTHLY";

	HashList<TimerTask, TaskWrapper> tasks = new HashList<TimerTask, TaskWrapper>();
	List<TasksControlPanel> panels = new Vector<TasksControlPanel>();

	private Timer timer = new Timer(true);
	private Logger logger = Logger.getLogger("");

	boolean isStarted = false;

	public TasksScheduler() {
		super();
	}

	// ------------------------------------------------------------------------------------------- //

	public JPanel getControlPanel() {
		TasksControlPanel ret = new TasksControlPanel(this);
		panels.add(ret);
		return ret;
	}

	void disposeControlPanel(JPanel panel) {
		panels.remove(panel);
	}

	private void refreshControlPanel() {
		for (TasksControlPanel panel : panels) {
			panel.refresh();
		}
	}

	// ------------------------------------------------------------------------------------------- //

	public void add(TimerTask task, ScheduleInfoBean taskInfo) {
		logger.logp(Level.INFO, "TasksScheduler", "add", "Adding task [" + task + "]...");
		TaskWrapper taskWrapper = new TaskWrapper(task, taskInfo);
		tasks.put(task, taskWrapper);
		if (isStarted) {
			schedule(taskWrapper);
		}
		refreshControlPanel();
		logger.logp(Level.INFO, "TasksScheduler", "add", "Task added.");
	}

	public void run(TimerTask task) {
		logger.logp(Level.INFO, "TasksScheduler", "run", "Scheduling task [" + task + "]...");
		if (!tasks.containsKey(task)) {
			throw new IllegalArgumentException("Task not previously added!");
		}
		schedule(tasks.get(task));
		refreshControlPanel();
		logger.logp(Level.INFO, "TasksScheduler", "run", "Task scheduled.");
	}

	public void cancel(TimerTask task) {
		logger.logp(Level.INFO, "TasksScheduler", "cancel", "Canceling task [" + task + "]...");
		if (!tasks.containsKey(task)) {
			throw new IllegalArgumentException("Task [" + task + "] doesn't exists");
		}
		TaskWrapper taskWrapper = tasks.get(task);
		if (taskWrapper.started) {
			taskWrapper.cancel();
			taskWrapper.started = false;
			refreshControlPanel();
			logger.logp(Level.INFO, "TasksScheduler", "cancel", "Task cancelled.");
		} else {
			logger.logp(Level.INFO, "TasksScheduler", "cancel", "Task not scheduled.");
		}
	}

	public void remove(TimerTask task) {
		logger.logp(Level.INFO, "TasksScheduler", "remove", "Removing task [" + task + "]...");
		if (!tasks.containsKey(task)) {
			throw new IllegalArgumentException("Task [" + task + "] doesn't exists");
		}
		cancel(task);
		tasks.remove(task);
		refreshControlPanel();
		logger.logp(Level.INFO, "TasksScheduler", "remove", "Task removed.");
	}

	public void purge() {
		// TODO: Removes all cancelled tasks from this timer's task queue.
	}

	// ------------------------------------------------------------------------------------------- //

	public void start() {
		logger.logp(Level.INFO, "TasksScheduler", "start", "Starting scheduler...");
		isStarted = true;
		for (Iterator<Map.Entry<TimerTask, TaskWrapper>> it = tasks.entrySet().iterator(); it.hasNext();) {
			Entry<TimerTask, TaskWrapper> entry = it.next();
			schedule(entry.getValue());
		}
		logger.logp(Level.INFO, "TasksScheduler", "start", "Scheduler started...");
	}

	public void stop() {
		logger.logp(Level.INFO, "TasksScheduler", "stop", "Stopping scheduler...");
		for (TimerTask task : tasks.keySet()) {
			cancel(task);
		}
		timer.cancel();
		isStarted = false;
		logger.logp(Level.INFO, "TasksScheduler", "stop", "Scheduler stopped.");
	}

	private Calendar getNextRun(ScheduleInfoBean taskInfo) {
		validateTaskInfo(taskInfo);
		logger.logp(Level.FINE, "TasksScheduler", "getNextRun", "Calculating next run [" + taskInfo + "]...");
		// Prepare hour
		Calendar nextRun = Calendar.getInstance();
		if (!taskInfo.getPeriodType().equals(PERIOD_TIME)) {
			// Working variables
			Calendar timeCalendar = Calendar.getInstance();
			timeCalendar.setTime(Time.valueOf(taskInfo.getMakeTime()));
			nextRun.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			nextRun.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			nextRun.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
			nextRun.set(Calendar.MILLISECOND, 0);
		}

		// Prepare day
		if (taskInfo.getPeriodType().equals(PERIOD_TIME)) {
			nextRun.setTimeInMillis(nextRun.getTimeInMillis() + taskInfo.getPeriodTime());
		} else if (taskInfo.getPeriodType().equals(PERIOD_DAILY)) {
			if (nextRun.getTimeInMillis() < System.currentTimeMillis()) {
				nextRun.add(Calendar.DATE, 1);
			}
		} else if (taskInfo.getPeriodType().equals(PERIOD_WEEKLY)) {
			nextRun.set(Calendar.DAY_OF_WEEK, taskInfo.getPeriodWeeklyDay());
			if (nextRun.getTimeInMillis() < System.currentTimeMillis()) {
				nextRun.add(Calendar.DATE, 7);
			}
		} else if (taskInfo.getPeriodType().equals(PERIOD_MONTHLY)) {
			nextRun.set(Calendar.DATE, taskInfo.getPeriodMonthlyDay());
			if (nextRun.getTimeInMillis() < System.currentTimeMillis()) {
				nextRun.add(Calendar.MONTH, 1);
			}
		}

		// Check
		logger.logp(Level.FINE, "TasksScheduler", "getNextRun", "Next run calculated at [" + nextRun.getTime() + "].");
		return nextRun;
	}

	private void validateTaskInfo(ScheduleInfoBean taskInfo) {
		logger.logp(Level.FINE, "TasksScheduler", "validateTaskInfo", "Validating taskInfo [" + taskInfo + "]...");
		assert (taskInfo.getPeriodType().equals(PERIOD_TIME) || taskInfo.getPeriodType().equals(PERIOD_DAILY)
				|| taskInfo.getPeriodType().equals(PERIOD_WEEKLY) || taskInfo.getPeriodType().equals(PERIOD_MONTHLY));
		if (taskInfo.getPeriodType().equals(PERIOD_TIME)) {
			assert (taskInfo.getPeriodTime() >= 0);
		} else if (taskInfo.getPeriodType().equals(PERIOD_DAILY)) {
			assert (taskInfo.getMakeTime() != null);
			Time.valueOf(taskInfo.getMakeTime());
		} else if (taskInfo.getPeriodType().equals(PERIOD_WEEKLY)) {
			assert (taskInfo.getPeriodWeeklyDay() >= Calendar.SUNDAY && taskInfo.getPeriodWeeklyDay() <= Calendar.SATURDAY);
			assert (taskInfo.getMakeTime() != null);
			Time.valueOf(taskInfo.getMakeTime());
		} else if (taskInfo.getPeriodType().equals(PERIOD_MONTHLY)) {
			assert (taskInfo.getPeriodMonthlyDay() >= 0 && taskInfo.getPeriodWeeklyDay() <= 28);
			assert (taskInfo.getMakeTime() != null);
			Time.valueOf(taskInfo.getMakeTime());
		}
		logger.logp(Level.FINE, "TasksScheduler", "validateTaskInfo", "Task validated.");
	}

	private void schedule(TaskWrapper taskWrapper) {
		if (taskWrapper.scheduled) {
			TaskWrapper newTaskWrapper = taskWrapper.clone();
			tasks.put(taskWrapper.task, newTaskWrapper, tasks.getListInterface().indexOf(taskWrapper));
			taskWrapper = newTaskWrapper;
		}
		Calendar nextRun = getNextRun(taskWrapper.taskInfo);
		logger.logp(Level.INFO, "TasksScheduler", "scheduleTask", "Scheduling task [" + taskWrapper.taskInfo.getName() + "] at ["
				+ nextRun.getTime() + "]...");
		timer.schedule(taskWrapper, nextRun.getTime());
		taskWrapper.scheduled = true;
		taskWrapper.started = true;
	}

	class TaskWrapper extends TimerTask {
		boolean scheduled = false; // variable to control TimerTask instantiation

		boolean started = false;
		boolean running = false;

		TimerTask task;
		ScheduleInfoBean taskInfo;

		TaskWrapper(TimerTask report, ScheduleInfoBean taskInfo) {
			this.task = report;
			this.taskInfo = taskInfo;
		}

		public boolean cancel() {
			task.cancel();
			return super.cancel();
		}

		public void run() {
			running = true;
			refreshControlPanel();
			long reportStartTime = System.currentTimeMillis();
			logger.logp(Level.INFO, "TaskWrapper", "run", "Running task...");
			try {
				task.run();
				long elapsedSecs = (System.currentTimeMillis() - reportStartTime) / 1000;
				logger.logp(Level.INFO, "TaskWrapper", "run", "Task runned in [" + elapsedSecs + "] seconds.");
			} catch (Exception ex) {
				logger.throwing("TaskWrapper", "run", ex);
			}
			running = false;
			refreshControlPanel();
			if (started) {
				TasksScheduler.this.run(task);
			}
		}

		public TaskWrapper clone() {
			return new TaskWrapper(task, taskInfo);
		}

		public String toString() {
			return "TasksWrapper";
		}
	}

	// ------------------------------------------------------------------------------------------- //

	public static void main(String[] args) {

		final TimerTask myTask = new TimerTask() {

			int iteration = 1;

			public boolean cancel() {
				return super.cancel();
			}

			public void run() {
				System.out.println("Ejecutando tarea 1, número " + iteration);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Ejecutada tarea 1, número " + iteration + " exitósamente");
				iteration++;
			}
		};
		ScheduleInfoBean taskInfo = new ScheduleInfoBean();
		taskInfo.setName("Task 1");
		taskInfo.setPeriodType(TasksScheduler.PERIOD_TIME);
		taskInfo.setPeriodTime(3000);
		taskInfo.setScheduled(true);

		final TimerTask myTask2 = new TimerTask() {

			int iteration = 1;

			public boolean cancel() {
				return super.cancel();
			}

			public void run() {
				System.out.println("Ejecutando tarea 2, número " + iteration);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Ejecutada tarea 2, número " + iteration + " exitósamente");
				iteration++;
			}
		};
		ScheduleInfoBean taskInfo2 = new ScheduleInfoBean();
		taskInfo2.setName("Task 2");
		taskInfo2.setPeriodType(TasksScheduler.PERIOD_TIME);
		taskInfo2.setPeriodTime(5000);
		taskInfo2.setScheduled(true);

		final TasksScheduler scheduler = new TasksScheduler();
		scheduler.add(myTask, taskInfo);
		scheduler.add(myTask2, taskInfo2);

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Running...");
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Cancelling...");
				scheduler.cancel(myTask);
			}
		});
		t.start();

		scheduler.start();

		JOptionPane.showMessageDialog(null, scheduler.getControlPanel(), "Scheduler", JOptionPane.PLAIN_MESSAGE);

		scheduler.stop();
	}
}
