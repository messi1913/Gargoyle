/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.ability
 *	작성일   : 2016. 10. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.ability;

/**
 * @author KYJ
 *
 */
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class PolygonEx extends Application {

	@Override
	public void start(Stage stage) {

		Pane g = new Pane();
		ScrollPane convert = XY.convert(g);
		{
			//한 점의 좌표가 주어진경우
			double x = 150;
			double y = 150;
			//빗변의 길이
			double r = 100;
			double degree = Math.atan2(y, x);

			double x1 = x + (r / Math.tan(degree));
			double y1 = y;

			double x2 = x1;
			double y2 = y + y - r;

			Polygon polygon = new Polygon();

			polygon.setLayoutX(x);
			polygon.setLayoutY(x);
			polygon.getPoints().addAll(new Double[] { x, y, x1, y1, x2, y2 });



			g.getChildren().add(polygon);
			g.getChildren().add(new Line(1,1,10,10));
		}

		{
			//한 점의 좌표가 주어진경우
			double x = 150;
			double y = 150;
			//빗변의 길이
			double r = 100;
			double degree = Math.atan2(y, x);

			double x1 = x + (r / Math.tan(degree));
			double y1 = y;

			double x2 = x1;
			double y2 = y + r;

			Polygon polygon = new Polygon();
			polygon.getPoints().addAll(new Double[] { x, y, x1, y1, x2, y2 });
			g.getChildren().add(polygon);
		}
		//		Line line = new Line();
		//		line.setStartX(x);
		//		line.setStartY(y);
		//		line.setEndX(x2);
		//		line.setEndY(y2);
		//
		//		Text text = new Text("x : " + x + " y : " + y);
		//		text.setX(x);
		//		text.setY(y - 20);
		//
		//		Text text1 = new Text("x1 : " + x1 + " y1 : " + y1);
		//		text1.setX(x1);
		//		text1.setY(y1 - 20);
		//
		//		Text text2 = new Text("x2 : " + x2 + " y2 : " + y2);
		//		text2.setX(x2);
		//		text2.setY(y2 - 20);
		//		g.getChildren().addAll(polygon, text, text1, text2);

		Scene scene = new Scene(convert, 1800, 900);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
