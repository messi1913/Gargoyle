/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.jfx.agenda
 *	작성일   : 2016. 9. 23.
 *	작성자   : KYJ
 *******************************/
package external.jfx.agenda;

import java.util.Map;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.internal.scene.control.skin.agenda.AgendaDaysFromDisplayedSkin;
import jfxtras.internal.scene.control.skin.agenda.AgendaSkin;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;

/**
 * @author KYJ
 *
 */
public class AgendaExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		Agenda agenda = new Agenda() {
			@Override
			public Skin<?> createDefaultSkin() {
				return new AgendaDaysFromDisplayedSkin(this);
			}
		};

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

		BorderPane root = new BorderPane(agenda);

		root.getStylesheets()
				.add(AgendaSkinSwitcher.class
						.getResource("/jfxtras/internal/scene/control/skin/agenda/" + AgendaSkinSwitcher.class.getSimpleName() + ".css")
						.toExternalForm());

		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
