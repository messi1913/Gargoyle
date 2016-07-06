/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.model
 *	작성일   : 2016. 2. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.model;

import java.util.TimeZone;

/**
 * @author KYJ
 *
 */
public class GagoyleDate {

	private TimeZone timeZone = TimeZone.getDefault();

	private int year;

	private int month;

	private int day;

	public GagoyleDate(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public GagoyleDate(TimeZone timeZone, int year, int month, int day) {
		this.timeZone = timeZone;
		this.year = year;
		this.month = month;
		this.day = day;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @param day
	 *            the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public String getYearString() {
		return String.valueOf(year);
	}

	public String getMonthString() {
		return String.valueOf(month);
	}

	public String getDayString() {
		return String.valueOf(day);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GagoyleDate [timeZone=" + timeZone + ", year=" + year + ", month=" + month + ", day=" + day + "]";
	}

}
