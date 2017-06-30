/**
 * package : com.kyj.fx.voeditor.visual.component.table
 *	fileName : TableInformationFrameManager.java
 *	date      : 2016. 1. 1.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.exceptions.NotSupportException;
import com.kyj.fx.voeditor.visual.util.DbUtil;

/**
 * 기능관리
 *
 * @author KYJ
 *
 */
class TableInformationFrameManager {

	/**
	 * 데이터베이스 접근에 대한 Connection객체를 제공
	 *
	 * @최초생성일 2015. 12. 31.
	 */
	private Supplier<Connection> connectionSupplier;

	/**
	 * 테이블에 정보를 조회하기 위한 사용자 데이터 정보
	 */
	private Supplier<TableInformationUserMetadataVO> metadata;

	public TableInformationFrameManager() {
	}

	public List<Map<String, Object>> query(Connection connection, String sql) throws Exception {
		return DbUtil.select(connection, sql);
	}

	public List<Map<String, Object>> query(Connection connection, String sql,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> converter) throws Exception {
		return DbUtil.select(connection, sql, 10, -1, converter);
	}

	public <T> List<T> query(Connection connection, String sql, RowMapper<T> mapper) throws Exception {
		return DbUtil.selectBeans(connection, sql, 10, mapper);
	}

	public <T> T queryForMeta(DatabaseMetaData metaData, Function<DatabaseMetaData, T> converter) {
		return converter.apply(metaData);
	}

	/**
	 * 커넥션의 Driver명을 리턴
	 *
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws NotSupportException
	 */
	public final String getDriver(Connection connection) throws SQLException, NotSupportException {
		return DbUtil.getDriverNameByConnection(connection);
	}

	/**
	 * @return the connectionSupplier
	 */
	public final Supplier<Connection> getConnectionSupplier() {
		return connectionSupplier;
	}

	/**
	 * @param connectionSupplier
	 *            the connectionSupplier to set
	 */
	public void setConnectionSupplier(Supplier<Connection> connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	/**
	 * @return the metadata
	 */
	public final Supplier<TableInformationUserMetadataVO> getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(Supplier<TableInformationUserMetadataVO> metadata) {
		this.metadata = metadata;
	}

}
