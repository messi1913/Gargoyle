/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.capture;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author KYJ
 *
 */
public class TableDrawItemSkin extends DrawItemSkin<TableDrawItem> {

	protected TableDrawItemSkin(TableDrawItem control) {
		super(control);
	}

	@Override
	public void draw(VBox container) {
		TableDrawItem control = getControl();

		container.getChildren().add(new Label(control.getTitle()));

		List<Label> collect = control.getItems().stream().map(a -> new Label(a)).collect(Collectors.toList());
		container.getChildren().addAll(collect);

		// container.getChildren().add(new Label("Column-Name"));
		// container.getChildren().add(new Label("Column-Name"));
	}

}
