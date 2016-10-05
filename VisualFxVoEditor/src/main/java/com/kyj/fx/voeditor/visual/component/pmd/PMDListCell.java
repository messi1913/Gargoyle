/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import jfxtras.scene.layout.HBox;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
public class PMDListCell extends ListCell<RuleViolation> {

	//	private StringConverter<RuleViolation> defaultStringConverter = new StringConverter<RuleViolation>() {
	//
	//		@Override
	//		public String toString(RuleViolation object) {
	//			return object.toString();
	//		}
	//
	//		@Override
	//		public RuleViolation fromString(String string) {
	//			return null;
	//		}
	//	};

	public PMDListCell() {

	}

	//	public PMDListCell(StringConverter<RuleViolation> stringConverter) {
	//		this();
	//		this.defaultStringConverter = stringConverter;
	//	}

	/* (non-Javadoc)
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	protected void updateItem(RuleViolation item, boolean empty) {
		super.updateItem(item, empty);

		setText("");
		if (empty) {
			setGraphic(null);
		} else {
			VBox value = new VBox();
			HBox hBox = new HBox();
			hBox.setStyle("-fx-background-color:transparent");
			Label index = new Label();
			hBox.getChildren().addAll(index, value);
			Label fileName = new Label();
			Label methodName = new Label();
			Label message = new Label();
			value.getChildren().addAll(fileName, methodName, message);

			index.setText(String.valueOf(getIndex()));
			fileName.setText(item.getFilename());
			methodName.setText(item.getMethodName());
			message.setText(item.getRule().getMessage());
			setGraphic(hBox);
		}
	}

}
