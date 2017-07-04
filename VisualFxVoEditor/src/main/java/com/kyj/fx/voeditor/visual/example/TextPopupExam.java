package com.kyj.fx.voeditor.visual.example;

import java.sql.Connection;

import org.fxmisc.richtext.CodeArea;

import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.component.text.ASTSqlCodeAreaHelper;
import com.kyj.fx.voeditor.visual.util.DbUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TextPopupExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	//	@Override
	//	public void start(Stage primaryStage) throws Exception {
	//		CustomTextArea textArea = new CustomTextArea("Hello world.,!");
	//
	//		textArea.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
	//			if (ev.isControlDown() && ev.getCode() == KeyCode.SPACE) {
	//
	//				Bounds blankBounds = textArea.getBlankBounds();
	//				Bounds localToScreen = textArea.localToScreen(blankBounds);
	//
	//				double minX = localToScreen.getMinX();
	//				double minY = localToScreen.getMinY();
	//				PopOver popOver = new PopOver(new Label("asdasdasd"));
	//				popOver.show(textArea, minX, minY);
	//			}
	//		});
	//		primaryStage.setScene(new Scene(textArea));
	//		primaryStage.show();
	//	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		CodeArea textArea = new CodeArea("select * from ");

		new ASTSqlCodeAreaHelper(textArea, new ConnectionSupplier() {

			@Override
			public Connection get() {
				try {
					return DbUtil.getConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

		});
		//		//		UnaryOperator<Point2D> unaryOperator = textArea.popupAnchorAdjustmentProperty().get();
		//		//		unaryOperator.
		//		TableOpenResourceView tableOpenResourceView = new TableOpenResourceView(() -> {
		//			try {
		//				return DbUtil.getConnection();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//			return null;
		//		}) {
		//
		//			/* (non-Javadoc)
		//			 * @see com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView#close()
		//			 */
		//			@Override
		//			public void close() {
		//
		//				ResultDialog<Map<String, Object>> result = getResult();
		//
		//				Map<String, Object> data = result.getData();
		//				if (data == null)
		//					return;
		//				Object object = data.get("table_name");
		//				if (object == null)
		//					return;
		//
		//				String string = currentPragraph(textArea);
		//				int markTextColumnIndex = getIndexOfValideWhiteSpace(string);
		//
		//				Paragraph<Collection<String>> currentParagraphRange = currentParagraphRange(textArea);
		//				IndexRange styleRangeAtPosition = currentParagraphRange.getStyleRangeAtPosition(markTextColumnIndex);
		//				IndexRange normalize = IndexRange.normalize(markTextColumnIndex, styleRangeAtPosition.getEnd());
		//				textArea.replaceText(normalize, object.toString());
		//
		//				super.close();
		//				textArea.getPopupWindow().hide();
		//
		//			}
		//
		//		};
		//
		//		textArea.setPopupWindow(new PopOver(tableOpenResourceView.getView()));
		//		textArea.setPopupAlignment(PopupAlignment.SELECTION_TOP_CENTER);
		//		textArea.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
		//
		//			if (ev.isControlDown() && ev.getCode() == KeyCode.SPACE) {
		//
		//				String string = markText(textArea);
		//				tableOpenResourceView.getView().getTxtFilter().setText(string);
		//
		//				textArea.getPopupWindow().show(primaryStage);
		//				textArea.getPopupWindow().requestFocus();
		//
		//				tableOpenResourceView.getView().getTxtFilter().requestFocus();
		//				tableOpenResourceView.getView().getTxtFilter().selectEnd();
		//			}
		//		});
		primaryStage.setScene(new Scene(textArea));
		primaryStage.show();

	}

	//	private int getIndexOfValideWhiteSpace(String string) {
	//		for (int i = string.length() - 1; i >= 0; i--) {
	//			if (Character.isWhitespace(string.charAt(i))) {
	//				return i + 1;
	//			}
	//		}
	//		return -1;
	//	}
	//
	//	private Paragraph<Collection<String>> currentParagraphRange(CodeArea textArea) {
	//		int currentParagraph = textArea.getCurrentParagraph();
	//		return textArea.getDocument().getParagraphs().get(currentParagraph);
	//	}
	//
	//	private String currentPragraph(CodeArea textArea) {
	//		Paragraph<Collection<String>> x = currentParagraphRange(textArea);
	//		return x.toString();
	//	}
	//
	//	private String markText(CodeArea textArea) {
	//		String string = currentPragraph(textArea);
	//		int markTextColumnIndex = getIndexOfValideWhiteSpace(string);
	//		string = string.substring(markTextColumnIndex).trim();
	//		return string;
	//	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 9.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
