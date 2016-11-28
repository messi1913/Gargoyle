/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;

/**
 * 테이블 생성조회문을 보여주기 위한. 추상화 클래스
 *
 * @author KYJ
 *
 */
public abstract class AbstractTableCreateCodeInformationController<T> extends AbstractTableInfomation {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractTableCreateCodeInformationController.class);
	private TableInformationFrameView parent;

	@FXML
	private SqlKeywords txtSql;
	/**
	 * 테이블 create문에 대한 텍스트를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_CREATE_CODE_INFORMATION = TableInformationFrameView.KEY_TABLE_CREATE_CODE_INFORMATION;

	/**
	 * 생성자
	 * @throws Exception
	 */
	public AbstractTableCreateCodeInformationController() throws Exception {
		super(KEY_TABLE_CREATE_CODE_INFORMATION);
	}

	@Override
	public void setParentFrame(TableInformationFrameView parent) {
		this.parent = parent;

	}

	@Override
	public void init() throws Exception {

		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		String databaseName = metadata.getDatabaseName();
		String tableName = metadata.getTableName();

		if (ValueUtil.isEmpty( tableName)) {
			throw new NullPointerException("tableName 이 비었습니다.");
		}

		String sql = getCreateTableSQL(databaseName, tableName);
		if(sql.trim().isEmpty())
			return;

		try {
			List<T> query = this.parent.query(sql, mapper());
			T content = applyContent(query);
			txtSql.setContent(convertString(content));

		} catch (Exception e) {
			txtSql.appendContent(String.format("###### error #######\n%s", e.getMessage()));
			LOGGER.error(ValueUtil.toString(e));
		}
		txtSql.setEditable(false);
	}

	public T applyContent(List<T> result) {
		return result.get(0);
	}

	/**
	 * 결과값을 문자열로 변환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 22.
	 * @param t
	 * @return
	 */
	public abstract String convertString(T t);

	/**
	 * 테이블 생성문을 조회하는 SQL문 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @param databaseName
	 * @param tableName
	 * @return
	 */
	public abstract String getCreateTableSQL(String databaseName, String tableName);

	/**
	 * SQL에 해당하는결과를 바인딩하는 맵퍼 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @return
	 */
	public abstract RowMapper<T> mapper();

	@Override
	public void clear() throws Exception {
		txtSql.setContent("");
	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}
}
