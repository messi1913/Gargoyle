/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 16.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang.SystemUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kyj.fx.voeditor.visual.exceptions.ProgramSpecSourceNullException;
import com.kyj.fx.voeditor.visual.framework.velocity.ExtensionDateFormatVelocityContext;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.sun.star.lang.IllegalArgumentException;

import javafx.collections.ObservableList;

/**
 * @author KYJ
 *
 */
public class ValueUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValueUtil.class);

	public enum IndexCaseTypes {
		UPPERCASE, LOWERCASE
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자, 혹은 소문자로 고친다. (IndexCaseTypes에 따른 설정)
	 *
	 * @param str
	 *            target
	 * @param index
	 *            character index
	 * @param type
	 *            lower or upper
	 * @return
	 */
	public static String getIndexcase(String str, int index, IndexCaseTypes type) {
		StringBuffer sb = new StringBuffer();

		// DEFAULT UPPERCASE
		char indexChar = Character.toUpperCase(str.charAt(index));
		if (type == IndexCaseTypes.LOWERCASE) {
			indexChar = Character.toLowerCase(str.charAt(index));
		}

		switch (index) {
		case 0:
			sb.append(indexChar).append(str.substring(index + 1));
			break;
		default:
			sb.append(str.substring(0, index)).append(indexChar).append(str.substring(index + 1));
			break;
		}
		return sb.toString();
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 대문자로 고친다.
	 *
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexUppercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.UPPERCASE);
	}

	/**
	 * str 문자열에서 index에 해당하는 부분만 소문자로 고친다.
	 *
	 * @param str
	 * @param index
	 * @return
	 */
	public static String getIndexLowercase(String str, int index) {
		return getIndexcase(str, index, IndexCaseTypes.LOWERCASE);
	}

	/**
	 * 문자열 패턴형식이 '_'가 들어가고 해당하는 문단 첫글자만 대문자로 바꾸지만.. 맨 처음 글자는 소문자
	 *
	 * @param str
	 * @return
	 */
	public static String getPrefixLowerTextMyEdit(String str) {

		char[] charArray = getPrefixUpperTextMyEdit(str).toCharArray();

		String lowerCase = String.valueOf(charArray[0]).toLowerCase();
		charArray[0] = lowerCase.charAt(0);

		return String.valueOf(charArray);
	}

	/**
	 * 문자열 패턴형식이 '_'가 들어가고 해당하는 문단 첫글자만 대문자로 바꾸고 싶을경우사용
	 *
	 * @param str
	 * @return
	 */
	public static String getPrefixUpperTextMyEdit(String str) {
		StringTokenizer stringTokenizer = new StringTokenizer(str, "_");
		String nextElement = null;
		char[] charArray = null;
		String temp = "";
		while (stringTokenizer.hasMoreElements()) {
			nextElement = (String) stringTokenizer.nextElement();
			charArray = nextElement.toCharArray();
			charArray[0] = Character.toUpperCase(charArray[0]);
			for (int i = 1; i < charArray.length; i++) {
				charArray[i] = Character.toLowerCase(charArray[i]);
			}
			temp += String.valueOf(charArray);
		}
		return temp;
	}

	/**
	 * 테이블명을 DVO명으로 바꾼다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 16.
	 * @param tableName
	 * @return
	 */
	public static String toDVOName(String tableName) {
		return getPrefixUpperTextMyEdit(tableName) + "DVO";
	}

	public static String getVelocityToText(String dynamicSql, String key, Object value) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(key, value);
		return getVelocityToText(dynamicSql, hashMap, false, null);
	}

	/**
	 * Velocity문법의 텍스트를 맵핑된 텍스트결과값으로 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param dynamicSql
	 * @param paramMap
	 * @return
	 */
	public static String getVelocityToText(String dynamicSql, Map<String, Object> paramMap) {
		return getVelocityToText(dynamicSql, paramMap, false, null);
	}

	public static String getVelocityToText(String dynamicSql, Map<String, Object> paramMap, boolean replaceNamedValue) {
		return getVelocityToText(dynamicSql, paramMap, replaceNamedValue, null);
	}

	/**
	 * Velocity문법의 텍스트를 맵핑된 텍스트결과값으로 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @param dynamicSql
	 * @param paramMap
	 * @param replaceNamedValue
	 *            namedParameter값을 바인드 변수로 사용하여 보여줄지 유무
	 * @return
	 */
	public static String getVelocityToText(String dynamicSql, Map<String, Object> paramMap, boolean replaceNamedValue,
			Context velocityContext) {
		StringWriter writer = new StringWriter();
		VelocityContext context = new VelocityContext(paramMap, new ExtensionDateFormatVelocityContext(velocityContext));

		String _dynamicSql = dynamicSql;

		Velocity.evaluate(context, writer, "DaoWizard", _dynamicSql);
		String convetedString = writer.toString();
		if (replaceNamedValue) {
			convetedString = replace(convetedString, paramMap);
		}
		return convetedString.trim();
	}

	private static String replace(String sql, Map<String, Object> paramMap) {
		if (sql == null || sql.trim().isEmpty())
			return sql;

		//		SqlParameterSource paramSource = new MapSqlParameterSource(paramMap);
		//
		//		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
		//		String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
		//		Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
		//		List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
		//		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
		//		PreparedStatementCreator newPreparedStatementCreator = pscf.newPreparedStatementCreator(params);

		//		return _sql.toString();
		//		String _sql = sql.replaceAll(STRING_PATTERN, "");
		String _sql = sql.replaceAll(COMMENT_PATTERN, "");
		String pattern = ":\\w+";
		//		String pattern = ":";
		String result = regexReplaceMatchs(pattern, _sql, v -> {
			String replace = v.replaceAll(":", "");
			Object object = paramMap.get(replace);
			String string = object.toString();
			if (object instanceof List) {
				StringBuffer sb = new StringBuffer();
				List<Object> items = (List<Object>) object;
				for (Object o : items) {
					if (ValueUtil.isNotEmpty(o)) {
						sb.append(String.format("'%s',", o.toString()));
					}
				}
				int length = sb.length();
				if (length != 0) {
					sb.setLength(length - 1);
				}
				return sb.toString();
			}

			return String.format("'%s'", string);
		});
		//		Optional<String> reduce = regexMatchs.stream().reduce((a, b) -> a.concat(b));
		//		if (reduce.isPresent())
		//			return reduce.get();
		//		return _sql;
		return result;
		//		return sql.replaceAll(pattern, "\\$");
	}

	/**
	 * Velocity 문법이 포함된 텍스트인지 리턴.
	 *
	 * @param sql
	 * @return
	 */
	public static boolean isVelocityContext(String sql) {
		if (sql == null || sql.isEmpty())
			return false;

		String _sql = sql;
		//주석에 대해당하는 문자들을 제거
		_sql = _sql.replaceAll(STRING_PATTERN, "");
		_sql = _sql.replaceAll(COMMENT_PATTERN, "");
		//		String pattern = "\\$\\w+|:\\w+";
		String pattern = "\\$\\w+";
		//		String pattern = "[( ]+\\$\\w+|=[ ]{0,}:\\w+";

		Pattern compile = Pattern.compile(pattern);
		Matcher matcher = compile.matcher(_sql);
		return matcher.find();
	}

	static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|\'([^\"\\\\]|\\\\.)*\'";
	static final String COMMENT_PATTERN = "(?:/\\*[^;]*?\\*/)|(?:--[^\\n]*)";

	/**
	 * Velocity Key값 목록을 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param dynamicSql
	 * @return
	 */
	public static List<String> getVelocityKeys(String _dynamicSql) {
		String dynamicSql = _dynamicSql;
		//주석에 대해당하는 문자들을 제거
		//		dynamicSql = dynamicSql.replaceAll(STRING_PATTERN, "");
		dynamicSql = dynamicSql.replaceAll(COMMENT_PATTERN, "");
		String pattern = "\\$\\w+|:\\w+";
		//		String pattern = "[( ]+\\$\\w+|=[ ]{0,}:\\w+";
		// 맨앞의 특수문자는 제거.
		return regexMatchs(pattern, dynamicSql, param -> {
			//			String result = param.trim();
			//			if (result.startsWith("($")) {
			//				return result.substring(2);
			//			}
			//			else if(result.startsWith("="))
			//				return result.substring(1).trim().substring(1);
			return param.substring(1);
		});
	}

	/**
	 * 정규식으로 일치하는 패턴하나 반환
	 *
	 * @param string
	 * @param tempName
	 * @return
	 */
	public static String regexMatch(String regex, String value) {
		Pattern compile = Pattern.compile(regex);
		Matcher matcher = compile.matcher(value);
		// 패턴에 일치하는 문자가 없을때까지 반복한다.
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/**
	 * 정규식으로 일치하는 패턴 데이터 목록 반환
	 *
	 * @param regex
	 * @param value
	 * @return
	 */
	public static List<String> regexMatchs(String regex, String value) {
		return regexMatchs(regex, value, null);
	}

	/**
	 * 정규식으로 일치하는 패턴 데이터 목록 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param regex
	 * @param value
	 * @param convert
	 *            찾은 문자열이 다른형태의 문자로 다시 변환의 필요가 있는경우 사용
	 * @return
	 */
	public static List<String> regexMatchs(String regex, String value, Function<String, String> convert) {

		Pattern compile = Pattern.compile(regex);

		Matcher matcher = compile.matcher(value);
		List<String> resultList = new ArrayList<String>();

		// 패턴에 일치하는 문자가 없을때까지 반복한다.
		while (matcher.find()) {
			String text = matcher.group();
			if (convert != null)
				text = convert.apply(text);

			resultList.add(text);
		}
		return resultList;
	}

	public static String regexReplaceMatchs(String regex, String value, Function<String, String> replacedText) {

		Pattern compile = Pattern.compile(regex);

		StringBuffer sb = new StringBuffer(value);
		Matcher matcher = compile.matcher(value);
		// 패턴에 일치하는 문자가 없을때까지 반복한다.
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			sb.replace(start, end, replacedText.apply(sb.substring(start, end)));
			matcher = compile.matcher(sb.toString());
		}
		return sb.toString();
	}

	/**
	 * 패턴에 일치하는 텍스트를 replace한후 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 22.
	 * @param regex
	 * @param value
	 * @param replacedText
	 * @return
	 */
	public static String regexReplaceMatchs(String regex, String value, String replacedText) {

		Pattern compile = Pattern.compile(regex);

		StringBuffer sb = new StringBuffer(value);
		Matcher matcher = compile.matcher(value);
		// 패턴에 일치하는 문자가 없을때까지 반복한다.
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			sb.replace(start, end, replacedText);
			matcher = compile.matcher(sb.toString());
		}
		return sb.toString();
	}

	/**
	 * value값이 비어있거나 null일경우 emptyThan값으로 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param value
	 * @param emptyThan
	 * @return
	 */
	public static Object emptyThan(Object value, String emptyThan) {
		if (isEmpty(value)) {
			return emptyThan;
		}
		return value;
	}

	/**
	 * value값이 빈값이아니면 notEmpty리턴 value값이 빈값이면 empty 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param value
	 * @param emptyThan
	 * @return
	 */
	public static Object decode(Object value, Object notEmpty, Object empty) {
		if (isEmpty(value)) {
			return empty;
		}
		return notEmpty;
	}

	public static Object decode(Object value, Object empty) {
		if (isEmpty(value)) {
			return empty;
		}
		return value;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 26.
	 * @param value
	 * @param notEmptyThan
	 * @param emptyThan
	 * @return
	 */
	public static <T, R> R decode(T value, Function<T, R> notEmptyThan, Supplier<R> emptyThan) {
		if (isNotEmpty(value))
			return notEmptyThan.apply(value);
		return emptyThan.get();
	}

	public static boolean isNotEmpty(Object obj) {
		boolean flag = true;
		if (obj != null) {
			if (obj instanceof String) {
				String valueOf = obj.toString().trim();
				flag = valueOf.length() > 0 && valueOf != "" && !valueOf.equals("null");
			} else if (obj instanceof Collection) {
				Collection<?> list = (Collection<?>) obj;
				flag = !list.isEmpty();

				// flag = list.size() > 0;
			} else if (obj instanceof Map) {

				Map<?, ?> map = (Map<?, ?>) obj;
				flag = map.size() > 0;
			}
		} else {
			flag = false;
		}
		return flag;

	}

	public static boolean isEmpty(Object obj) {
		return !isNotEmpty(obj);
	}

	/**
	 * 배열요소중 하나라도 비어있는경우 true
	 *
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Object... objs) {
		for (Object obj : objs) {
			if (isEmpty(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get메소드 이름 패턴의 명에서 get을 제거하고 앞글자는 소문자로 바꾼 글자를 반환
	 *
	 * @param methodName
	 * @return
	 */
	public static String getSimpleMethodName(final String methodName) {
		String getMethodName = methodName;
		// validation
		char[] charArray = getMethodName.replaceFirst("get", "").toCharArray();
		String lowerCase = String.valueOf(charArray[0]).toLowerCase();
		charArray[0] = lowerCase.charAt(0);
		getMethodName = String.valueOf(charArray);
		return getMethodName;
	}

	/**
	 * 빈을 Map으로 변환한다. 기본형 데이터만 Map으로 변환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 9. 25.
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<String, Object> toMap(final T t) {
		List<String> fields = Stream.of(t.getClass().getDeclaredFields()).map(field -> field.getName()).collect(Collectors.toList());
		return toMap(t, fields);
	}

	public static <T> Map<String, Object> toMap(final T t, String... fields) {
		return toMap(t, Arrays.asList(fields));
	}

	public static <T> Map<String, Object> toMap(final T t, List<String> fields) {
		Map<String, Object> hashMap = new LinkedHashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
			// Iterate over all the attributes
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

				// write메소드와 read메소드가 존재할때만.
				Method writeMethod = descriptor.getWriteMethod();
				Method readMethod = descriptor.getReadMethod();
				if (ValueUtil.isEmpty(writeMethod) || ValueUtil.isEmpty(readMethod)) {
					continue;
				}
				String methodName = ValueUtil.getSimpleMethodName(readMethod.getName());
				if (fields.contains(methodName)) {
					Object originalValue = readMethod.invoke(t);
					hashMap.put(methodName, originalValue);
				}

			}
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return hashMap;
	}

	public static String toString(Throwable e) {
		return toString(null, e);
	}

	/**
	 * 에러 메세지 상세화
	 *
	 * @param title
	 *            메세지 타이틀
	 * @param e
	 * @return
	 */
	public static String toString(String title, Throwable e) {
		if (e == null)
			return "[warnning] Exception is null";

		String errMsg = "";
		try (StringBuilderWriter sbw = new StringBuilderWriter()) {
			try (PrintWriter printWriter = new PrintWriter(sbw, true)) {
				if (title != null)
					printWriter.write("#############  ".concat(title).concat("  ##############\n"));
				e.printStackTrace(printWriter);
			}
			errMsg = sbw.toString();
		}
		return errMsg;
	}

	public static JSONObject toJSONObject(String str) {
		Gson gson = new Gson();
		JSONObject fromJson = gson.fromJson(str, JSONObject.class);
		return fromJson == null ? new JSONObject() : fromJson;
	}

	public static String toJSONString(Object obj) {
		Gson gson = new Gson();

		String fromJson = gson.toJson(obj);
		return fromJson;
	}

	public static JsonObject toJSONObject(Object obj) {
		String jsonString = toJSONString(obj);
		return new JsonParser().parse(jsonString).getAsJsonObject();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 4.
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public static JSONArray toJSONArray(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONArray parse = (JSONArray) parser.parse(str);
		return parse;
	}

	/**
	 * Driver명을 이용하여 데이터베이스 유형을 반환받음
	 *
	 * @param driverName
	 * @return
	 */
	public static String getDriverToDBMSName(String driver) {
		String dbmsName = "";

		// String driver =
		// ResourceLoader.getInstance().get(ResourceLoader.BASE_KEY_JDBC_DRIVER);
		if (ResourceLoader.ORG_MARIADB_JDBC_DRIVER.equals(driver)) {
			dbmsName = ResourceLoader.DBMS_SUPPORT_MY_SQL;
		} else if (ResourceLoader.ORG_POSTGRESQL_DRIVER.equals(driver)) {
			dbmsName = ResourceLoader.DBMS_SUPPORT_POSTGRE;
		} else if (ResourceLoader.ORACLE_JDBC_DRIVER_ORACLEDRIVER.equals(driver)) {
			dbmsName = ResourceLoader.DBMS_SUPPORT_ORACLE;
		} else if (ResourceLoader.ORG_H2_DRIVER.equals(driver)) {
			dbmsName = ResourceLoader.DBMS_SUPPORT_H2;
		} else if (ResourceLoader.ORG_SQLITE_JDBC.equals(driver)) {
			dbmsName = ResourceLoader.DBMS_SUPPORT_Sqlite;
		} else {
			dbmsName = null;
		}

		return dbmsName;
	}

	public static String getDriverToDBMSName(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
		String dbms = dataSource.getDriverClassName();//ValueUtil.getDriverToDBMSName(driver);
		return getDriverToDBMSName(dbms);
	}

	/**
	 * cvs 스트링 형태로 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 25.
	 * @param map
	 * @return
	 */
	public static String toCVSString(Map<String, Object> map, String delimiter) {
		if (map == null || map.isEmpty())
			return "";

		Optional<Object> reduce = map.values().stream().map(obj -> decode(obj, obj + delimiter, delimiter))
				.reduce(new BinaryOperator<Object>() {

					@Override
					public String apply(Object t, Object u) {
						return t.toString().concat(u.toString());
					}

				});

		if (reduce.isPresent()) {
			String string = reduce.get().toString();
			return string.substring(0, string.length() - 1);
		}
		return "";
	}

	/**
	 * cvs 스트링 형태로 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 25.
	 * @param map
	 * @return
	 */
	public static String toCVSString(Map<String, Object> map) {
		return toCVSString(map, ",");
	}

	public static <T> String toCVSString(List<List<T>> rows, Function<T, String> algorism) {
		return toCVSString(rows, algorism, "\t");
	}

	public static <T> String toCVSString(List<List<T>> rows, Function<T, String> algorism, String delimiter) {
		StringBuffer sb = new StringBuffer();
		rows.forEach(oba -> {
			oba.forEach(cell -> {
				String apply = algorism.apply(cell);
				sb.append(apply).append(delimiter);
			});
			sb.append(SystemUtils.LINE_SEPARATOR);
		});
		return sb.toString();
	}

	public static <T> String toCVSString(ObservableList<ObservableList<T>> rows, Function<T, String> algorism) {
		return toCVSString(rows, algorism, "\t");
	}

	public static <T> String toCVSString(ObservableList<ObservableList<T>> rows, Function<T, String> algorism, String delimiter) {
		StringBuffer sb = new StringBuffer();
		rows.forEach(oba -> {
			oba.forEach(cell -> {
				String apply = algorism.apply(cell);
				sb.append(apply).append(delimiter);
			});
			sb.append(SystemUtils.LINE_SEPARATOR);
		});
		return sb.toString();
	}

	public static String toCamelCase(final String name) {
		String[] names = StringUtils.tokenizeToStringArray(name.toLowerCase(), "_");
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (String n : names) {
			buf.append(i++ == 0 ? n : capitalize(n));
		}
		return buf.toString();
	}

	public static String capitalize(String name) {
		return StringUtils.capitalize(name);
	}

	/**
	 * 길이가 더 긴 문자열을 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String getMaxLengthString(String str1, String str2) {
		if (str1.length() > str2.length()) {
			return str1;
		} else {
			return str2;
		}
	}

	/**
	 * 길이가 더 긴 문자열을 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int getMaxLength(String str1, String str2) {
		if (str1.length() > str2.length()) {
			return str1.length();
		} else {
			return str2.length();
		}
	}

	public static String rightTrim(String text) {
		return text.replaceAll("\\s+$", "");
	}

	public static String leftTrim(String text) {
		return text.replaceAll("^\\s+", "");
	}

	/**
	 * 분자열 배열을 i~ j까지 하나로 결합
	 *
	 * @param split
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public static String valueOf(String[] split, int startIndex, int endIndex) {
		StringBuffer sb = new StringBuffer();
		for (int index = startIndex; index < endIndex; index++) {
			sb.append(split[index].trim());
		}
		return sb.toString();
	}

	/**
	 * Map객체를 값에의한 정렬을 시도한다.
	 *
	 * @param map
	 * @return
	 */
	public static <T, K> ArrayList<T> sortByValue(Map<T, K> map) {
		return sortByValue(map, (o1, o2) -> {
			K k = map.get(o1);
			K k2 = map.get(o2);

			return k.toString().compareTo(k2.toString());

		});
	}

	/**
	 * 값에의한 정렬
	 *
	 * @param map
	 * @param compare
	 * @return
	 */
	public static <T, K> ArrayList<T> sortByValue(Map<T, K> map, Comparator<T> compare) {
		return sortByValue(map, compare, false);
	}

	/**
	 * 값에의한 정렬
	 *
	 * @param map
	 * @param compare
	 * @param reverse
	 * @return
	 */
	public static <T, K> ArrayList<T> sortByValue(Map<T, K> map, Comparator<T> compare, boolean reverse) {
		ArrayList<T> arrayList = new ArrayList<T>();
		arrayList.addAll(map.keySet());
		Collections.sort(arrayList, compare);

		if (reverse)
			Collections.reverse(arrayList);
		return arrayList;
	}

	/**
	 * 클래스명으로부터 테이블이름을 유추해 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 29.
	 * @param className
	 * @return
	 */
	public static String getTableNameByClass(Class<?> className) {
		StringBuffer sb = new StringBuffer();
		String simpleName = className.getSimpleName();

		if (simpleName.endsWith("DAO")) {
			simpleName = simpleName.substring(0, simpleName.indexOf("DAO"));
		}

		if (simpleName.endsWith("DVO")) {
			simpleName = simpleName.substring(0, simpleName.indexOf("DVO"));
		}

		sb.append(Character.toUpperCase(simpleName.charAt(0)));

		for (int i = 1; i < simpleName.length(); i++) {
			char charAt = simpleName.charAt(i);

			if (Character.isUpperCase(charAt)) {
				sb.append("_").append(charAt);
			} else if (Character.isLowerCase(charAt)) {
				sb.append(Character.toUpperCase(charAt));
			}

		}

		return sb.toString().toLowerCase();
	}

	/**
	 * 객체로부터 insert문 스크립트를 리턴
	 *
	 * 단 객체가 테이블명 규칙에 준해야한다.
	 *
	 *
	 * ex) TbmUser ::: TBM_USER 테이블을 찾는다. TbpSx ::: Tbp_Sx 테이블을 찾는다. ex) TbmMsMdDVO ::: TBM_MS_MD 테이블을 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 29.
	 * @param dvo
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	private static String createInsertStatement(final Object dvo, final List<String> columns) throws Exception {
		if (isEmpty(dvo)) {
			return "";
		}

		StringBuffer insertStatement = new StringBuffer();
		String tableName = getTableNameByClass(dvo.getClass());

		if (isNotEmpty(tableName)) {

			if (isNotEmpty(columns)) {
				StringBuffer columnsBuffer = new StringBuffer();
				StringBuffer valuesBuffer = new StringBuffer();
				insertStatement.append("INSERT INTO ").append(tableName).append("\n");
				insertStatement.append("(\n");

				for (String column : columns) {
					try {
						String prefixUpperText = getPrefixUpperTextMyEdit(column);
						Method declaredMethod = dvo.getClass().getDeclaredMethod("get".concat(prefixUpperText));

						if (declaredMethod != null) {
							Object value = declaredMethod.invoke(dvo);

							// if (ObjectUtil.isNotEmpty(value)) {
							columnsBuffer.append(column).append(",\n");
							valuesBuffer.append(":").append(getPrefixLowerTextMyEdit(column)).append("").append(",\n");
							// }
						}
					} catch (NoSuchMethodException e) {
						throw e;
					}

				}

				if (columnsBuffer.length() <= 0 || valuesBuffer.length() <= 0) {
					return "";
				}

				columnsBuffer.setLength(columnsBuffer.length() - 2);
				valuesBuffer.setLength(valuesBuffer.length() - 2);
				insertStatement.append(columnsBuffer.toString());

				insertStatement.append(") VALUES (\n");

				insertStatement.append(valuesBuffer.toString());

				insertStatement.append(")\n");
			} else {
				throw new IllegalArgumentException("Table name is empty. !");
			}
		}
		return insertStatement.toString();
	}

	/**
	 * String 입력값으로부터 테이블명을 찾아본다. 글자내에 소문자가 포함되면 앞에 '_'를 붙인다.
	 *
	 * @param sourceNm
	 * @return
	 * @throws ProgramSpecSourceNullException
	 */
	public static String getTableName(String sourceNm) {
		StringBuffer stringBuffer = new StringBuffer();
		char[] charArray = sourceNm.toCharArray();

		int length = charArray.length;
		stringBuffer.append(charArray[0]);
		for (int i = 1; i < length; i++) {
			stringBuffer.append(charArray[i]);
			if (i + 1 < length) {

				if (Character.isUpperCase(charArray[i + 1])) {
					stringBuffer.append('_');
				}
			} // end if

		} // end for

		return stringBuffer.toString().toUpperCase();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 1.
	 * @param sql
	 * @return
	 */
	public static boolean isEditScript(String text) {
		String result = regexMatch("^(?i)edit", text.trim());
		if (result == null)
			return false;
		return true;
	}

	/**
	 * 인자로 받아온 path에 baseDir 디렉토리 경로를 붙여주어 절대경로로 바꾼다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @param path
	 * @return
	 */
	public static String appendBaseDir(String path) {
		String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		if (path.startsWith("\\")) {
			return baseDir.concat(path);
		}
		return path;
	}

	/**
	 * 인자로 받아온 path에서 baseDir 절대경로가 있으면 제거한다.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @param path
	 * @return
	 */
	public static String removeBaseDir(String path) {
		return path.replace(ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR), "");
	}

	/********************************
	 * 작성일 : 2016. 10. 3. 작성자 :
	 *
	 * @param sql
	 * @return
	 ********************************/

	public static String toUpperCase(String sql) {
		return toUpperLowerCase(sql, true);
	}

	/********************************
	 * 작성일 : 2016. 10. 3. 작성자 :
	 *
	 * @param sql
	 * @return
	 ********************************/
	public static String toLowerCase(String sql) {
		return toUpperLowerCase(sql, false);
	}

	/********************************
	 * 작성일 : 2016. 10. 3. 작성자 :
	 *
	 * 대소문자로 뷴류
	 *
	 * @param sql
	 * @param isUpper
	 * @return
	 ********************************/
	public static String toUpperLowerCase(String sql, boolean isUpper) {
		StringBuffer result = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(sql, "'-/*\n", true);
		String token;
		while (tokens.hasMoreTokens()) {
			token = tokens.nextToken();
			// 변수
			if ("'".equals(token)) {
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} while (!"'".equals(t) && tokens.hasMoreTokens());

				result.append(token);
			}
			// 주석 /**/
			else if ("/".equals(token)) {
				String t;
				t = tokens.nextToken();
				token += t;
				if ("*".equals(t)) {

					/*
					 * 2016-10-13 NoSuchElementException 예외처리
					 * by kyj. 주석에 해당하는 내용은 대소문자 처리안함에 관련된 로직인데
					 * 예외에 걸림.
					 */
					try{
					do {
							t = tokens.nextToken();

						token += t;
					} while (!token.endsWith("*/"));
					}catch(NoSuchElementException e){}

				}

				result.append(token);
			}
			// 주석 --
			else if ("-".equals(token)) {
				String t;
				t = tokens.nextToken();
				token += t;
				if ("-".equals(t)) {
					do {
						t = tokens.nextToken();
						token += t;
					} while (!token.endsWith("\n"));
				}
				result.append(token);
			}
			// 문자 저장
			else {
				if (isUpper) {
					result.append(token.toUpperCase());
				} else {
					result.append(token.toLowerCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param value
	 */
	public static boolean isNumber(String value) {
		int length = value.length();
		for (int i = 0; i < length; i++)
			if (!Character.isDigit(value.charAt(i)))
				return false;
		return length == 0 ? false : true;

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param value
	 */
	public static void ifNumberPresent(String value, Consumer<Double> action) {
		int length = value.length();
		for (int i = 0; i < length; i++)
			if (!Character.isDigit(value.charAt(i)))
				return;
		if (length == 0 ? false : true) {
			action.accept(Double.parseDouble(value));
		}

	}

	/**
	 * 대소문자 무시 일치하는 문자열이 포함되면 true
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @param findText
	 * @param target
	 * @return
	 */
	public static boolean hasTextIgnorecase(String target, String findText) {
		return org.apache.commons.lang.StringUtils.containsIgnoreCase(target, findText);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 12.
	 * @param code
	 * @return
	 */
	public static List<String> toList(String code) {
		return toList(code, () -> "\n");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 12.
	 * @param code
	 * @return
	 */
	public static List<String> toList(String code, Supplier<String> regexSeparators) {
		String[] split = code.split(regexSeparators.get());
		return Stream.of(split).collect(Collectors.toList());
	}

	/**
	 * 주석 자동화 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 12.
	 * @param code
	 * @param appendLineKeyword 코멘트가 추가되는 행의 첫번째줄에 키워드라고 표시할 문자 기입.
	 * @return
	 */
	public static List<String> toAutoCommentedList(String code, String appendLineKeyword) {
		return CodeCommentUtil.doAutoComment(code, appendLineKeyword);
	}


	/**
	 * 문자열로된 텍스트로부터 파일명만 추출하는 정규식 패턴을 적용한후 리턴받음.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @param fileName
	 * @return
	 */
	public static String getSimpleFileName(String fileName){

		//경로를 나타내는 특수문자만 제거한 모든 텍스트중 가장 마지막에 있는 텍스트 리턴.
		return ValueUtil.regexMatch("[^\\\\]{1,}$", fileName);
	}
}
