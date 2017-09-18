/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Map;

import org.fxmisc.richtext.CodeArea;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.MssqlTableOpenResourceView;
import com.kyj.fx.voeditor.visual.component.popup.ResourceView;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.control.IndexRange;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class MssqlASTSqlCodeAreaHelper extends ASTSqlCodeAreaHelper {

	public MssqlASTSqlCodeAreaHelper(CodeArea codeArea, ConnectionSupplier connectionSupplier) {
		super(codeArea, connectionSupplier);
	}

	@Override
	public ResourceView<Map<String, Object>> createResourceView() {
		ConnectionSupplier connectionSupplier = getConnectionSupplier();

		MssqlTableOpenResourceView tableOpenResourceView = new MssqlTableOpenResourceView(connectionSupplier, new Stage()) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.kyj.fx.voeditor.visual.component.popup.TableOpenResourceView#
			 * close()
			 */
			@Override
			public void close() {
				CodeArea codeArea = getCodeArea();
				codeArea.getPopupWindow().hide();

				ResultDialog<Map<String, Object>> result = getResult();

				if (result.getStatus() == ResultDialog.SELECT) {
					Map<String, Object> data = result.getData();
					if (data == null)
						return;

					String tableName = getTableName(data);
					if (tableName == null)
						return;
					
					Object _tbCat = data.get("TABLE_CAT");
					if(ValueUtil.isNotEmpty(_tbCat))
					{
						tableName = String.format("%s.%s", _tbCat.toString(), tableName);
					}
					
					// Object object = data.get("table_name");
					// if (object == null)
					// return;

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
