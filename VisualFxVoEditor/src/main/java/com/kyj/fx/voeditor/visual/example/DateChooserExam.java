package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.date.DateChooser;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DateChooserExam extends Application {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) {
		primaryStage.setTitle("Hello Calendar!");
		StackPane root = new StackPane();
		final DateChooser dateChooser = new DateChooser();
		
		root.getChildren().add(dateChooser);
		Scene scene = new Scene(root, 300, 250);
		
		primaryStage.setScene(scene);
		primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) {
				System.out.println("date " + dateChooser.getDate());
			}
		});
		primaryStage.show();
	}
}
