/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2016. 1. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.BigDataDVO;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * ResultSet 결과 데이터를 Map형태로 반환함.
 *
 * @author KYJ
 *
 */
public class ResultSetToMapConverter implements BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetToMapConverter.class);

	/**
	 * 컬럼 크기가 큰 경우 데이터 맵핑을 생략할건지 유무
	 *
	 * @최초생성일 2016. 2. 11.
	 */
	public static final String SKIP_BIG_DATA_COLUMN = ResourceLoader.SKIP_BIG_DATA_COLUMN;

	/**
	 * Mapping처리할때 필요한 속성이 정의된다.
	 *
	 * @최초생성일 2016. 2. 12.
	 */
	private Properties prop;

	private boolean isBigDataColumnSkip;

	public ResultSetToMapConverter(Properties prop) {
		if (prop != null)
			this.prop = prop;
		else
			this.prop = getDefaultProperties();
		initialize();
	}

	public ResultSetToMapConverter() {
		this.prop = getDefaultProperties();
		initialize();
	}

	public Properties getDefaultProperties() {
		return new Properties();
	}

	private void initialize() {
		if (this.prop.containsKey(SKIP_BIG_DATA_COLUMN)) {
			Object value = this.prop.get(SKIP_BIG_DATA_COLUMN);
			if (value != null)
				isBigDataColumnSkip = "true".equals(this.prop.get(SKIP_BIG_DATA_COLUMN).toString());
		}
	}

	@Override
	public List<Map<String, Object>> apply(ResultSetMetaData t, ResultSet u) {

		List<Map<String, Object>> arrayList = Collections.emptyList();
		try {
			ResultSetMetaData metaData = u.getMetaData();
			int columnCount = metaData.getColumnCount();
			arrayList = new ArrayList<Map<String, Object>>();
			boolean firstRow = true;
			while (u.next()) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				for (int c = 1; c <= columnCount; c++) {

					int columnType = metaData.getColumnType(c);

					// 컬럼 크기가 큰 경우 데이터 맵핑을 생략할건지 유무

					String value = u.getString(c);
					boolean isEmptyValue = value == null || value.isEmpty();
//					String tmpColumnLabel = metaData.getColumnLabel(c);
					String columnLabel = metaData.getColumnLabel(c);

					//2016-08-04 중복되는 컬럼이 씹혀 없어지지않도록 컬럼이름이 중복되면 인덱스를 붙임.
					int nextNameIdx = 1;
					while (map.containsKey(columnLabel) && /*무한루핑 방지*/nextNameIdx < 1000 ) {
						columnLabel = String.format("%s_%d", metaData.getColumnLabel(c), nextNameIdx);
						nextNameIdx ++;
					}

					if (isBigDataColumnSkip) {
						switch (columnType) {
						case Types.BLOB:
							// map.put(metaData.getColumnLabel(c), new
							// BigDataDVO("BLOB", value));
							map.put(columnLabel, isEmptyValue ? new BigDataDVO("{data.blob}", "") : new BigDataDVO("{DATA.BLOB}", value));
							break;
						case Types.CLOB:
							// map.put(metaData.getColumnLabel(c), new
							// BigDataDVO("CLOB", value));
							map.put(columnLabel,
									isEmptyValue ? new BigDataDVO("{data.clob}", value) : new BigDataDVO("{DATA.CLOB}", value));
							break;
						default:
							String columnTypeName = metaData.getColumnTypeName(c);
							// mysql big data type.
							if ("text".equals(columnTypeName)) {
								// map.put(metaData.getColumnLabel(c), new
								// BigDataDVO("TEXT", value));
								map.put(columnLabel,
										isEmptyValue ? new BigDataDVO("{data.text}", value) : new BigDataDVO("{DATA.TEXT}", value));
								break;
							}
							// postgre big data type.
							else if ("bytea".equals(columnTypeName)) {
								// map.put(metaData.getColumnLabel(c), new
								// BigDataDVO("BYTEA", value));
								map.put(columnLabel,
										isEmptyValue ? new BigDataDVO("{data.bytea}", value) : new BigDataDVO("{DATA.BYTEA}", value));
								break;
							}
							map.put(columnLabel, value);
							break;
						}
						if (firstRow) {
							LOGGER.debug(String.format("column : %s type %s", columnLabel, metaData.getColumnTypeName(c)));
						}
					} else {
						map.put(columnLabel, value);
					}

				}
				arrayList.add(map);
				firstRow = false;
			}
		} catch (SQLException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return arrayList;
	}

}
