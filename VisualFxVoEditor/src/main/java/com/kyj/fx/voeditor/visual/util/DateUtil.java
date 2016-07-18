/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 05. 09.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;

/**
 * 날짜처리와 관련된 유틸리티 클래스
 *
 * @author KYJ
 *
 */
public class DateUtil {
	public static final String SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd hh:mm:ss SSS";
	public static final String SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String SYSTEM_DATEFORMAT_YYYYMMDDHH = "yyyyMMddHH";
	public static final String SYSTEM_DATEFORMAT_YYYYMMDDHHSSS = "yyyyMMddHHmmssS";
	public static final String SYSTEM_DATEFORMAT_SSS = "SSS";
	public static final String SYSTEM_DATEFORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String SYSTEM_DATEFORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public static final String SYSTEM_DATEFORMAT_YYYY_MM = "yyyyMM";
	public static final String SYSTEM_DATEFORMAT_YYYY = "yyyy";
	public static final String SYSTEM_DATEFORMAT_YY_MM_DD_HH_MM_SS = "yy-MM-dd HH:mm:ss";
	public static final String SYSTEM_DATEFORMAT_EEE = "EEE";/*요일패턴.*/
	public static final String ORACLE_DATEFORMAT_YYYYMMDDHH24MISS = "YYYYMMDDHH24MISS";

	private static final SimpleDateFormat DEFAULT_SYSTEM_DATE_FORMAT = new SimpleDateFormat(SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS_SSS);

	public static enum DATE_TYPE {
		YEAR, MONTH, WEEK
	}

	public synchronized static String getCurrentDateString() {
		SimpleDateFormat format = new SimpleDateFormat(SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS);
		Date time = GregorianCalendar.getInstance().getTime();
		return format.format(time);
	}

	public static String getDateString(long dateTime, String format) {
		Calendar instance = GregorianCalendar.getInstance();
		synchronized (instance) {
			Date time = new Date(dateTime);

			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(time);
		}
	}

	/**
	 * year의 month에 해당하는 일자 목록을 리턴받는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 19.
	 * @param year
	 * @param month
	 * @return
	 */
	public static List<GagoyleDate> getPeriodDays(int year, int month) {
		GregorianCalendar today = new GregorianCalendar(year, month, 0);
		int maxday = today.getActualMaximum((GregorianCalendar.DAY_OF_MONTH));
		TimeZone timeZone = today.getTimeZone();
		List<GagoyleDate> dateList = new ArrayList<>();
		for (int i = 1; i <= maxday; i++)
			dateList.add(new GagoyleDate(timeZone, year, month, i));
		return dateList;
	}

	/**
	 * 현재 주차에 해당하는 일자수 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 */
	public static List<GagoyleDate> getPeriodDaysByWeek() {
		Calendar instance = Calendar.getInstance();
		int year = instance.get(Calendar.YEAR);
		int week = instance.get(Calendar.WEEK_OF_YEAR);
		return getPeriodDaysByWeek(year, week);
	}

	/**
	 * 현재 주에서 gap주차를 뺀값부터 나열.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 18.
	 * @param gap
	 * @return
	 */
	public static List<GagoyleDate> getPeriodDaysByWeek(int gap) {
		Calendar current = Calendar.getInstance();
		int currentYear = current.get(Calendar.YEAR);
		int currentWeek = current.get(Calendar.WEEK_OF_YEAR);

		Calendar past = Calendar.getInstance();
		past.set(Calendar.WEEK_OF_MONTH, (-gap));
		int pastYear = past.get(Calendar.YEAR);
		int pastWeek = past.get(Calendar.WEEK_OF_YEAR);

		return getPeriodDaysByWeek(pastYear, pastWeek, currentYear, currentWeek);
	}

	public static List<GagoyleDate> getPeriodDaysByWeek(int startYear, int startWeek, int endYear, int endWeek) {

		List<GagoyleDate> periodDaysByWeek1 = getPeriodDaysByWeek(startYear, startWeek);
		List<GagoyleDate> periodDaysByWeek2 = getPeriodDaysByWeek(endYear, endWeek);

		ArrayList<GagoyleDate> arrayList = new ArrayList<>(periodDaysByWeek1.size() + periodDaysByWeek2.size());
		arrayList.addAll(periodDaysByWeek1);
		arrayList.addAll(periodDaysByWeek2);
		return arrayList;
	}

	/**
	 * 주차에 해당하는 일자수 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param year
	 * @param week
	 * @return
	 */
	public static List<GagoyleDate> getPeriodDaysByWeek(int year, int week) {

		Calendar instance = GregorianCalendar.getInstance();

		instance.setWeekDate(year, week, Calendar.SUNDAY);

		//		int minimum = instance.getActualMinimum(GregorianCalendar.DAY_OF_WEEK);
		//		int maximum = instance.getActualMaximum(GregorianCalendar.DAY_OF_WEEK);

		TimeZone timeZone = instance.getTimeZone();
		List<GagoyleDate> dateList = new ArrayList<>();

		for (int i = 0 /*minimum*/; i <= 6/*maximum*/; i++)

			dateList.add(new GagoyleDate(timeZone, instance.get(GregorianCalendar.YEAR), instance.get(GregorianCalendar.MONTH),
					instance.get(GregorianCalendar.DAY_OF_MONTH) + i));

		return dateList;
	}

	/**
	 * 첫번째 주차 날짜정보 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @param year
	 * @param week
	 * @return
	 */
	public static GagoyleDate getFirstDateByWeek(int year, int week) {
		Calendar instance = GregorianCalendar.getInstance();
		instance.setWeekDate(year, week, Calendar.SUNDAY);
		TimeZone timeZone = instance.getTimeZone();
		return new GagoyleDate(timeZone, instance.get(GregorianCalendar.YEAR), instance.get(GregorianCalendar.MONTH),
				instance.get(GregorianCalendar.DAY_OF_MONTH));
	}

	public static GagoyleDate getLastDateByWeek(int year, int week) {
		Calendar instance = GregorianCalendar.getInstance();
		instance.setWeekDate(year, week, Calendar.SUNDAY);
		TimeZone timeZone = instance.getTimeZone();
		return new GagoyleDate(timeZone, instance.get(GregorianCalendar.YEAR), instance.get(GregorianCalendar.MONTH),
				instance.get(GregorianCalendar.DAY_OF_MONTH) + 6);
	}

	public static String getCurrentDateString(String format) {
		Date time = GregorianCalendar.getInstance().getTime();
		return new SimpleDateFormat(format).format(time);
	}

	public static String getDateString(Date date) {
		return DEFAULT_SYSTEM_DATE_FORMAT.format(date);
	}

	public static String getDateString(long dateTime) {
		return getDateString(new Date(dateTime));
	}

	public static String getDateAsStr(Date date, String format) {
		return new SimpleDateFormat(format, Locale.ENGLISH).format(date);
	}

	public static String getDateAsStr(String date, String fromFormat, String toFormat) {
		String returnDate = "";

		try {
			returnDate = new SimpleDateFormat(toFormat).format(new SimpleDateFormat(fromFormat).parse(date));
		} catch (ParseException e) {
			returnDate = date;
		}

		return returnDate;
	}

	public static String getSystemDateAsStr(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static Date toDate(String data, String format) throws ParseException {
		return new SimpleDateFormat(format).parse(data);
	}

	/**
	 * 타임존에 대한 정보를 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @param tz
	 * @return
	 */
	public static String displayTimeZone(TimeZone tz) {
		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);
		String result = "";
		if (hours > 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
		}
		return result;
	}

	/**
	 * 시스템 디폴트 타임존 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @return
	 */
	public static String displayGMT() {
		return displayHourGMT(Calendar.getInstance().getTimeZone());
	}

	/**
	 * 타임존에 해당하는 GMT 시간을 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @param tz
	 * @return
	 */
	public static String displayGMT(TimeZone tz) {
		return displayHourGMT(tz);
	}

	/**
	 * 타임존에 해당하는 GMT 시간을 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @param tz
	 * @return
	 */
	public static String displayHourGMT(TimeZone tz) {
		return displayGMT(tz, (a, b) -> "GMT+%d");
	}

	/**
	 * 타임존에 해당하는 시간과 분 정보를 리턴
	 *
	 * 디폴트 포멧 ::: GMT+%d:%02d
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @param tz
	 * @return
	 */
	public static String displayHourMinuteGMT(TimeZone tz) {
		return displayGMT(tz, (a, b) -> "GMT+%d:%02d");
	}

	/**
	 * GMT 시간정보를 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 9.
	 * @param tz
	 * @param format
	 *            파라미터 시간,분 정보를 받고 포멧을 리턴함.
	 * @return
	 */
	public static String displayGMT(TimeZone tz, BiFunction<Long, Long, String> format) {

		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);
		String result = "";
		if (hours > 0) {
			result = String.format(format.apply(hours, minutes), hours, minutes, tz.getID());
		} else {
			result = String.format(format.apply(hours, minutes), hours, minutes, tz.getID());
		}
		return result;

	}
}
