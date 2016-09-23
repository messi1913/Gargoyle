/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.jfx.agenda
 *	작성일   : 2016. 9. 23.
 *	작성자   : KYJ
 *******************************/
package external.jfx.agenda;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.scene.control.agenda.Agenda;

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
		Agenda agenda = new Agenda();

		primaryStage.setScene(new Scene(new BorderPane(agenda)));
		primaryStage.show();
	}
}
