package org.andresoviedo.util.bean;

import java.io.Serializable;

public class ScheduleInfoBean implements Serializable {

	private static final long serialVersionUID = -4392345037665730823L;

	// TimerTask schedule
	private String name;
	private boolean isScheduled;
	private String periodType;
	private long periodTime; // period time in milliseconds.
	private int periodWeeklyDay; // day of week for making task. Set when periodType is weekly
	private int periodMonthlyDay; // day of month for making task. Set when periodType is monthly
	private String makeTime;

	public ScheduleInfoBean() {
		super();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public boolean isScheduled() {
		return isScheduled;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodTime(long periodTime) {
		this.periodTime = periodTime;
	}

	public long getPeriodTime() {
		return periodTime;
	}

	public void setPeriodWeeklyDay(int periodWeeklyDay) {
		this.periodWeeklyDay = periodWeeklyDay;
	}

	public int getPeriodWeeklyDay() {
		return periodWeeklyDay;
	}

	public void setPeriodMonthlyDay(int periodMonthlyDay) {
		this.periodMonthlyDay = periodMonthlyDay;
	}

	public int getPeriodMonthlyDay() {
		return periodMonthlyDay;
	}

	public void setMakeTime(String makeTime) {
		this.makeTime = makeTime;
	}

	public String getMakeTime() {
		return makeTime;
	}
}
