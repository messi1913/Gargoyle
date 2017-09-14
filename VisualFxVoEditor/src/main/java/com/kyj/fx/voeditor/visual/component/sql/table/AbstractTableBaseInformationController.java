/**
 * package : com.kyj.fx.voeditor.visual.component.table
 *	fileName : TableBaseInformationController.java
 *	date      : 2016. 1. 1.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * 테이블에 이름 및 코멘트에 대한 정보를 보여준다.
 *
 * @author KYJ
 *
 */
public abstract class AbstractTableBaseInformationController extends AbstractTableInfomation{

	public static final String KEY_TABLE_BASE_INFORMATION = TableInformationFrameView.KEY_TABLE_BASE_INFORMATION;

	private TableInformationFrameView parent;

	@FXML
	private TextField txtTableName;

	@FXML
	private TextArea txtComments;

	/**
	 * 생성자 fxml을 로드한다.
	 *
	 * @throws Exception
	 */
	public AbstractTableBaseInformationController() throws Exception {
		super(KEY_TABLE_BASE_INFORMATION);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.ItableInformation#
	 * setParentFrame
	 * (com.kyj.fx.voeditor.visual.component.table.TableInformationFrameView)
	 */
	@Override
	public void setParentFrame(TableInformationFrameView tableInformationFrameView) {
		this.parent = tableInformationFrameView;
	}

	/**
	 * 테이블 코멘트정보를 조회할 SQL을 리턴함.
	 *
	 * @return
	 */
	public abstract String getTableCommentSQL(String databaseName, String tableName) throws Exception;

	/**
	 * 데이터베이스 조회 결과를 파라미터로 넘겨주며 그중에서 테이블 코멘트의 내용을 리턴함.
	 *
	 * @param resultList
	 * @return
	 */
	public abstract String getTableComment(List<Map<String, Object>> resultList);

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.ItableInformation#init()
	 */
	public void init() throws Exception {
		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		String databaseName = metadata.getDatabaseName();
		String tableName = metadata.getTableName();

		if (ValueUtil.isEmpty(tableName)) {
			throw new NullPointerException("tableName 이 비었습니다.");
		}

		this.txtTableName.setText(tableName);

		String sql = getTableCommentSQL(databaseName, tableName);
		if (sql != null) {
			List<Map<String, Object>> query = this.parent.query(sql);
			String comment = getTableComment(query);
			txtComments.setText(comment);
		}

	}

	@Override
	public void clear() throws Exception {
		txtTableName.clear();
		txtComments.clear();
	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}

}
