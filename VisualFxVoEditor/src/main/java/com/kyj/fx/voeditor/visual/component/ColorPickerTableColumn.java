/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * @author Hong
 *
 */
public class ColorPickerTableColumn<S extends Map<String, Object>> extends TableColumn<S, Color> {

	public ColorPickerTableColumn(TableColumn<Map<String, Object>, Color> col, String colName) {

		this.setStyle(" -fx-alignment: CENTER;");
		// 보여줄 UI를 디자인함.
		this.setEditable(true);
		ColorCellFactory value = new ColorCellFactory(this);
		this.setCellFactory(value);

		// 실제 값 바인드 이벤트 처리
		this.setCellValueFactory(param -> {
			S map = param.getValue();
			Object colorObj = map.get(colName);
			Color color = null;
			if (colorObj != null) {
				color = Color.web(colorObj.toString());
			}
			ObjectProperty<Color> property = new SimpleObjectProperty<>(color);
			property.addListener((oba, oldval, newval) -> map.put(colName, newval));
			return property;
		});
	}

	public void ChangeHandle(int rowIndex) {

	}

	class ColorCellFactory implements Callback<TableColumn<S, Color>, TableCell<S, Color>> {
		ColorPickerTableColumn<?> parent;

		public ColorCellFactory(ColorPickerTableColumn<?> parent) {
			this.parent = parent;
		}

		@Override
		public TableCell<S, Color> call(TableColumn<S, Color> param) {
			return new ColorPickerTableCell<>(param, this);
		}

		public void chage(int rowIndex) {
			parent.ChangeHandle(rowIndex);
		}

	}

}
