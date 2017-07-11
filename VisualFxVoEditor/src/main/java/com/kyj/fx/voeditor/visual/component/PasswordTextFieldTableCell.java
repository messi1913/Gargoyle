/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Optional;
import java.util.stream.IntStream;

import com.kyj.utils.EncrypUtil;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/***************************
 *
 * passwordTextFieldTableCell
 *
 * 패스워드 처리가된 테이블 셀
 *
 * 2016-04-14 paasswordField가 특정 자리에서 글자가 꺠지는 현상이 있어 수정. PasswordField에서
 * TextField로 수정.
 *
 * @author KYJ
 *
 ***************************/
public class PasswordTextFieldTableCell<S> extends TableCell<S, String> {

	private TextField textField;

	public PasswordTextFieldTableCell() {

	}

	private static final StringConverter<String> converter = new StringConverter<String>() {

		@Override
		public String toString(String object) {
			if (object == null)
				return "";
			int length = object.length();

			Optional<String> reduce = IntStream.range(0, length > 10 ? 10 : length).mapToObj(v -> "*")
					.reduce((str1, str2) -> str1.concat(str2));
			if (reduce.isPresent())
				return reduce.get();
			return "";
		}

		@Override
		public String fromString(String string) {
			return string;
		}
	};

	public static final StringConverter<String> getPasswordTextFieldStringConverter() {
		return converter;
	}

	@Override
	public void startEdit() {
		// super.startEdit();
		if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
			return;
		}
		super.startEdit();

		if (isEditing()) {
			if (textField == null) {
				textField = createTextField(this, getConverter());
			}

			startEdit(this, getConverter(), null, null, textField);
		}
	}

	public StringConverter<String> getConverter() {
		return converter;
	}

	@Override
	public void commitEdit(String newValue) {

		try {
			super.commitEdit(EncrypUtil.encryp(newValue));
		} catch (Exception e) {
			super.commitEdit(newValue);
		}


	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		cancelEdit(this, getConverter(), null);
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		updateItem(this, getConverter(), null, null, textField);
	}

	/***************************************************************************
	 * * PasswordTextField convenience * *
	 *
	 * TextFieldTableCell클래스 참고함. reference TextFieldTableCell class.
	 *
	 * 2016.4.13 by callakrsos@naver.com
	 **************************************************************************/

	private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
		return converter == null ? cell.getItem() == null ? "" : cell.getItem().toString() : converter.toString(cell.getItem());
	}

	static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final PasswordField textField) {
		updateItem(cell, converter, null, null, textField);
	}

	static <T> void updateItem(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox, final Node graphic,
			final TextField textField) {
		if (cell.isEmpty()) {
			cell.setText(null);
			cell.setGraphic(null);
		} else {
			if (cell.isEditing()) {
				if (textField != null) {
					textField.setText(getItemText(cell, converter));
				}
				cell.setText(null);

				if (graphic != null) {
					hbox.getChildren().setAll(graphic, textField);
					cell.setGraphic(hbox);
				} else {
					cell.setGraphic(textField);
				}
			} else {
				cell.setText(getItemText(cell, converter));
				cell.setGraphic(graphic);
			}
		}
	}

	static <T> void startEdit(final Cell<T> cell, final StringConverter<T> converter, final HBox hbox, final Node graphic,
			final TextField textField) {
		if (textField != null) {
			textField.setText(getItemText(cell, converter));
		}
		cell.setText(null);

		if (graphic != null) {
			hbox.getChildren().setAll(graphic, textField);
			cell.setGraphic(hbox);
		} else {
			cell.setGraphic(textField);
		}

		textField.selectAll();

		// requesting focus so that key input can immediately go into the
		// TextField (see RT-28132)
		textField.requestFocus();
	}

	static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter, Node graphic) {
		cell.setText(getItemText(cell, converter));
		cell.setGraphic(graphic);
	}

	static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter) {
		final TextField textField = new TextField();
		textField.setPromptText("user password.");
		textField.setText(getItemText(cell, converter));
		// Use onAction here rather than onKeyReleased (with check for Enter),
		// as otherwise we encounter RT-34685
		textField.setOnAction(event -> {
			if (converter == null) {
				throw new IllegalStateException("Attempting to convert text input into Object, but provided "
						+ "StringConverter is null. Be sure to set a StringConverter " + "in your cell factory.");
			}
			cell.commitEdit(converter.fromString(textField.getText()));
			event.consume();
		});
		textField.setOnKeyReleased(t -> {
			if (t.getCode() == KeyCode.ESCAPE) {
				cell.cancelEdit();
				t.consume();
			}
		});
		return textField;
	}

}
