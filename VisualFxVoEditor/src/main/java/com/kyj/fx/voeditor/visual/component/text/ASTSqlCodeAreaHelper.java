/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Map;

import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.PopupAlignment;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.ResourceView;
import com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class ASTSqlCodeAreaHelper extends ASTCodeAreaHelper {

	private CodeArea codeArea;
	private ConnectionSupplier connectionSupplier;
	private ResourceView<Map<String, Object>> view;
	private StringProperty replaceTarget = new SimpleStringProperty();

	public ASTSqlCodeAreaHelper(CodeArea codeArea, ConnectionSupplier connectionSupplier) {
		this.codeArea = codeArea;
		this.connectionSupplier = connectionSupplier;
		view = createResourceView();

		codeArea.setPopupWindow(new PopOver(view));
		codeArea.setPopupAlignment(PopupAlignment.SELECTION_TOP_CENTER);
		codeArea.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {

			if (ev.isControlDown() && ev.getCode() == KeyCode.SPACE) {

				if (ev.isConsumed())
					return;

				String string = markText(codeArea);
				view.getTxtFilter().setText(string);
				replaceTarget.set(string);

				codeArea.getPopupWindow().show(codeArea.getScene().getWindow());
				codeArea.getPopupWindow().requestFocus();

				view.getTxtFilter().requestFocus();
				view.getTxtFilter().selectEnd();

				ev.consume();
			}
		});

	}

	@Override
	public ResourceView<Map<String, Object>> createResourceView() {
		TableOpenResourceView tableOpenResourceView = new TableOpenResourceView(connectionSupplier, new Stage()) {

			/* (non-Javadoc)
			 * @see com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView#close()
			 */
			@Override
			public void close() {

				super.close();
				codeArea.getPopupWindow().hide();

				ResultDialog<Map<String, Object>> result = getResult();

				if (result.getStatus() == ResultDialog.SELECT) {
					Map<String, Object> data = result.getData();
					if (data == null)
						return;
					
					
					String tableName = getTableName(data);
					if(tableName == null)
						return;
//					Object object = data.get("table_name");
//					if (object == null)
//						return;

					String string = currentPragraph(codeArea);
					int markTextColumnIndex = getIndexOfValideWhiteSpace(string);

					if (markTextColumnIndex == -1) {
						codeArea.getUndoManager().mark();
						codeArea.appendText(tableName);
					} else {

						int anchor = codeArea.getAnchor();
						int start = anchor - string.length() + markTextColumnIndex;

						IndexRange normalize = IndexRange.normalize(start, anchor);
						codeArea.getUndoManager().mark();
						codeArea.replaceText(normalize, tableName);
					}
				}

			}

		};

		return tableOpenResourceView.getView();
	}

}
