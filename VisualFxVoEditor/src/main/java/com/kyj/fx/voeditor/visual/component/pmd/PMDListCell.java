/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import jfxtras.scene.layout.HBox;
import net.sourceforge.pmd.RulePriority;
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
	private static Font default1 = Font.getDefault();

	//	VBox value = new VBox();
	//	HBox hBox = new HBox(5);
	//	Label index = new Label();
	//	Label fileName = new Label();
	//	Label methodName = new Label();
	//	Label message = new Label();
	static {
//		default1 = Font.loadFont(PMDListCell.class.getResourceAsStream(FxUtil.FONTS_NANUMBARUNGOTHIC_TTF), 12);
	}

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
			Text index = new Text();
			Text priority = new Text();

			Text fileName = new Text();
			Text methodName = new Text();
			Text message = new Text();
			Text line = new Text();

			TextFlow lineExp = new TextFlow();
			Text text = new Text("Index :");
			text.setStyle("-fx-font-weight:bold;");
			lineExp.getChildren().addAll(text, new Text(String.valueOf(getIndex())));
			RulePriority prior = item.getRule().getPriority();

			hBox.setStyle("-fx-background-color:transparent");
			index.setStyle("-fx-fill:black;");
			line.setStyle("-fx-fill:black;");
			hBox.getChildren().addAll(lineExp, line, value);
			value.getChildren().addAll(priority, fileName, methodName, message);

			int priorityLevel = prior.getPriority();
			switch (priorityLevel) {
			case 3:
				priority.setStyle("-fx-fill:blue;");
				break;
			case 4:
				priority.setStyle("-fx-fill:orange");
				break;
			case 5:
				priority.setStyle("-fx-fill:red");
				break;
			}

			priority.setText(String.format("위험도 레벨 : [%d] - %s  ", priorityLevel, prior.getName()));
			fileName.setText(String.format("파일위치명 : %s", item.getFilename()));
			methodName.setText(String.format("메소드명 : %s", item.getMethodName()));
//						message.setStyle("-fx-font-style:italic;-fx-font-smoothing-type: lcd;");
			message.setText(String.format("관련 메세지 : %s", item.getRule().getMessage()));

//			message.setFont(Font.font(default1.getFamily(), FontWeight.BOLD, default1.getSize()));
			line.setText(String.format("Line : %d", item.getBeginLine()));
			setGraphic(hBox);
		}
	}

}
