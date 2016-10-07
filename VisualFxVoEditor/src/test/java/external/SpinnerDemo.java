package external;

import com.kyj.fx.voeditor.visual.component.bar.GargoyleSynchLoadBar;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SpinnerDemo extends Application {

	@Override
	public void start(final Stage stage) throws Exception {

		BorderPane pane = new BorderPane(new Label("Hello world"));
		Button value = new Button("Re");

		pane.setTop(value);
		final Scene scene = new Scene(pane, 1200, 800);
		scene.getStylesheets().add(SpinnerDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
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
				Thread.sleep(3000L);

				System.out.println("end sleep");
				succeeded();
				return null;
			}
		};

		GargoyleSynchLoadBar<Void> gargoyleSynchProgessPopup = new GargoyleSynchLoadBar<>(stage, task);
		gargoyleSynchProgessPopup.setExecutor(GargoyleSynchLoadBar.newSingleThreadExecutor);
		gargoyleSynchProgessPopup.start();

		System.out.println(gargoyleSynchProgessPopup.getValue());
	}

	public static void main(final String[] arguments) {
		Application.launch(arguments);
	}
}
