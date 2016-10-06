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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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

//	VBox value = new VBox();
//	HBox hBox = new HBox(5);
//	Label index = new Label();
//	Label fileName = new Label();
//	Label methodName = new Label();
//	Label message = new Label();

	public PMDListCell() {
//		hBox.setStyle("-fx-background-color:transparent");
//		index.setStyle("-fx-text-fill:black;");
//		hBox.getChildren().addAll(index, value);
//		value.getChildren().addAll(fileName, methodName, message);
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
			HBox hBox = new HBox(5);
			Label index = new Label();
			Label fileName = new Label();
			Label methodName = new Label();
			Label message = new Label();
			Label line = new Label();


			TextFlow lineExp = new TextFlow();
			Text text = new Text("Index :");
			text.setStyle("-fx-font-weight:bold;");
			lineExp.getChildren().addAll(text, new Text(String.valueOf(getIndex())));
			hBox.setStyle("-fx-background-color:transparent");
			index.setStyle("-fx-text-fill:black;");
			line.setStyle("-fx-text-fill:black;");
			hBox.getChildren().addAll(lineExp, line, value);
			value.getChildren().addAll(fileName, methodName, message);

//			index.setText(String.format("index : %d", getIndex()));
			fileName.setText(String.format("File Name : %s", item.getFilename()));
			methodName.setText(String.format("Method name : %s", item.getMethodName()));
			message.setText(String.format("Message : %s", item.getRule().getMessage()));
			line.setText(String.format("Line : %d", item.getBeginLine()));
			setGraphic(hBox);
		}
	}

}
