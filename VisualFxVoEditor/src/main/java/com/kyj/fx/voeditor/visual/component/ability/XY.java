/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.ability
 *	작성일   : 2016. 10. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.ability;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class XY {

	public static ScrollPane convert(Node target) {

		StackPane stackPane = new StackPane();
		ScrollPane scrollPane = new ScrollPane(stackPane);
		stackPane.getChildren().add(target);
		stackPane.getChildren().add(draw());
		return scrollPane;
	}

	private static Group draw() {
		Group g = new Group();

		int pick = 100;
		for (int i = 0; i < 3600; i += 10) {
			Line line = new Line();
			line.setStartX(i);
			line.setStartY(0);
			line.setEndX(i);
			if (i % pick == 0) {
				line.setEndY(10);
				Line longLine = new Line();
				longLine.setStyle("-fx-fill:gray");
				longLine.setOpacity(0.3d);
				longLine.setStartX(i);
				longLine.setStartY(0);
				longLine.setEndX(i);
				longLine.setEndY(3600);
				g.getChildren().add(longLine);
//				Group group = new Group();
//				Text text = new Text(String.valueOf(i));
//				text.setX(i);
//				text.setY(5);
//				group.getChildren().addAll(longLine, text);
//				g.getChildren().add(group);
			}

			else
				line.setEndY(5);
			g.getChildren().add(line);

		}

		for (int i = 0; i < 3600; i += 10) {
			Line line = new Line();
			line.setStartX(0);
			line.setStartY(i);
			line.setEndY(i);
			if (i % pick == 0) {
				line.setEndX(10);
				Line longLine = new Line();
				longLine.setStyle("-fx-fill:gray");
				longLine.setOpacity(0.3d);
				longLine.setStartX(0);
				longLine.setStartY(i);
				longLine.setEndX(3600);
				longLine.setEndY(i);
				g.getChildren().add(longLine);
			}

			else
				line.setEndX(5);
			g.getChildren().add(line);
		}
		return g;
	}

}
