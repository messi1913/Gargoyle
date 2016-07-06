package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AbsoltePointFocusExam extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		ScrollPane scrPane = new ScrollPane();
		scrPane.setPrefWidth(ScrollPane.USE_COMPUTED_SIZE);

		BorderPane borderPane = new BorderPane(scrPane);
		Scene scene = new Scene(borderPane, Color.LINEN);

		VBox vbox = new VBox();
		VBox.setVgrow(vbox, Priority.ALWAYS);
		vbox.setPrefWidth(VBox.USE_PREF_SIZE);
		vbox.setPrefHeight(VBox.USE_COMPUTED_SIZE);

		for (int i = 0; i < 20; i++) {
			AnchorPane ancPane = new AnchorPane();

			AnchorPane ancPane2 = new AnchorPane();

			ancPane2.setLayoutY(500);

			TextField text1 = new TextField();

			TextField text2 = new TextField();
			text2.setLayoutY(800);

			Button btn = new Button("Focus" + i);
			btn.setOnMouseClicked(event -> {
				text2.requestFocus();

				double absolteY = FxUtil.getAbsolteY(vbox, text2) + text2.getHeight();
				scrPane.setVvalue((absolteY / vbox.getHeight()));

			});

			btn.setLayoutX(150);
			ancPane2.getChildren().add(text2);
			ancPane.getChildren().addAll(text1, btn, ancPane2);
			vbox.getChildren().add(ancPane);
		}
		scrPane.setContent(vbox);
		stage.setWidth(700);
		stage.setHeight(400);
		Label status = new Label();

		borderPane.setBottom(status);

		vbox.addEventFilter(MouseEvent.ANY, event -> {
			status.textProperty().set(String.format(" x: %s y : %s scene x : %s scene y : %s screen x :%s screen y : %s", event.getX(),
					event.getY(), event.getSceneX(), event.getSceneY(), event.getScreenX(), event.getScreenY()));

		});

		stage.setScene(scene);
		stage.show();

	}

	public static double getAbsolteX(Region longRegion, Parent target) {
		double result = target.getLayoutY();
		Parent parent2 = target.getParent();
		if (longRegion == parent2) {
			return result;
		}
		return result += getAbsolteY(longRegion, parent2);
	}

	public static double getAbsolteY(Region longRegion, Parent target) {
		double result = target.getLayoutY();
		Parent parent2 = target.getParent();
		if (longRegion == parent2) {
			return result;
		}
		return result += getAbsolteY(longRegion, parent2);
	}

}
