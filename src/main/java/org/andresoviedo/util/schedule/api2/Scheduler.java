package org.andresoviedo.util.schedule.api2;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andresoviedo.util.bean.PeriodBean;
import org.andresoviedo.util.bean.PeriodBean.DailyPeriod;
import org.andresoviedo.util.time.TimeUtils;

/**
 * The <code>Scheduler</code> class extends the functionality to the {@link Timer} class allowing to schedule tasks for a specified period
 * of time.<br>
 * There is only a restriction about using this class, that is, tasks should be canceled through the {@link #cancelTask(TimerTask)} method. <br>
 * It acts exactly the same as the method {@link TimerTask#cancel()} method but frees internal resources for this <code>Timer</code>
 * implementation.
 * 
 * @see PeriodBean
 * 
 * @author andresoviedo
 * 
 */
public class Scheduler extends Timer {

	/**
	 * Tasks list and wrappers
	 */
	private Map<TimerTask, PeriodTask> periodTasks = new Hashtable<TimerTask, PeriodTask>();

	private Logger logger = Logger.getLogger("");

	private boolean newTasksMayBeScheduled = true;

	/**
	 * Constructs a scheduler that
	 */
	public Scheduler() {
		super();
	}

	/**
	 * Schedules a task for the specified period of time.
	 * 
	 * @param task
	 *            the task to be scheduled
	 * @param period
	 *            the period to execute task
	 * @return the next execution time for the scheduled task
	 */
	public Date schedule(TimerTask task, PeriodBean period) {
		// calculate next execution time
		Date nextExecution = TimeUtils.calculateNextExecutionDate(period);
		if (period.endDate != null && new Date(period.endDate.getTime() + period.allowedExpiredTime).before(nextExecution)) {
			// next execution times exceeds expiration time
			throw new IllegalArgumentException("Next execution time exceeds expiration time. " + nextExecution.getTime() + ">"
					+ new Date(period.endDate.getTime() + period.allowedExpiredTime));
		}
		// schedule wrapper task for one time execution at the calculated time
		PeriodTask periodTask = new PeriodTask(task, period);
		periodTasks.put(task, periodTask);
		period.nextExecution = nextExecution;
		super.schedule(periodTask, nextExecution);
		return nextExecution;
	}

	@Override
	public void cancel() {
		super.cancel();
		newTasksMayBeScheduled = false;
	}

	/**
	 * Cancel the specified <code>TimerTask</code> for execution.
	 * 
	 * @param task
	 *            the task to be canceled
	 * @return true if the task exists and the task could be canceled
	 * @see TimerTask#cancel();
	 */
	public boolean cancelTask(TimerTask task) {
		PeriodTask periodTask = periodTasks.get(task);
		if (periodTask != null) {
			periodTasks.remove(task);
			return periodTask.cancel();
		}
		return task.cancel();
	}

	public static void main(String[] args) {
		Scheduler s = new Scheduler();
		// WeeklyPeriod p = (WeeklyPeriod) PeriodBean.parsePeriod("WEEKLY ON Sun|Mon|Tue|Wed|Thu|Fri|Sat|Sun|Mon AT 18:00:00");
		org.andresoviedo.util.bean.PeriodBean.DailyPeriod p = (DailyPeriod) PeriodBean.parsePeriod("DAILY AT 18:00:00");
		// p.startDate = new Date(System.currentTimeMillis() + 60000 * 60 * 24 * 7);
		p.allowedExpiredTime = 300000;
		// p.endDate = new Date(System.currentTimeMillis() + 60000 * 60 * 24 * 7);
		// System.out.println("Start date: " + p.startDate);
		Date nextDate = s.schedule(new TimerTask() {
			public void run() {
				System.out.println("hola");
			}
		}, p);
		System.out.println("Next date: " + nextDate);
		s.cancel();
	}

	/**
	 * Wrapper class of the <code>TimerTask</code> class, that is used to reschedule one time execution tasks.
	 * 
	 * @author andresoviedo
	 */
	class PeriodTask extends TimerTask {
		TimerTask task;
		PeriodBean period;

		PeriodTask(TimerTask task, PeriodBean period) {
			this.task = task;
			this.period = period;
		}

		public void run() {
			period.lastExecution = period.nextExecution;
			task.run();
			try {
				if (newTasksMayBeScheduled) {
					schedule(task, period);
				}
			} catch (IllegalArgumentException ex) {
				logger.logp(Level.INFO, "PeriodTask", "run", ex.getMessage());
			}
		}
	}
}
