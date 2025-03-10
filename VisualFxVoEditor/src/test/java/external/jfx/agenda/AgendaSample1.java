/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.jfx.agenda
 *	작성일   : 2016. 9. 23.
 *	작성자   : KYJ
 *******************************/
package external.jfx.agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.internal.scene.control.skin.agenda.AgendaSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;

public class AgendaSample1 extends Application {

	final Agenda agenda;
	final AgendaSkinSwitcher agendaSkinSwitcher;

	public AgendaSample1() {
		agenda = new Agenda();
		agendaSkinSwitcher = new AgendaSkinSwitcher(agenda);

		// setup appointment groups
		final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
		for (Agenda.AppointmentGroup lAppointmentGroup : agenda.appointmentGroups()) {
			lAppointmentGroupMap.put(lAppointmentGroup.getDescription(), lAppointmentGroup);
		}

		// accept new appointments
		agenda.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange, Agenda.Appointment>() {
			@Override
			public Agenda.Appointment call(LocalDateTimeRange dateTimeRange) {


				return new Agenda.AppointmentImplLocal().withStartLocalDateTime(dateTimeRange.getStartLocalDateTime())
						.withEndLocalDateTime(dateTimeRange.getEndLocalDateTime()).withSummary("new").withDescription("new")
						.withAppointmentGroup(lAppointmentGroupMap.get("group01"));


			}
		});

		// initial set
		LocalDate lTodayLocalDate = LocalDate.now();
		LocalDate lTomorrowLocalDate = LocalDate.now().plusDays(1);
		int idx = 0;
		final String lIpsum = "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, commodo vitae, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. Donec non enim in turpis pulvinar facilisis. Ut felis. Praesent dapibus, neque id cursus faucibus, tortor neque egestas augue, eu vulputate magna eros eu erat. Aliquam erat volutpat. Nam dui mi, tincidunt quis, accumsan porttitor, facilisis luctus, metus";
		LocalDateTime lMultipleDaySpannerStartDateTime = lTodayLocalDate.atStartOfDay().plusHours(5);
		lMultipleDaySpannerStartDateTime = lMultipleDaySpannerStartDateTime
				.minusDays(lMultipleDaySpannerStartDateTime.getDayOfWeek().getValue() > 3
						&& lMultipleDaySpannerStartDateTime.getDayOfWeek().getValue() < 7 ? 3 : -1);
		LocalDateTime lMultipleDaySpannerEndDateTime = lMultipleDaySpannerStartDateTime.plusDays(2);

		Appointment[] lTestAppointments = new Appointment[] {
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 00)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(11, 30))).withSummary("A")
						.withDescription("A much longer test description").withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(10, 00))).withSummary("B")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group08")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(9, 30))).withSummary("C")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group09")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(9, 00)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(13, 30))).withSummary("D")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(10, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(11, 00))).withSummary("E")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(12, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(13, 30))).withSummary("F")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(13, 00)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(13, 30))).withSummary("H")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(14, 00)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(14, 45))).withSummary("G")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(15, 00)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(16, 00))).withSummary("I")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(15, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(16, 00))).withSummary("J")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group07"))
						// backwards compatibility: calendar based appointment
						,
				new Agenda.AppointmentImpl()
						.withStartTime(new GregorianCalendar(lTodayLocalDate.getYear(), lTodayLocalDate.getMonthValue() - 1,
								lTodayLocalDate.getDayOfMonth(), 4, 00))
						.withEndTime(new GregorianCalendar(lTodayLocalDate.getYear(), lTodayLocalDate.getMonthValue() - 1,
								lTodayLocalDate.getDayOfMonth(), 5, 30))
						.withSummary("Cal").withDescription("Calendar based").withAppointmentGroup(lAppointmentGroupMap.get("group08"))
						// ZonedDateTime: there is no additional value in using ZonedDateTime everywhere, so we just have one test appointment
						,
				new Agenda.AppointmentImplTemporal()
						.withStartTemporal(ZonedDateTime.of(lTodayLocalDate, LocalTime.of(2, 00), ZoneId.systemDefault()))
						.withEndTemporal(ZonedDateTime.of(lTodayLocalDate, LocalTime.of(3, 30), ZoneId.systemDefault()))
						.withSummary("Zoned").withDescription("Zoned based").withAppointmentGroup(lAppointmentGroupMap.get("group08"))
						// -----
						// too short for actual rendering
				,
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(20, 30)))
						.withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(20, 31))).withSummary("S")
						.withDescription("Too short").withAppointmentGroup(lAppointmentGroupMap.get("group07"))
						// -----
						// tasks
				,
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 10)))
						.withSummary("K kk kkkkk k k k k ").withDescription("A description " + (++idx))
						.withAppointmentGroup(lAppointmentGroupMap.get("group17")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 10)))
						.withSummary("M mmm m m m m m mmmm").withDescription("A description " + (++idx))
						.withAppointmentGroup(lAppointmentGroupMap.get("group18")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 11)))
						.withSummary("N nnnn n n n  nnnnn").withDescription("A description " + (++idx))
						.withAppointmentGroup(lAppointmentGroupMap.get("group19")),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(6, 00)))
						.withSummary("L asfsfd dsfsdfs fsfds sdgsds dsdfsd ").withDescription("A description " + (++idx))
						.withAppointmentGroup(lAppointmentGroupMap.get("group11"))
						// -----
						// wholeday
				,
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lTodayLocalDate.atStartOfDay()).withSummary("whole1")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group17"))
						.withWholeDay(true),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lTodayLocalDate.atStartOfDay()).withSummary("whole2")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group18"))
						.withWholeDay(true),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lTodayLocalDate.atStartOfDay())
						.withEndLocalDateTime(lTomorrowLocalDate.atStartOfDay()) // at we going to do en
						.withSummary("whole3").withDescription("A description " + (++idx))
						.withAppointmentGroup(lAppointmentGroupMap.get("group19")).withWholeDay(true),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lTomorrowLocalDate.atStartOfDay())
						.withEndLocalDateTime(lTomorrowLocalDate.atTime(13, 00)).withSummary("whole+end")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group13"))
						.withWholeDay(true),
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lTodayLocalDate.atStartOfDay())
						.withEndLocalDateTime(lTomorrowLocalDate.atTime(13, 00)).withSummary("whole+spanning")
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group14"))
						.withWholeDay(true)
						// -----
						// regular spanning
				,
				new Agenda.AppointmentImplLocal().withStartLocalDateTime(lMultipleDaySpannerStartDateTime)
						.withEndLocalDateTime(lMultipleDaySpannerEndDateTime)
						.withSummary(lIpsum.substring(0, 20 + new Random().nextInt(lIpsum.length() - 20)))
						.withDescription("A description " + (++idx)).withAppointmentGroup(lAppointmentGroupMap.get("group20")) };
		agenda.appointments().addAll(lTestAppointments);

		// action
		agenda.setActionCallback((appointment) -> {

			System.out.println("action callback");
//						showPopup(agenda, "Action on " + appointment);
			return null;
		});
	}

	/** Scales the node based on the standard letter, portrait paper to be printed.
	 * @param node The scene node to be printed.
	 */
	public void print() {
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			agenda.print(job);
			job.endJob();
		}
	}

	//	@Override
	//	public String getJavaDocURL() {
	//		return "http://jfxtras.org/doc/8.0/jfxtras-agenda/" + Agenda.class.getName().replace(".", "/") + ".html";
	//	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * get the calendar for the first day of the week
	 */
	static private Calendar getFirstDayOfWeekCalendar(Locale locale, Calendar c) {
		// result
		int lFirstDayOfWeek = Calendar.getInstance(locale).getFirstDayOfWeek();
		int lCurrentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		int lDelta = 0;
		if (lFirstDayOfWeek <= lCurrentDayOfWeek) {
			lDelta = -lCurrentDayOfWeek + lFirstDayOfWeek;
		} else {
			lDelta = -lCurrentDayOfWeek - (7 - lFirstDayOfWeek);
		}
		c = ((Calendar) c.clone());
		c.add(Calendar.DATE, lDelta);
		return c;
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane(agenda);
		root.setTop(this.agendaSkinSwitcher);

		primaryStage.setScene(new Scene(root));

		primaryStage.show();

	}

}