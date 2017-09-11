/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.kyj.fx.voeditor.visual.exceptions.GargoyleException;
import com.kyj.fx.voeditor.visual.exceptions.NotSupportException;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
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
	/**
	 * @최초생성일 2016. 8. 4.
	 */
	public static final int DEFAULT_FETCH_SIZE = 1000;

	/**
	 * @최초생성일 2016. 9. 1.
	 */
	public static final int DEFAULT_LIMIT_ROW_COUNT = 1000;

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

	static BiFunction<Connection, String, PreparedStatement> DEFAULT_PREPAREDSTATEMENT_CONVERTER = (c, sql) -> {
		try {
			return c.prepareStatement(sql);
		} catch (Exception e) {
			throw new RuntimeException(e);
			// return null;
		}
	};

	static BiFunction<Connection, String, PreparedStatement> READ_ONLY_CURSOR_PREPAREDSTATEMENT_CONVERTER = (c,
			sql) -> {
		try {
			return c.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			return null;
		}
	};

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

	public static <T> List<Map<String, T>> select(final String sql, int fetchCount, int limitSize,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, T>>> convert) throws Exception {
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
		return selectLimit(con, sql, 10, DEFAULT_FETCH_SIZE);
	}

	public static List<Map<String, Object>> selectLimit(final String sql, int limitSize) throws Exception {
		Connection con = getConnection();
		return selectLimit(con, sql, 10, limitSize);
	}

	public static List<Map<String, Object>> selectLimit(Connection con, final String sql, int fetchCount, int limitSize)
			throws Exception {
		return select(con, sql, fetchCount, limitSize, new ResultSetToMapConverter());
	}

	/**
	 * SQL을 실행하고 결과를 반환
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> select(final Connection con, final String sql, int fetchCount)
			throws Exception {
		return select(con, sql, fetchCount, -1, new ResultSetToMapConverter());
	}

	public static Map<String, Object> findOne(final Connection con, final String sql) throws Exception {
		List<Map<String, Object>> select = select(con, sql, 1, 1, new ResultSetToMapConverter());
		return select.isEmpty() ? Collections.emptyMap() : select.get(0);
	}

	public static <T> T findOne(Connection con, String sql, Class<T> clazz) throws Exception {
		List<T> select = selectBeans(con, sql, 1, 1, createBeanRowMapper(clazz));
		return select.isEmpty() ? null : select.get(0);
	}

	public static List<Map<String, Object>> selectCursor(final Connection con, final String sql, int startRow)
			throws Exception {
		return selectCursor(con, sql, startRow, -1);
	}

	public static List<Map<String, Object>> selectCursor(final Connection con, final String sql, int startRow,
			int limitRow) throws Exception {
		Properties properties = new Properties();
		properties.put(ResultSetToMapConverter.START_ROW, --startRow);
		return select(con, sql, DEFAULT_FETCH_SIZE, limitRow, READ_ONLY_CURSOR_PREPAREDSTATEMENT_CONVERTER,
				new ResultSetToMapConverter(properties));
	}

	public static <T> List<Map<String, T>> select(final Connection con, final String sql, int fetchCount,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, T>>> convert) throws Exception {
		return select(con, sql, fetchCount, -1, DEFAULT_PREPAREDSTATEMENT_CONVERTER, convert);
	}

	public static <T> List<Map<String, T>> select(final Connection con, final String sql, int fetchCount,
			int limitedSize, BiFunction<ResultSetMetaData, ResultSet, List<Map<String, T>>> convert) throws Exception {
		return select(con, sql, fetchCount, limitedSize, DEFAULT_PREPAREDSTATEMENT_CONVERTER, convert);
	}

	public static <T> List<Map<String, T>> select(final Connection con, final String sql, int fetchCount,
			int limitedSize, BiFunction<Connection, String, PreparedStatement> prestatementConvert,
			BiFunction<ResultSetMetaData, ResultSet, List<Map<String, T>>> convert) throws Exception {
		List<Map<String, T>> arrayList = Collections.emptyList();

		try {

			noticeQuery(sql);

			PreparedStatement prepareStatement = null;
			ResultSet executeQuery = null;

			/* 쿼리 타임아웃 시간 설정 SEC */
			// int queryTimeout = getQueryTimeout();

			prepareStatement = prestatementConvert.apply(con, sql); // con.prepareStatement(sql);
			// postgre-sql can't
			// prepareStatement.setQueryTimeout(queryTimeout);
			if (prepareStatement != null) {
				if (!(limitedSize <= 0)) {
					prepareStatement.setMaxRows(limitedSize);
				}

				if (fetchCount > 0) {
					prepareStatement.setFetchSize(fetchCount);
				}
				executeQuery = prepareStatement.executeQuery();

				ResultSetMetaData metaData = executeQuery.getMetaData();

				arrayList = convert.apply(metaData, executeQuery);
			}

		} catch (Throwable e) {
			throw e;
		}
		// finally {
		// close();
		// }

		return arrayList;
	}

	public static <T> List<T> selectBeans(final Connection con, String sql, final Class<T> bean) throws Exception {
		RowMapper<T> createBeanRowMapper = createBeanRowMapper(bean);
		return selectBeans(con, sql, 30, -1, createBeanRowMapper);
	}

	public static <T> List<T> selectBeans(final Connection con, String sql, int fetchCount, final Class<T> bean)
			throws Exception {
		RowMapper<T> createBeanRowMapper = createBeanRowMapper(bean);
		return selectBeans(con, sql, fetchCount, -1, createBeanRowMapper);
	}

	public static <T> List<T> selectBeans(final Connection con, final String sql, int fetchCount, RowMapper<T> mapper)
			throws Exception {
		return selectBeans(con, sql, fetchCount, -1, mapper);
	}

	public static <T> List<T> selectBeans(final Connection con, final String sql, int fetchCount, int limitedSize,
			RowMapper<T> mapper) throws Exception {
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
			if (!(limitedSize <= 0)) {
				prepareStatement.setMaxRows(limitedSize);
			}

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

	public static <T> List<T> select(final String sql, Map<String, Object> paramMap, RowMapper<T> rowMapper)
			throws Exception {
		DataSource dataSource = null;
		List<T> query = null;
		try {
			dataSource = getDataSource();
			query = select(dataSource, sql, paramMap, rowMapper);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			query = Collections.emptyList();
		} finally {
			close(dataSource);
		}
		return query;
	}

	public static <T> List<T> select(DataSource dataSource, final String sql, Map<String, Object> paramMap,
			RowMapper<T> rowMapper) throws Exception {
		List<T> query = null;
		try {
			noticeQuery(sql);
			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			// String _sql = ValueUtil.getVelocityToText(sql, paramMap);
			// LOGGER.debug(_sql);
			query = jdbcTemplate.query(sql, new MapSqlParameterSource(paramMap), rowMapper);
		} catch (Exception e) {
			throw e;
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
	public static <T> List<T> select(final String sql, MapSqlParameterSource paramMap, RowMapper<T> rowMapper)
			throws Exception {

		DataSource dataSource = null;
		List<T> query = null;
		try {

			noticeQuery(sql);

			dataSource = getDataSource();

			NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			// String _sql = ValueUtil.getVelocityToText(sql,
			// paramMap.getValues());

			query = jdbcTemplate.query(sql, paramMap, rowMapper);
		} catch (Exception e) {

			// cleanDataSource();
			// close(dataSource);
			LOGGER.error(ValueUtil.toString(e));
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

		return getTransactionedScope(null, new BiTransactionScope<T, NamedParameterJdbcTemplate>() {

			@Override
			public int scope(T t, NamedParameterJdbcTemplate u) throws Exception {
				return u.update(query, map);
			}
		});
	}

	/**
	 * API 수정.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 6.
	 * @param query
	 * @param maps
	 * @return
	 * @throws Exception
	 */
	public static <T> int updateList(String query, List<Map<String, Object>> maps) throws Exception {

		return getTransactionedScope(null, new BiTransactionScope<T, NamedParameterJdbcTemplate>() {

			@Override
			public int scope(T t, NamedParameterJdbcTemplate u) throws Exception {

				@SuppressWarnings("unchecked")
				Map<String, ?>[] array = maps.toArray(new HashMap[maps.size()]);
				noticeQuery(query);
				int[] batchUpdate = u.batchUpdate(query, array);
				return IntStream.of(batchUpdate).sum();
			}
		});

		// DataSource dataSource = null;
		// Connection connection = null;
		// try {
		//
		// noticeQuery(query);
		//
		// connection = getDataSource().getConnection();
		// connection.setAutoCommit(false);
		// NamedParameterJdbcTemplate jdbcTemplate = new
		// NamedParameterJdbcTemplate(dataSource);
		// Map<String, ?>[] array = maps.toArray(new HashMap[maps.size()]);
		// jdbcTemplate.batchUpdate(query, array);
		// connection.commit();
		// } catch (Exception e) {
		// connection.rollback();
		// e.printStackTrace();
		// } finally {
		// close(connection);
		// }

	}

	/**
	 * 트랜잭션으로 감싸진 영역을 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param consumer
	 * @throws Exception
	 */
	public static <T> int getTransactionedScope(T userObj, BiTransactionScope<T, NamedParameterJdbcTemplate> consumer) {
		return getTransactionedScope(userObj, consumer, null);
	}

	public static <T> int getTransactionedScope(T userObj, BiTransactionScope<T, NamedParameterJdbcTemplate> consumer,
			Consumer<Exception> exceptionHandler) {
		try {
			return getTransactionedScope(getDataSource(), userObj, consumer, exceptionHandler);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			if (exceptionHandler != null)
				exceptionHandler.accept(e);
		}
		return -1;
	}

	public static <T> int getTransactionedScope(DataSource dataSource, T userObj,
			BiTransactionScope<T, NamedParameterJdbcTemplate> consumer, Consumer<Exception> exceptionHandler) {
		// DataSource dataSource = null;
		try {
			// dataSource = getDataSource();
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

			TransactionTemplate template = new TransactionTemplate();
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
			template.setTransactionManager(transactionManager);

			return template.execute(status -> {
				int result = -1;
				try {
					result = consumer.scope(userObj, namedParameterJdbcTemplate);
				} catch (Exception e) {
					status.setRollbackOnly();
					LOGGER.error(ValueUtil.toString(e));
					if (exceptionHandler != null)
						exceptionHandler.accept(e);
					result = -1;
				}
				return result;
			});

		} catch (Exception e) {
			exceptionHandler.accept(e);
		} finally {
			try {
				close(dataSource);
			} catch (Exception e) {
			}
		}
		return -1;
	}

	public static <T> int getTransactionedScope(Connection con, T userObj, Function<T, List<String>> sqlConverter,
			Consumer<Exception> exceptionHandler) throws Exception {
		int result = -1;
		try {
			LOGGER.debug("is AutoCommit ? : {}", con.getAutoCommit());
			con.setAutoCommit(false);
			List<String> apply = sqlConverter.apply(userObj);
			Statement createStatement = con.createStatement();
			for (String sql : apply) {

				/*
				 * sqlite에서 공백이 포함된 sql은 add한경우 에러. 확인해보니 isEmpty함수에 이상이 있는듯하여 수정.
				 */
				if (ValueUtil.isEmpty(sql))
					continue;

				LOGGER.debug(sql);
				createStatement.addBatch(sql);
			}

			int[] executeBatch = createStatement.executeBatch();

			con.commit();
			result = (int) IntStream.of(executeBatch).filter(v -> v == 0).count();
		} catch (Exception e) {
			con.rollback();
			exceptionHandler.accept(e);
			result = -1;
		} finally {
			con.commit();
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
		if (connection == null)
			throw new NullPointerException("Connection is null");

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
	public static void ping(Supplier<PoolProperties> conSupplier, Consumer<Boolean> onSuccess,
			Consumer<Throwable> exHandler) {
		ping(conSupplier, onSuccess, exHandler, 1, false);
	}
	
	public static void pingAsync(Supplier<PoolProperties> conSupplier, Consumer<Boolean> onSuccess,
			Consumer<Throwable> exHandler) {
		ping(conSupplier, onSuccess, exHandler, 1, true);
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
	public static void ping(Supplier<PoolProperties> conSupplier, Consumer<Boolean> onSuccess,
			Consumer<Throwable> exHandler, int timeoutSec, boolean async) {

//		boolean result = false;
		PoolProperties prop = conSupplier.get();

		String driverClassName = prop.getDriverClassName();
		String url = prop.getUrl();
		String userName = prop.getUsername();
		String password = prop.getPassword();

		if (async) {
			ExecutorDemons.getGargoyleSystemExecutorSerivce().execute(() -> {
				ping(onSuccess, exHandler, timeoutSec, driverClassName, url, userName, password);
			});
		} else {
			ping(onSuccess, exHandler, timeoutSec, driverClassName, url, userName, password);
		}

	}

	private static void ping(Consumer<Boolean> onSuccess, Consumer<Throwable> exHandler, int timeoutSec,
			String driverClassName, String url, String userName, String password) {
		boolean result;
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
				onSuccess.accept(result);
			} else
				exHandler.accept(new Exception("Connection create fail."));
		} catch (Throwable e) {
			exHandler.accept(e);
		}
	}

	/********************************
	 * 작성일 : 2016. 8. 11. 작성자 : KYJ
	 *
	 *
	 * @return
	 * @throws Exception
	 ********************************/
	public static List<String> tables(String tableNamePattern) throws Exception {
		return tables(tableNamePattern, rs -> {
			try {
				return rs.getString(3);
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			return "";
		});
	}

	/********************************
	 * 작성일 : 2016. 8. 11. 작성자 : KYJ
	 *
	 * 2016-11-10 모든 테이블탐색후 대소문자무시 검색으로 수정 </br>
	 * </br>
	 * 1.TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM
	 * TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". </br>
	 * 5.REMARKS String => explanatory comment on the table </br>
	 * 6.TYPE_CAT String => the types catalog (may be null) </br>
	 * 7.TYPE_SCHEM String => the types schema (may be null) </br>
	 * 8.TYPE_NAME String => type name (may be null) </br>
	 * 9.SELF_REFERENCING_COL_NAME String => name of the designated "identifier"
	 * column of a typed table (may be null) </br>
	 * 10.REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME
	 * are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) </br>
	 * 
	 * @param converter
	 * @return
	 * @throws Exception
	 ********************************/
	public static <T> List<T> tables(String tableNamePattern, Function<ResultSet, T> converter) throws Exception {
		try (Connection connection = getConnection()) {
			return tables(connection, tableNamePattern, converter);
		}
	}

	/********************************
	 * 작성일 : 2016. 8. 11. 작성자 : KYJ
	 *
	 * 2016-11-10 모든 테이블탐색후 대소문자무시 검색으로 수정 2017-07-12 Connection을 파라미터로 넣어 동적으로 찾을 수
	 * 있게 수정 </br>
	 * </br>
	 * 1.TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.TABLE_TYPE String => table type. Typical types are "TABLE", "VIEW", "SYSTEM
	 * TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". </br>
	 * 5.REMARKS String => explanatory comment on the table </br>
	 * 6.TYPE_CAT String => the types catalog (may be null) </br>
	 * 7.TYPE_SCHEM String => the types schema (may be null) </br>
	 * 8.TYPE_NAME String => type name (may be null) </br>
	 * 9.SELF_REFERENCING_COL_NAME String => name of the designated "identifier"
	 * column of a typed table (may be null) </br>
	 * 10.REF_GENERATION String => specifies how values in SELF_REFERENCING_COL_NAME
	 * are created. Values are "SYSTEM", "USER", "DERIVED". (may be null) </br>
	 * 
	 * @param connection
	 * @param converter
	 * @return
	 * @throws Exception
	 ********************************/
	public static <T> List<T> tables(Connection connection, String tableNamePattern, Function<ResultSet, T> converter)
			throws Exception {
		if (converter == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		List<T> tables = new ArrayList<>();

		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = metaData.getTables(null, null, "%"/* + tableNamePattern + "%" */, new String[] { "TABLE", });

		String tableNamePatternUpperCase = tableNamePattern.toUpperCase();
		while (rs.next()) {

			// 2016-08-18 특정데이터베이스(sqlite)에서는 인덱스 트리거정보도 동시에 출력된다.
			String tableType = rs.getString(4);
			if ("TABLE".equals(tableType)) {

				String tableName = rs.getString(3);
				if (tableName.toUpperCase().indexOf(tableNamePatternUpperCase) != -1) {
					T apply = converter.apply(rs);
					if (apply != null)
						tables.add(apply);
				}

			}

		}

		return tables;
	}

	public static List<String> columns(String tableNamePattern) throws Exception {
		try (Connection con = getConnection()) {
			return columns(con, tableNamePattern, t -> {
				try {
					return t.getString(4);
				} catch (SQLException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
				return "";
			});
		}
	}

	/**
	 *
	 * 1. TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.COLUMN_NAME String => column name </br>
	 * 5.DATA_TYPE int => SQL type from java.sql.Types </br>
	 * 6.TYPE_NAME String => Data source dependent type name, for a UDT the type
	 * name is fully qualified </br>
	 * 7.COLUMN_SIZE int => column size. </br>
	 * 8.BUFFER_LENGTH is not used. </br>
	 * 9.DECIMAL_DIGITS int => the number of fractional digits. Null is returned for
	 * data types where DECIMAL_DIGITS is not applicable. </br>
	 * 10.NUM_PREC_RADIX int => Radix (typically either 10 or 2) </br>
	 * 11.NULLABLE int => is NULL allowed. ◦ columnNoNulls - might not allow NULL
	 * values </br>
	 * ◦ columnNullable - definitely allows NULL values </br>
	 * ◦ columnNullableUnknown - nullability unknown </br>
	 * </br>
	 * 12.REMARKS String => comment describing column (may be null) </br>
	 * 13.COLUMN_DEF String => default value for the column, which should be
	 * interpreted as a string when the value is enclosed in single quotes (may be
	 * null) </br>
	 * 14.SQL_DATA_TYPE int => unused </br>
	 * 15.SQL_DATETIME_SUB int => unused </br>
	 * 16.CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the
	 * column </br>
	 * 17.ORDINAL_POSITION int => index of column in table (starting at 1) </br>
	 * 18.IS_NULLABLE String => ISO rules are used to determine the nullability for
	 * a column. ◦ YES --- if the column can include NULLs </br>
	 * ◦ NO --- if the column cannot include NULLs </br>
	 * ◦ empty string --- if the nullability for the column is unknown </br>
	 * </br>
	 * 19.SCOPE_CATALOG String => catalog of table that is the scope of a reference
	 * attribute (null if DATA_TYPE isn't REF) </br>
	 * 20.SCOPE_SCHEMA String => schema of table that is the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 21.SCOPE_TABLE String => table name that this the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 22.SOURCE_DATA_TYPE short => source type of a distinct type or user-generated
	 * Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or
	 * user-generated REF) </br>
	 * 23.IS_AUTOINCREMENT String => Indicates whether this column is auto
	 * incremented ◦ YES --- if the column is auto incremented </br>
	 * ◦ NO --- if the column is not auto incremented </br>
	 * ◦ empty string --- if it cannot be determined whether the column is auto
	 * incremented </br>
	 * </br>
	 * 24.IS_GENERATEDCOLUMN String => Indicates whether this is a generated column
	 * ◦ YES --- if this a generated column </br>
	 * ◦ NO --- if this not a generated column </br>
	 * ◦ empty string --- if it cannot be determined whether this is a generated
	 * column </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 10.
	 * @param tableNamePattern
	 * @param converter
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> columns(String tableNamePattern, Function<ResultSet, T> converter) throws Exception {

		try (Connection con = getConnection()) {
			return columns(con, tableNamePattern, converter);
		}
	}

	/**
	 * 1. TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.COLUMN_NAME String => column name </br>
	 * 5.DATA_TYPE int => SQL type from java.sql.Types </br>
	 * 6.TYPE_NAME String => Data source dependent type name, for a UDT the type
	 * name is fully qualified </br>
	 * 7.COLUMN_SIZE int => column size. </br>
	 * 8.BUFFER_LENGTH is not used. </br>
	 * 9.DECIMAL_DIGITS int => the number of fractional digits. Null is returned for
	 * data types where DECIMAL_DIGITS is not applicable. </br>
	 * 10.NUM_PREC_RADIX int => Radix (typically either 10 or 2) </br>
	 * 11.NULLABLE int => is NULL allowed. ◦ columnNoNulls - might not allow NULL
	 * values </br>
	 * ◦ columnNullable - definitely allows NULL values </br>
	 * ◦ columnNullableUnknown - nullability unknown </br>
	 * </br>
	 * 12.REMARKS String => comment describing column (may be null) </br>
	 * 13.COLUMN_DEF String => default value for the column, which should be
	 * interpreted as a string when the value is enclosed in single quotes (may be
	 * null) </br>
	 * 14.SQL_DATA_TYPE int => unused </br>
	 * 15.SQL_DATETIME_SUB int => unused </br>
	 * 16.CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the
	 * column </br>
	 * 17.ORDINAL_POSITION int => index of column in table (starting at 1) </br>
	 * 18.IS_NULLABLE String => ISO rules are used to determine the nullability for
	 * a column. ◦ YES --- if the column can include NULLs </br>
	 * ◦ NO --- if the column cannot include NULLs </br>
	 * ◦ empty string --- if the nullability for the column is unknown </br>
	 * </br>
	 * 19.SCOPE_CATALOG String => catalog of table that is the scope of a reference
	 * attribute (null if DATA_TYPE isn't REF) </br>
	 * 20.SCOPE_SCHEMA String => schema of table that is the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 21.SCOPE_TABLE String => table name that this the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 22.SOURCE_DATA_TYPE short => source type of a distinct type or user-generated
	 * Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or
	 * user-generated REF) </br>
	 * 23.IS_AUTOINCREMENT String => Indicates whether this column is auto
	 * incremented ◦ YES --- if the column is auto incremented </br>
	 * ◦ NO --- if the column is not auto incremented </br>
	 * ◦ empty string --- if it cannot be determined whether the column is auto
	 * incremented </br>
	 * </br>
	 * 24.IS_GENERATEDCOLUMN String => Indicates whether this is a generated column
	 * ◦ YES --- if this a generated column </br>
	 * ◦ NO --- if this not a generated column </br>
	 * ◦ empty string --- if it cannot be determined whether this is a generated
	 * column </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 10.
	 * @param con
	 * @param tableNamePattern
	 * @return
	 * @throws Exception
	 */
	public static List<String> columns(Connection con, String tableNamePattern) throws Exception {
		return columns(con, tableNamePattern, t -> {
			try {
				return t.getString(4);
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			return "";
		});
	}

	/**
	 * 1. TABLE_CAT String => table catalog (may be null) </br>
	 * 2.TABLE_SCHEM String => table schema (may be null) </br>
	 * 3.TABLE_NAME String => table name </br>
	 * 4.COLUMN_NAME String => column name </br>
	 * 5.DATA_TYPE int => SQL type from java.sql.Types </br>
	 * 6.TYPE_NAME String => Data source dependent type name, for a UDT the type
	 * name is fully qualified </br>
	 * 7.COLUMN_SIZE int => column size. </br>
	 * 8.BUFFER_LENGTH is not used. </br>
	 * 9.DECIMAL_DIGITS int => the number of fractional digits. Null is returned for
	 * data types where DECIMAL_DIGITS is not applicable. </br>
	 * 10.NUM_PREC_RADIX int => Radix (typically either 10 or 2) </br>
	 * 11.NULLABLE int => is NULL allowed. ◦ columnNoNulls - might not allow NULL
	 * values </br>
	 * ◦ columnNullable - definitely allows NULL values </br>
	 * ◦ columnNullableUnknown - nullability unknown </br>
	 * </br>
	 * 12.REMARKS String => comment describing column (may be null) </br>
	 * 13.COLUMN_DEF String => default value for the column, which should be
	 * interpreted as a string when the value is enclosed in single quotes (may be
	 * null) </br>
	 * 14.SQL_DATA_TYPE int => unused </br>
	 * 15.SQL_DATETIME_SUB int => unused </br>
	 * 16.CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the
	 * column </br>
	 * 17.ORDINAL_POSITION int => index of column in table (starting at 1) </br>
	 * 18.IS_NULLABLE String => ISO rules are used to determine the nullability for
	 * a column. ◦ YES --- if the column can include NULLs </br>
	 * ◦ NO --- if the column cannot include NULLs </br>
	 * ◦ empty string --- if the nullability for the column is unknown </br>
	 * </br>
	 * 19.SCOPE_CATALOG String => catalog of table that is the scope of a reference
	 * attribute (null if DATA_TYPE isn't REF) </br>
	 * 20.SCOPE_SCHEMA String => schema of table that is the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 21.SCOPE_TABLE String => table name that this the scope of a reference
	 * attribute (null if the DATA_TYPE isn't REF) </br>
	 * 22.SOURCE_DATA_TYPE short => source type of a distinct type or user-generated
	 * Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or
	 * user-generated REF) </br>
	 * 23.IS_AUTOINCREMENT String => Indicates whether this column is auto
	 * incremented ◦ YES --- if the column is auto incremented </br>
	 * ◦ NO --- if the column is not auto incremented </br>
	 * ◦ empty string --- if it cannot be determined whether the column is auto
	 * incremented </br>
	 * </br>
	 * 24.IS_GENERATEDCOLUMN String => Indicates whether this is a generated column
	 * ◦ YES --- if this a generated column </br>
	 * ◦ NO --- if this not a generated column </br>
	 * ◦ empty string --- if it cannot be determined whether this is a generated
	 * column </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 10.
	 * @param connection
	 * @param tableNamePattern
	 * @param converter
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> columns(Connection connection, String tableNamePattern, Function<ResultSet, T> converter)
			throws Exception {
		if (converter == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		List<T> tables = new ArrayList<>();
		// try (Connection connection = getConnection()) {

		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = COLUMN_CONVERTER.apply(tableNamePattern, metaData); // metaData.getColumns(null,
																			// null,
																			// tableNamePattern,
																			// null);

		while (rs.next()) {
			tables.add(converter.apply(rs));
		}
		// }

		return tables;
	}

	public static <K, T> Map<K, T> columnsToMap(Connection connection, String tableNamePattern,
			Function<ResultSet, K> keyMapper, Function<ResultSet, T> valueMapper) throws Exception {
		if (keyMapper == null || valueMapper == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		Map<K, T> tables = new LinkedHashMap<>();
		// try (Connection connection = getConnection()) {

		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = COLUMN_CONVERTER.apply(tableNamePattern, metaData);

		while (rs.next()) {
			K k = keyMapper.apply(rs);
			if (k == null)
				continue;
			T t = valueMapper.apply(rs);
			tables.put(k, t);
		}
		return tables;
	}

	public static final BiFunction<String, DatabaseMetaData, ResultSet> COLUMN_CONVERTER = (tableNamePattern,
			metaData) -> {
		int dotIdx = tableNamePattern.indexOf('.');
		try {
			if (dotIdx >= 0) {
				String category = tableNamePattern.substring(0, dotIdx);
				String tableName = tableNamePattern.substring(dotIdx + 1);
				return metaData.getColumns(category, null, tableName, null);
			}
			return metaData.getColumns(null, null, tableNamePattern, null);
		} catch (Exception e) {
			//
		}
		return null;
	};

	public static final BiFunction<String, DatabaseMetaData, ResultSet> PRIMARY_CONVERTER = (tableNamePattern,
			metaData) -> {
		int dotIdx = tableNamePattern.indexOf('.');
		try {
			if (dotIdx >= 0) {
				String category = tableNamePattern.substring(0, dotIdx);
				String tableName = tableNamePattern.substring(dotIdx + 1);
				return metaData.getPrimaryKeys(category, null, tableName);
			}
			return metaData.getPrimaryKeys(null, null, tableNamePattern);
		} catch (Exception e) {
			//
		}
		return null;
	};

	public static List<String> pks(String tableNamePattern) throws Exception {
		return pks(getConnection(), tableNamePattern, t -> {
			try {
				return t.getString(4);
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			return "";
		});
	}

	public static List<String> pks(Connection con, String tableNamePattern) throws Exception {
		return pks(con, tableNamePattern, t -> {
			try {
				return t.getString(4);
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			return "";
		});
	}

	public static <T> List<T> pks(String tableNamePattern, Function<ResultSet, T> converter) throws Exception {
		List<T> tables = Collections.emptyList();
		try (Connection connection = getConnection()) {
			tables = pks(connection, tableNamePattern, converter);
		}
		return tables;
	}

	public static <T> List<T> pks(Connection connection, String tableNamePattern, Function<ResultSet, T> converter)
			throws Exception {
		if (converter == null)
			throw new GargoyleException(GargoyleException.ERROR_CODE.PARAMETER_EMPTY, "converter is null ");

		List<T> tables = new ArrayList<>();

		DatabaseMetaData metaData = connection.getMetaData();

		ResultSet rs = PRIMARY_CONVERTER.apply(tableNamePattern, metaData); // metaData.getPrimaryKeys(null,
																			// null,
																			// tableNamePattern);

		if (rs != null) {
			while (rs.next()) {
				tables.add(converter.apply(rs));
			}
		}

		return tables;
	}

	/**
	 *
	 * // 16.09.01 >> 쿼리로 부터 테이블을 찾아옴 퍼옴 by Hong
	 *
	 * @param sql
	 * @return
	 */
	public static String getTableNames(String sql) {
		// 2016-11-10 문자열 관련된 처리므로 ValueUtil로 이동.
		return ValueUtil.getTableNames(sql);
	}

	/********************************
	 * 작성일 : 2016. 9. 3. 작성자 : KYJ
	 *
	 * 스키마라는 개념이 존재하는 데이터베이스인지 유무
	 *
	 * @return
	 ********************************/
	public static boolean isExistsSchemaDatabase() {

		String driver = DbUtil.getDriver().trim();
		if (driver == null || driver.isEmpty())
			return true;
		return isExistsSchemaDatabase(driver);
	}

	/********************************
	 * 작성일 : 2016. 9. 3. 작성자 : KYJ
	 *
	 * 스키마라는 개념이 존재하는 데이터베이스인지 유무
	 *
	 * @param con
	 * @return
	 ********************************/
	public static boolean isExistsSchemaDatabase(Connection con) {
		try {
			String driverName = con.getMetaData().getDriverName();
			return isExistsSchemaDatabase(driverName);
		} catch (SQLException e) {
			// Nothing.
		}
		return false;
	}

	/********************************
	 * 작성일 : 2016. 9. 3. 작성자 : KYJ
	 *
	 * 스키마라는 개념이 존재하는 데이터베이스인지 유무
	 *
	 * @param driver
	 * @return
	 ********************************/
	public static boolean isExistsSchemaDatabase(String driver) {

		String drivers = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.NOT_EXISTS_SCHEMA_DRIVER_NAMES);
		if (drivers != null && !driver.isEmpty()) {
			drivers = drivers.trim();
			Optional<String> findFirst = Stream.of(drivers.split(",")).filter(v -> v.equals(driver)).findFirst();
			return !findFirst.isPresent();
		}
		return true;
	}

	public static List<String> getSqlFunctions(final Connection con, boolean autoClose) throws SQLException {
		try {
			DatabaseMetaData metaData = con.getMetaData();

			String stringFunctions = metaData.getStringFunctions();
			String numericFunctions = metaData.getNumericFunctions();
			String timeDateFunctions = metaData.getTimeDateFunctions();

			return Stream.of(stringFunctions.split(","), numericFunctions.split(","), timeDateFunctions.split(","))
					.flatMap(v -> {
						return Stream.of(v);
					}).collect(Collectors.toList());

		} catch (Exception e) {

		} finally {

			if (autoClose)
				con.close();
		}
		return Collections.emptyList();
	}

	public static void getCurrentSchema(Connection con) {

	}

	/**
	 * 빈프로퍼티 로우 매퍼클래스를 생성후 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 4.
	 * @param clazz
	 * @return
	 */
	public static <T> RowMapper<T> createBeanRowMapper(Class<T> clazz) {
		return ParameterizedBeanPropertyRowMapper.newInstance(clazz);
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
