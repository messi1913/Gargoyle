/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/***************************
 * 
 * 문자열값 (Y,N ) 속성에 맞춰 CheckBox를 화면에 보여줌
 * 
 * @author KYJ
 *
 * @param <T>
 ***************************/
public class CheckYnColumn<T> extends TableColumn<T, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(CheckYnColumn.class);

	private String columnName;

	/**
	 * @param columnName
	 */
	public CheckYnColumn(@NamedArg("columnName") String columnName) {
		super();
		this.columnName = columnName;
		this.setCellFactory(defaultCellFactory);
		this.setCellValueFactory(defaultCellValueFactory);

	}

	private StringProperty getProperty(Object param) throws Exception {

		Field declaredField = param.getClass().getDeclaredField(columnName);
		if (declaredField != null) {
			declaredField.setAccessible(true);
			Object object = declaredField.get(param);

			if (object instanceof StringProperty) {
				StringProperty se = (StringProperty) object;
				return se;
			} else if (object == null)
				return new SimpleStringProperty("N");

			return new SimpleStringProperty("N");
		}
		return new SimpleStringProperty("N");
	}

	/**
	 * @최초생성일 2016. 8. 27.
	 */
	private Callback<TableColumn<T, String>, TableCell<T, String>> defaultCellFactory = param -> {

		TableCell<T, String> c = new TableCell<T, String>() {

			/* (non-Javadoc)
			 * @see javafx.scene.control.cell.CheckBoxTableCell#updateItem(java.lang.Object, boolean)
			 */
			@Override
			public void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {
					setGraphic(null);
				} else {
					CheckBox checkBox = new CheckBox();

					checkBox.setOnAction(ev -> {
						Object item2 = getTableRow().getItem();
						try {
							StringProperty property = getProperty(item2);
							if (checkBox.isSelected()) {
								property.set("Y");
							} else {
								property.set("N");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});

					if ("Y".equals(item)) {
						checkBox.setSelected(true);
					} else
						checkBox.setSelected(false);

					setGraphic(checkBox);
				}

			}
		};

		return c;
	};

	/**
	 * @최초생성일 2016. 8. 27.
	 */
	private Callback<javafx.scene.control.TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> defaultCellValueFactory = param -> {
		try {
			Field declaredField = param.getValue().getClass().getDeclaredField(columnName);
			Object obj = param.getValue();
			if (declaredField != null) {
				declaredField.setAccessible(true);
				Object object = declaredField.get(obj);

				if (object instanceof StringProperty) {
					StringProperty se = (StringProperty) object;
					return se;
				} else if (object == null)
					return new SimpleStringProperty("N");

				return new SimpleStringProperty("N");
			}

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return new SimpleStringProperty("N");
	};
}
