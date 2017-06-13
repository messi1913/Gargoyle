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
	private TableInformationFrameView frame;

	@FXML
	protected SqlKeywords txtSql;
	/**
	 * 테이블 create문에 대한 텍스트를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_CREATE_CODE_INFORMATION = TableInformationFrameView.KEY_TABLE_CREATE_CODE_INFORMATION;
	private String databaseName;
	private String tableName;

	/**
	 * 생성자
	 * @throws Exception
	 */
	public AbstractTableCreateCodeInformationController() throws Exception {
		super(KEY_TABLE_CREATE_CODE_INFORMATION);
	}

	@Override
	public void setParentFrame(TableInformationFrameView frame) {
		this.frame = frame;

	}

	@Override
	public void init() throws Exception {

		TableInformationUserMetadataVO metadata = this.frame.getMetadata();
		databaseName = metadata.getDatabaseName();
		tableName = metadata.getTableName();

		if (ValueUtil.isEmpty(tableName)) {
			throw new NullPointerException("tableName 이 비었습니다.");
		}

		if (isEmbeddedSupport()) {
			T embeddedScript = getEmbeddedScript();
			txtSql.setContent(convertString(embeddedScript));
		} else {
			String sql = getCreateTableSQL(databaseName, tableName);
			if (sql.trim().isEmpty())
				return;

			try {
				List<T> query = this.frame.query(sql, mapper());
				T content = applyContent(query);
				txtSql.setContent(convertString(content));

			} catch (Exception e) {
				txtSql.appendContent(String.format("###### error #######\n%s", e.getMessage()));
				LOGGER.error(ValueUtil.toString(e));
			}
		}
//		txtSql.moveToLine(1);
		txtSql.setEditable(false);
	}

	@Override
	public void postInit() {

	}

	/**
	 * 특화처리하여 SQL문을 제공하는 경우 true
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 */
	protected abstract boolean isEmbeddedSupport();

	/**
	 * 특화처리하여 SQL문을 제공
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 13. 
	 * @return
	 */
	protected abstract T getEmbeddedScript();

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
	
	public void setTextSql(String sql){
		txtSql.setContent(sql);
	}
	
	

	@Override
	public String getDbmsDriver() {
		return this.frame.getDbmsDriver();
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getTableName() {
		return tableName;
	}

	public TableInformationFrameView getFrame() {
		return this.frame;
	}
}
