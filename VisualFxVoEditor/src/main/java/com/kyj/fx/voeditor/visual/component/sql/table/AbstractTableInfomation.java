/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 11. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.layout.BorderPane;

/**
 * 데이터베이스에 대한 메타정보를 보여주기 위한 팝업을 표현하기 위한 추상클래스
 *
 *
 * @author KYJ
 *
 */
public abstract class AbstractTableInfomation extends BorderPane implements ItableInformation {

	protected TableInformationFrameView parent;

	public AbstractTableInfomation(String fxml) throws Exception {
		FxUtil.load(AbstractTableInfomation.class, this, true, fxml, null, null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.ItableInformation#
	 * setParentFrame(com.kyj.fx.voeditor.visual.component.sql.table.
	 * TableInformationFrameView)
	 */
	@Override
	public void setParentFrame(TableInformationFrameView parent) {
		this.parent = parent;
	}

}
