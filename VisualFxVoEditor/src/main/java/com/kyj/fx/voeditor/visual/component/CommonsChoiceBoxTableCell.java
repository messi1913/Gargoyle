/********************************
 *	프로젝트 : CrudGrigSample
 *	패키지   : application
 *	작성일   : 2016. 5. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Optional;

import com.kyj.fx.voeditor.visual.framework.CodeDVO;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableStringValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class CommonsChoiceBoxTableCell<T> extends TableCell<T, String> {

	private ObservableList<CodeDVO> codes;
	private ChoiceBox<CodeDVO> choiceBox;

	/**
	 * 기본으로 제공되는 컨버터. UI에 디스플레이되는 항목이 기술됨.
	 *
	 * @최초생성일 2016. 5. 17.
	 */
	private StringConverter<CodeDVO> stringConverter;

	public CommonsChoiceBoxTableCell(ObservableList<CodeDVO> codes) {
		this(codes, new StringConverter<CodeDVO>() {

			@Override
			public String toString(CodeDVO object) {
				if (object == null)
					return "";
				return object.getNm();
			}

			@Override
			public CodeDVO fromString(String code) {

				Optional<CodeDVO> findFirst = codes.stream().filter(v -> v.getCode().equals(code)).findFirst();
				if (findFirst.isPresent())
					return findFirst.get();
				return null;
			}
		});
	}

	public CommonsChoiceBoxTableCell(ObservableList<CodeDVO> codes, StringConverter<CodeDVO> stringConverter) {
		this.codes = codes;
		this.stringConverter = stringConverter;
		choiceBox = new ChoiceBox<>(codes);
		choiceBox.setConverter(stringConverter);
		choiceBox.setOnAction(event -> {

			CodeDVO value = choiceBox.getValue();
			if (value == null)
				return;

			ObservableValue<String> cellObservableValue = getTableColumn().getCellObservableValue(getIndex());
			WritableStringValue writableStringValue = (WritableStringValue) cellObservableValue;
			if (cellObservableValue instanceof WritableStringValue) {
				writableStringValue.set(value.getCode());
			}
			event.consume();
		});

	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setItem(null);
			setGraphic(null);
		} else {
			int index = getIndex();
			String code = getTableColumn().getCellData(index);
			CodeDVO fromString = stringConverter.fromString(code);
			// System.out.printf("%d %s %s\n", index, code, fromString);
			choiceBox.setValue(fromString);
			choiceBox.setDisable(!getTableColumn().editableProperty().get());
			setGraphic(choiceBox);
		}
	}

}
