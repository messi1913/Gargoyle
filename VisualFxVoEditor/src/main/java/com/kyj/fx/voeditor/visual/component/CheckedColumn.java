/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.lang.reflect.Field;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

public class CheckedColumn<T> extends TableColumn<T, Boolean> {

	private static Logger LOGGER = LoggerFactory.getLogger(CheckedColumn.class);

	public CheckedColumn(String columnName) {
		super();

		this.setCellFactory(new Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>>() {
			@Override
			public TableCell<T, Boolean> call(TableColumn<T, Boolean> param) {
				CheckBoxTableCell<T, Boolean> checkBoxTableCell = new CheckBoxTableCell<T, Boolean>();
				return checkBoxTableCell;
			}
		});

		this.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<T, Boolean>, ObservableValue<Boolean>>() {

			@Override
			public ObservableValue<Boolean> call(CellDataFeatures<T, Boolean> param) {
				try {
					Field declaredField = param.getValue().getClass().getDeclaredField(columnName);
					Object obj = param.getValue();
					if (declaredField != null) {
						declaredField.setAccessible(true);
						Boolean bol = (Boolean) declaredField.get(obj);
						return new SimpleBooleanProperty(bol);
					}

					throw new IllegalAccessException(String.format("field type [ %s ] must be boolean", columnName));
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}
				return new SimpleBooleanProperty();
			}
		});

	}
}
