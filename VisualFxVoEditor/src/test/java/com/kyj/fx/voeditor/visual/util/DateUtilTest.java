/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;

/**
 * @author KYJ
 *
 */
public class DateUtilTest {

	@Test
	public void daytest(){
		Date monday = DateUtil.getFirstWeekOfEEE(new Date(), Calendar.MONDAY);
		Date friday = DateUtil.getFirstWeekOfEEE(new Date(), Calendar.FRIDAY);
		System.out.println(monday);
		System.out.println(friday);
	}
	
	@Test
	public void print2016() {

		// ######################### 2016
		print(DateUtil.getPeriodDays(2016, 1));
		print(DateUtil.getPeriodDays(2016, 2));
		print(DateUtil.getPeriodDays(2016, 3));
		print(DateUtil.getPeriodDays(2016, 4));

		print(DateUtil.getPeriodDays(2016, 5));
		print(DateUtil.getPeriodDays(2016, 6));
		print(DateUtil.getPeriodDays(2016, 7));
		print(DateUtil.getPeriodDays(2016, 8));

		print(DateUtil.getPeriodDays(2016, 9));
		print(DateUtil.getPeriodDays(2016, 10));
		print(DateUtil.getPeriodDays(2016, 11));
		print(DateUtil.getPeriodDays(2016, 12));
		System.out.println("\t");
		// ######################### 2017
		print(DateUtil.getPeriodDays(2017, 1));
		print(DateUtil.getPeriodDays(2017, 2));
		print(DateUtil.getPeriodDays(2017, 3));
		print(DateUtil.getPeriodDays(2017, 4));

		print(DateUtil.getPeriodDays(2017, 5));
		print(DateUtil.getPeriodDays(2017, 6));
		print(DateUtil.getPeriodDays(2017, 7));
		print(DateUtil.getPeriodDays(2017, 8));

		print(DateUtil.getPeriodDays(2017, 9));
		print(DateUtil.getPeriodDays(2017, 10));
		print(DateUtil.getPeriodDays(2017, 11));
		print(DateUtil.getPeriodDays(2017, 12));
		System.out.println("\t");
		// ######################### 2018
		print(DateUtil.getPeriodDays(2018, 1));
		print(DateUtil.getPeriodDays(2018, 2));
		print(DateUtil.getPeriodDays(2018, 3));
		print(DateUtil.getPeriodDays(2018, 4));

		print(DateUtil.getPeriodDays(2018, 5));
		print(DateUtil.getPeriodDays(2018, 6));
		print(DateUtil.getPeriodDays(2018, 7));
		print(DateUtil.getPeriodDays(2018, 8));

		print(DateUtil.getPeriodDays(2018, 9));
		print(DateUtil.getPeriodDays(2018, 10));
		print(DateUtil.getPeriodDays(2018, 11));
		print(DateUtil.getPeriodDays(2018, 12));
		System.out.println("\t");
		// ######################### 2019
		print(DateUtil.getPeriodDays(2019, 1));
		print(DateUtil.getPeriodDays(2019, 2));
		print(DateUtil.getPeriodDays(2019, 3));
		print(DateUtil.getPeriodDays(2019, 4));

		print(DateUtil.getPeriodDays(2019, 5));
		print(DateUtil.getPeriodDays(2019, 6));
		print(DateUtil.getPeriodDays(2019, 7));
		print(DateUtil.getPeriodDays(2019, 8));

		print(DateUtil.getPeriodDays(2019, 9));
		print(DateUtil.getPeriodDays(2019, 10));
		print(DateUtil.getPeriodDays(2019, 11));
		print(DateUtil.getPeriodDays(2019, 12));
		System.out.println("\t");
	}

	void print(List<GagoyleDate> periodDays) {
		System.out.print(periodDays.get(0).getYearString());
		System.out.print(" ");
		System.out.println(periodDays.get(0).getMonthString());
		periodDays.forEach(v -> {
			System.out.print(v.getDayString() + " ");
		});
		System.out.println("");
	}

	@Test
	public void simplePrint() {

		// GMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		// SYD.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));

		System.out.println(TimeZone.getDefault().getDisplayName());
		System.out.println(TimeZone.getDefault().getDefault());
		System.out.println(TimeZone.getDefault().getID());
		System.out.println(TimeZone.getDefault().getDSTSavings());
		System.out.println(TimeZone.getDefault().getRawOffset());
		System.out.println(TimeZone.getDefault().toZoneId().toString());

		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		simpleDateFormat.setTimeZone(timeZone);

		System.out.println("Time zone: " + timeZone.getID());
		System.out.println("default time zone: " + TimeZone.getDefault().getID());
		System.out.println();

		System.out.println("UTC:     " + simpleDateFormat.format(calendar.getTime()));
		System.out.println("Default: " + calendar.getTime());

	}

	@Test
	public void printSeoulTimeZone() {
		System.out.println(DateUtil.displayTimeZone(Calendar.getInstance().getTimeZone()));
		System.out.println(DateUtil.displayGMT(Calendar.getInstance().getTimeZone()));
		System.out.println(DateUtil.displayHourGMT(Calendar.getInstance().getTimeZone()));
		System.out.println(DateUtil.displayHourMinuteGMT(Calendar.getInstance().getTimeZone()));
	}

	@Test
	public void printAllTimeZone() {
		String[] ids = TimeZone.getAvailableIDs();
		for (String id : ids) {
			System.out.println(DateUtil.displayTimeZone(TimeZone.getTimeZone(id)));
		}
		System.out.println("\nTotal TimeZone ID " + ids.length);
	}

	@Test
	public void printWeek() {

		{
			List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek(2016, 1);

			System.out.println("##### Date 2016 .1 #####");
			periodDaysByWeek.forEach(v -> {
				System.out.println(v.toDateString());
			});
		}

		{
			List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek(2016, 2);

			System.out.println("##### Date 2016 .2 #####");
			periodDaysByWeek.forEach(v -> {
				System.out.println(v.toDateString());
			});
		}

		{
			GagoyleDate first = DateUtil.getFirstDateByWeek(2016, 2);
			System.out.printf("First Day of Week %s\n", first.toDateString());
			GagoyleDate last = DateUtil.getLastDateByWeek(2016, 2);
			System.out.printf("Last Day of Week %s\n", last.toDateString());

		}

	}

	@Test
	public void gap() {
		int gap = -1;
		Calendar current = Calendar.getInstance();
		int currentYear = current.get(Calendar.YEAR);
		int currentWeek = current.get(Calendar.WEEK_OF_YEAR);

		Calendar past = Calendar.getInstance();
		past.set(Calendar.WEEK_OF_MONTH, gap);
		int pastYear = past.get(Calendar.YEAR);
		int pastWeek = past.get(Calendar.WEEK_OF_YEAR);

		System.out.println(currentYear);
		System.out.println(currentWeek);
		System.out.println(pastYear);
		System.out.println(pastWeek);
	}

}
