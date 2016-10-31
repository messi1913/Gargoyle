package external;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Gargoyle Loadingbar Test
 * @author KYJ
 *
 */
public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		BorderPane pane = new BorderPane(new Label("Hello world"));
		Button value = new Button("Re");
		pane.setStyle("-fx-background-color:forestgreen");
		pane.setTop(value);
		final Scene scene = new Scene(pane, 1200, 800, Color.FORESTGREEN);
		//		scene.getStylesheets().add(SpinnerDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
		stage.setScene(scene);
		stage.setTitle("JFX Spinner Demo");
		stage.show();

		value.setOnAction(ev -> {

			extracted((Stage) value.getScene().getWindow());

		});
		//		extracted(stage);
		System.out.println("ss");
	}

	private void extracted(final Stage stage) {

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				Thread.sleep(50000000L);

				System.out.println("end sleep");

				//				succeeded();
				return null;
			}
		};

		//동기 로딩바
		//		GargoyleLoadBar<Void> gargoyleSynchProgessPopup = new GargoyleSynchLoadBar<>(stage, task);
		//		//비동기 로딩바
		//		gargoyleSynchProgessPopup = new GargoyleASynchLoadBar<>(stage, task);
		//
		////		gargoyleSynchProgessPopup.setExecutor(GargoyleSynchLoadBar.newSingleThreadExecutor);
		//		gargoyleSynchProgessPopup.start();

		System.out.println(FxUtil.showLoading(task));
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
