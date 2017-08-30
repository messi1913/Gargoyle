/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

		// container.getChildren().add(new Label(control.getTitle()));

		// List<Label> collect = control.getItems().stream().map(a -> new Label(a)).collect(Collectors.toList());
		// container.getChildren().addAll(collect);

		try {
			FXMLLoader loader = new FXMLLoader();

			VBox vbox = loader.load(TableDrawItemSkin.class.getResourceAsStream("ErdBaseTemplate.fxml"));
			vbox.getStylesheets().add(TableDrawItemSkin.class.getResource("ErdBaseTemplate.css").toExternalForm());

			ErdBaseTemplateController controller = loader.getController();
			ListView<String> lvColumns = controller.getLvColumns();

			List<String> items = control.getItems();

			if (items.isEmpty()) {
				controller.setTableName("virtual-tableName");
				lvColumns.getItems().add("");
				lvColumns.getItems().add("");
				lvColumns.getItems().add("");
				lvColumns.getItems().add("");

			} else {
				String tableName = items.get(0);

				controller.setTableName(tableName);
				for (int i = 1; i < items.size(); i++) {
					String col = items.get(i);
					lvColumns.getItems().add(col);
				}
				container.getChildren().add(vbox);
			}

		} catch (IOException e) {
			ValueUtil.toString(e);
		}

	}

}
