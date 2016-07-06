/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import com.kyj.fx.voeditor.visual.component.ColorPickerTableColumn.ColorCellFactory;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

/**
 * 
 * ColorPicker TableCell
 * 
 * @author KYJ
 *
 * @param <T>
 */
public class ColorPickerTableCell<T> extends TableCell<T, Color> {

	ColorCellFactory parent;
	private final ColorPicker colorPicker;

	public ColorPickerTableCell(TableColumn<T, Color> column , ColorCellFactory parent) {
		this.parent = parent;
		this.colorPicker = new ColorPicker();
		this.colorPicker.getStyleClass().add("button");
		this.colorPicker.setStyle("-fx-color-label-visible:false;");

		this.colorPicker.editableProperty().bind(column.editableProperty());
		this.colorPicker.disableProperty().bind(column.editableProperty().not());
		this.colorPicker.setOnShowing(event -> {
			final TableView<T> tableView = getTableView();
			tableView.getSelectionModel().clearSelection();
			tableView.getSelectionModel().select(getTableRow().getIndex());
			tableView.edit(getIndex(), column);
		});
		this.colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (isEditing()) {
				commitEdit(newValue);
				parent.chage(getIndex());
			}
		});
		// 텍스트는 화면에 보여주지않음.
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	public ColorPicker getColorPicker() {
		return colorPicker;
	}

	public int getRowIndex() {
		return getIndex();
	}

	@Override
	protected void updateItem(Color item, boolean empty) {
		super.updateItem(item, empty);
		setText(null);
		if (empty) {
			// 객체가 삭제될때
			setGraphic(null);
		} else {
			// 객체 생성될때
			this.colorPicker.setValue(item);
			this.setGraphic(this.colorPicker);

		}
	}
}