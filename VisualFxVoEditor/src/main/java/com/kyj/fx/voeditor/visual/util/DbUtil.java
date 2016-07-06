/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.kyj.fx.voeditor.visual.exceptions.NotSupportException;
import com.kyj.fx.voeditor.visual.functions.BiTransactionScope;
import com.kyj.fx.voeditor.visual.functions.ResultSetToMapConverter;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

/**
 * jdbc
 *
 * @author KYJ
 *
 */
public class DbUtil extends ConnectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DbUtil.class);

	/**
	 * 쿼리이벤트 수신 클래스 목록
	 *
	 * @최초생성일 2016. 2. 12.
	 */
	private static ConcurrentHashMap<String, DbExecListener> listeners = new ConcurrentHashMap<>();

	public static String[] dmlkeyword;// = { "insert", "update", "delete",
										// "create", "drop", "alter" };

	static {
		String item = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.DML_KEYWORD);
		dmlkeyword = item.split(",");
	}

	/**
	 * 쿼리가 수행되면 메세지를 보낸다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 4.
	 * @param query
	 */
	private static void noticeQuery(String query) {

		// 성능 차이로 인한 극복이 필요한경우에는 아래 주석을 해제하고 사용할것.
		// listeners.values().parallelStream().forEach(listener ->
		// listener.onQuertying(query));

		listeners.values().stream().forEach(listener -> listener.onQuerying(query));
	}

	/**
	 * 쿼리 수행 이벤트 수신을 등록한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 12.
	 * @param listener
	 */
	public static void registQuertyListener(DbExecListener listener) {
		String canonicalName = listener.getClass().getCanonicalName();
		if (!listeners.containsKey(canonicalName))
			listeners.put(canonicalName, listener);
	}

	public static List<Map<String, Object>> select(final String sql) throws Exception {
		Connection con = getConnection();
		return select(con, sql);
	}

	public static List<Map<String, Object>> select(final String sql, int fetchCount, int limitSize,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> convert) throws Exception {
		Connection con = getConnection();
		return select(con, sql, fetchCount, limitSize, convert);
	}

	public static List<Map<String, Object>> select(final String sql,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> convert) throws Exception {
		Connection con = getConnection();
		return select(con, sql, convert);
	}

	public static List<Map<String, Object>> select(Connection con, final String sql) throws Exception {
		return select(con, sql, 10);
	}

	public static List<Map<String, Object>> select(final Connection con, final String sql,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> convert) throws Exception {
		return select(con, sql, 10, -1, convert);
	}

	public static List<Map<String, Object>> selectLimit(final String sql) throws Exception {
		Connection con = getConnection();
		return selectLimit(con, sql, 10, 1000);
	}

	public static List<Map<String, Object>> selectLimit(final String sql, int limitSize) throws Exception {
		Connection con = getConnection();
		return selectLimit(con, sql, 10, limitSize);
	}

	public static List<Map<String, Object>> selectLimit(Connection con, final String sql, int fetchCount, int limitSize) throws Exception {
		return select(con, sql, fetchCount, limitSize, new ResultSetToMapConverter());
	}

	/**
	 * SQL을 실행하고 결과를 반환
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> select(final Connection con, final String sql, int fetchCount) throws Exception {
		return select(con, sql, fetchCount, -1, new ResultSetToMapConverter());
	}

	public static List<Map<String, Object>> select(final Connection con, final String sql, int fetchCount, int limitedSize,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> convert) throws Exception {
		List<Map<String, Object>> arrayList = null;

		try {

			noticeQuery(sql);

			PreparedStatement prepareStatement = null;
			ResultSet executeQuery = null;

			/* 쿼리 타임아웃 시간 설정 SEC */
			// int queryTimeout = getQueryTimeout();

			prepareStatement = con.prepareStatement(sql);
			// postgre-sql can't
			// prepareStatement.setQueryTimeout(queryTimeout);

			if (!(limitedSize <= 0)) {
				prepareStatement.setMaxRows(limitedSize);
			}

			if (fetchCount > 0) {
				prepareStatement.setFetchSize(fetchCount);
			}
			executeQuery = prepareStatement.executeQuery();

			ResultSetMetaData metaData = executeQuery.getMetaData();

			arrayList = convert.apply(metaData, executeQuery);
		} catch (Throwable e) {
			throw e;
		} finally {
			close();
		}

		return arrayList;
	}

	public static <T> List<T> select(final Connection con, final String sql, int fetchCount, RowMapper<T> mapper) throws Exception {
		List<T> resultList = Collections.emptyList();

		try {
			noticeQuery(sql);

			PreparedStatement prepareStatement = null;
			ResultSet executeQuery = null;

			/* 쿼리 타임아웃 시간 설정 SEC */
			// int queryTimeout = getQueryTimeout();

			prepareStatement = con.prepareStatement(sql);
			LOGGER.debug(sql);
			// postgre-sql can't
			// prepareStatement.setQueryTimeout(queryTimeout);

			if (fetchCount > 0) {
				prepareStatement.setFetchSize(fetchCount);
			}
			executeQuery = prepareStatement.executeQuery();

			RowMapperResultSetExtractor<T> rowMapperResultSetExtractor = new RowMapperResultSetExtractor<T>(mapper);
			resultList = rowMapperResultSetExtractor.extractData(executeQuery);
		} catch (Throwable e) {
			throw e;
		} finally {
			close(con);
		}
		return resultList;
	}

	/**
	 * SQL을 실행하고 결과를 반환
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> select(final String sql, Map<String, Object> paramMap, RowMapper<T> rowMapper) throws Exception {

		DataSource dataSource = null;
		List<T> query = null;

		try {

			noticeQuery(sql);

			dataSource = getDataSource();

			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			String _sql = ValueUtil.getVelocityToText(sql, paramMap, true);
			LOGGER.debug(_sql);
			query = jdbcTemplate.query(_sql, new MapSqlParameterSource(paramMap), rowMapper);
		} catch (Exception e) {
			// cleanDataSource();
			// close(dataSource.getConnection());
			throw e;
		} finally {
			close(dataSource);
		}
		return query;
	}

	/**
	 * SQL을 실행하고 결과를 반환
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> select(final String sql, MapSqlParameterSource paramMap, RowMapper<T> rowMapper) throws Exception {

		DataSource dataSource = null;
		List<T> query = null;
		try {

			noticeQuery(sql);

			dataSource = getDataSource();

			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			String _sql = ValueUtil.getVelocityToText(sql, paramMap.getValues());
			query = jdbcTemplate.query(_sql, paramMap, rowMapper);
		} catch (Exception e) {

			// cleanDataSource();
			// close(dataSource);
			LOGGER.debug(ValueUtil.toString(e));
			throw e;
		} finally {
			cleanDataSource();
			// close(dataSource);
		}
		return query;
	}

	/**
	 * SQL을 실행하고 결과를 반환
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> select(final String sql, Map<String, Object> paramMap) throws Exception {

		List<Map<String, Object>> arrayList = null;
		Connection connection = getConnection();
		try {

			noticeQuery(sql);

			PreparedStatement prepareStatement = null;
			ResultSet executeQuery = null;

			/* 쿼리 타임아웃 시간 설정 SEC */
			int queryTimeout = getQueryTimeout();

			prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setQueryTimeout(queryTimeout);

			executeQuery = prepareStatement.executeQuery();
			/*
			 * if (fetchCount > 0) { executeQuery.setFetchSize(fetchCount); }
			 */

			ResultSetMetaData metaData = executeQuery.getMetaData();
			int columnCount = metaData.getColumnCount();
			arrayList = new ArrayList<Map<String, Object>>();

			while (executeQuery.next()) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				for (int c = 1; c <= columnCount; c++) {
					map.put(metaData.getColumnLabel(c), executeQuery.getString(c));
				}
				arrayList.add(map);
			}

		} catch (Exception e) {
			LOGGER.debug(ValueUtil.toString(e));
			throw e;
		} finally {
			close(connection);
		}

		return arrayList;
	}

	public static int update(Connection con, String query) throws Exception {
		try {
			noticeQuery(query);

			PreparedStatement prepareStatement = con.prepareStatement(query);
			return prepareStatement.executeUpdate();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			throw e;
		}
	}

	public static int update(String query) throws Exception {
		Connection con = null;

		try {
			noticeQuery(query);

			con = getConnection();
			PreparedStatement prepareStatement = con.prepareStatement(query);
			return prepareStatement.executeUpdate();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			throw e;
		} finally {
			close(con);
		}
	}

	public static <T> int update(String query, Map<String, Object> map) throws Exception {
		DataSource dataSource = null;
		try {

			noticeQuery(query);

			dataSource = getDataSource();

			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			return jdbcTemplate.update(query, map);
		} catch (Exception e) {
			LOGGER.debug(ValueUtil.toString(e));
			throw e;
		} finally {
			close(dataSource);
		}
	}

	public static <T> void updateList(String query, List<Map<String, Object>> maps) throws Exception {
		DataSource dataSource = null;
		Connection connection = null;
		try {

			noticeQuery(query);

			connection = getDataSource().getConnection();
			connection.setAutoCommit(false);
			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			Map<String, ?>[] array = maps.toArray(new HashMap[maps.size()]);
			jdbcTemplate.batchUpdate(query, array);
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
		} finally {
			close(connection);
		}
	}

	/**
	 * 트랜잭션으로 감싸진 영역을 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param consumer
	 * @throws Exception
	 */
	public static <T> int getTransactionedScope(T userObj, BiTransactionScope<T, NamedParameterJdbcTemplate> consumer) throws Exception {
		return getTransactionedScope(userObj, consumer, null);
	}

	public static <T> int getTransactionedScope(T userObj, BiTransactionScope<T, NamedParameterJdbcTemplate> consumer,
			Consumer<Throwable> exceptionHandler) throws Exception {
		DataSource dataSource = null;
		try {
			dataSource = getDataSource();
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			TransactionTemplate template = new TransactionTemplate();
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
			template.setTransactionManager(transactionManager);

			return template.execute(status -> {
				try {
					consumer.scope(userObj, namedParameterJdbcTemplate);
				} catch (Throwable e) {
					status.setRollbackOnly();
					LOGGER.error(e.getMessage());
					if (exceptionHandler != null)
						exceptionHandler.accept(e);
					return -1;
				}
				return 1;
			});

		} catch (Exception e) {
			throw e;
		} finally {
			close(dataSource);
		}
	}

	public static <T> int getTransactionedScope(Connection con, T userObj, Function<T, List<String>> sqlConverter,
			Consumer<Exception> exceptionHandler) throws Exception {
		int result = -1;
		try {
			con.setAutoCommit(false);
			Statement createStatement = con.createStatement();

			List<String> apply = sqlConverter.apply(userObj);


			for (String sql : apply) {

				if (ValueUtil.isEmpty(sql))
					continue;

				LOGGER.debug(sql);
				createStatement.addBatch(sql);
			}

			int[] executeBatch = createStatement.executeBatch();

			con.commit();
			result = IntStream.of(executeBatch).sum();
		} catch (Exception e) {
			con.rollback();
			exceptionHandler.accept(e);
		} finally {
			con.setAutoCommit(false);
			close(con);
		}
		return result;
	}

	/**
	 * 롤백
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param e
	 * @param connection
	 */
	private static void rollback(Exception e, Connection connection) {
		try {
			e.printStackTrace();
			if (connection != null)
				connection.rollback();
		} catch (SQLException e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}
	}

	/**
	 * 쿼리가 데이터 조작 SQL문인지를 체크한다.
	 *
	 * @param query
	 * @return
	 */
	public static boolean isDml(final String query) {
		if (query != null && !query.trim().isEmpty()) {
			final String sql = getSubLowerCase(query);
			return Arrays.stream(dmlkeyword).anyMatch(keyword -> {
				return sql.startsWith(keyword);
			});
		}
		return false;
	}

	public static boolean isNotDml(final String query) {
		return !isDml(query);
	}

	/**
	 * isDml 함수에서 문자열이 너무 긴경우는 substring으로 앞부분만 잘라 확인함.
	 *
	 * @param sql
	 * @return
	 */
	private static String getSubLowerCase(String sql) {
		String lowerCase = null;
		if (sql.length() > 15)
			lowerCase = sql.trim().substring(0, 15).toLowerCase();
		else
			lowerCase = sql.toLowerCase();
		return lowerCase;
	}

	/**
	 * 커넥션의 Driver명을 리턴
	 *
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws NotSupportException
	 */
	public static String getDriverNameByConnection(Connection connection) throws SQLException, NotSupportException {
		if (connection != null) {
			Driver dbmsDriver = DriverManager.getDriver(connection.getMetaData().getURL());
			if (dbmsDriver != null) {
				Class<? extends Driver> class1 = dbmsDriver.getClass();
				return class1.getName();
			}
		}
		throw new NotSupportException("could not  found DBMS Driver! ");
	}

	/**
	 * Map에 존재하는 값들을 ''가 붙은 형태로 새로 만들어서 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 15.
	 * @param param
	 * @return
	 */
	public static Map<String, Object> replaceDotMapValue(Map<String, Object> param) {
		Iterator<Entry<String, Object>> iterator = param.entrySet().iterator();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		while (iterator.hasNext()) {
			Entry<String, Object> next = iterator.next();
			Object value = null;
			if (next.getValue() != null)
				value = "'".concat(next.getValue().toString()).concat("'");
			map.put(next.getKey(), value);
		}
		return map;
	}

	/**
	 * 데이터베이스 접속 여부 확인 기본 대기시간 : 2초
	 *
	 * @param conSupplier
	 *            커넥션정보를 제공
	 * @param onSuccess
	 *            접속성공시 처리
	 * @param exHandler
	 *            에러발생시 처리
	 * @return 접속 성공여부
	 */
	public static void ping(Supplier<PoolProperties> conSupplier, Consumer<Boolean> onSuccess, Consumer<Throwable> exHandler) {
		ping(conSupplier, onSuccess, exHandler, 3);
	}

	/**
	 * 데이터베이스 접속 여부 확인
	 *
	 * @param conSupplier
	 *            커넥션정보를 제공
	 * @param onSuccess
	 *            접속성공시 처리
	 * @param exHandler
	 *            에러발생시 처리
	 * @param timeoutSec
	 *            핑테스트 대기시간 (Secound)
	 * @return 접속 성공여부
	 */
	public static void ping(Supplier<PoolProperties> conSupplier, Consumer<Boolean> onSuccess, Consumer<Throwable> exHandler,
			int timeoutSec) {

		boolean result = false;
		PoolProperties prop = conSupplier.get();

		String driverClassName = prop.getDriverClassName();
		String url = prop.getUrl();
		String userName = prop.getUsername();
		String password = prop.getPassword();

		try (Connection connection = getConnection(driverClassName, url, userName, password, timeoutSec)) {

			String pingSQL = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_PING, driverClassName);

			// 리스너들에게 공지
			noticeQuery(pingSQL);

			if (connection != null) {
				// connection.setAutoCommit(false);
				// try {
				// // postgresql was not supported...
				// Oracle에서 관련 에러 발생.. Connection객체를 얻어오는 과정과
				// executeQuery처리결과만으로 판단
				// connection.setNetworkTimeout(Executors.newSingleThreadExecutor(),
				// 5000);
				// } catch (Exception e) {
				//
				// }
				ResultSet executeQuery = connection.createStatement().executeQuery(pingSQL);
				result = executeQuery.next();
			}
		} catch (Throwable e) {
			exHandler.accept(e);
		}

		onSuccess.accept(result);

	}

	// TODO 구현가능한부분인지 확인.
	// public void cancel(Connection activeConnection) {
	// try {
	// if (activeConnection != null) {
	// if (!activeConnection.isClosed()) {
	//
	// activeConnection.abort(new Executor() {
	//
	// @Override
	// public void execute(Runnable command) {
	// LOGGER.debug("abort.....");
	// }
	// });
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
