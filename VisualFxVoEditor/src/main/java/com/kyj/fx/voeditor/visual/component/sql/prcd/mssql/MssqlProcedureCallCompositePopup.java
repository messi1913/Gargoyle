/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd.mssql
 *	작성일   : 2017. 12. 1.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd.mssql;

import java.util.Optional;

import com.kyj.fx.voeditor.visual.component.sql.prcd.commons.ProcedureCallComposite;
import com.kyj.fx.voeditor.visual.framework.keyboard.FxKey;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class MssqlProcedureCallCompositePopup<T> {

	private ProcedureCallComposite<T> node;
	private Stage parent;

	/**
	 */
	public MssqlProcedureCallCompositePopup(Stage parent, ProcedureCallComposite<T> node) {
		this.parent = parent;
		this.node = node;
	}

	private Stage stage;

	public Optional<T> getResult() {
		T result = this.node.getResult();
		return result == null ? Optional.empty() : Optional.of(result);
	}

	public void show() {
		stage = new Stage();
		stage.setTitle(" MSSQL Procedure Call ");
		stage.initOwner(this.parent);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setAlwaysOnTop(true);
		stage.addEventHandler(KeyEvent.KEY_RELEASED, this::stageOnKeyReleaseEvent);
		stage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 1.
	 * @param e
	 */
	public void stageOnKeyReleaseEvent(KeyEvent e) {
		if (FxKey.isSingleClick(e, KeyCode.ESCAPE)) {
			if (stage != null)
				stage.close();
		}

	}

}
