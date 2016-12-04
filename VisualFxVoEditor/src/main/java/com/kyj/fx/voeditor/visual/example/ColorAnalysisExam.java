/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 12. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import jfxtras.scene.layout.HBox;

/**
 * @author KYJ
 *
 */
public class ColorAnalysisExam extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane();
		ColorPicker value = new ColorPicker();
		root.setTop(value);

		List<Label> arrayList = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			Label region = new Label();
			region.setPrefWidth(100);
			region.setPrefHeight(100);
			String text = "";
			switch (i) {
			case 0:
				text = "Default";
				break;
			case 1:
				text = "Brigher";
				break;
			case 2:
				text = "darker";
				break;
			case 3:
				text = "desaturate";
				break;
			case 4:
				text = "grayscale";
				break;
			case 5:
				text = "invert";
				break;
			case 6:
				text = "saturate";
				break;
			}
			region.setText(text);
			arrayList.add(region);
		}

		Region region2 = new Region();
		region2.setPrefWidth(100);
		region2.setPrefHeight(100);

		value.valueProperty().addListener((oba, o, n) -> {

			arrayList.get(0).setBackground(new Background(new BackgroundFill(n, CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(1).setBackground(new Background(new BackgroundFill(n.brighter(), CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(2).setBackground(new Background(new BackgroundFill(n.darker(), CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(3).setBackground(new Background(new BackgroundFill(n.desaturate(), CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(4).setBackground(new Background(new BackgroundFill(n.grayscale(), CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(5).setBackground(new Background(new BackgroundFill(n.invert(), CornerRadii.EMPTY, new Insets(0))));
			arrayList.get(6).setBackground(new Background(new BackgroundFill(n.saturate(), CornerRadii.EMPTY, new Insets(0))));
		});

		HBox value2 = new HBox();
		value2.getChildren().addAll(arrayList);
		root.setCenter(value2);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 4.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
