/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.table
 *	작성일   : 2016. 1. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.NumberingCellValueFactory;
import com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public abstract class AbstractTableColumnInformationController extends AbstractTableInfomation {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractTableColumnInformationController.class);;
	private TableInformationFrameView parent;

	@FXML
	private TableView<TableColumnMetaVO> tbColumns;

	@FXML
	private TableColumn<TableColumnMetaVO, KEY_TYPE> colKeyType;

	@FXML
	private TableColumn<TableColumnMetaVO, Integer> colNumber;

	@FXML
	private TableColumn<TableColumnMetaVO, String> colReference;

	/**
	 * 테이블의 컬럼 구성요소에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_COLUMNS_INFORMATION = TableInformationFrameView.KEY_TABLE_COLUMNS_INFORMATION;

	/**
	 * 기본키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String PRIMAKRY_KEY_IMAGE_NAME = "key_R";
	/**
	 * 멀티키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String MULTI_KEY_IMAGE_NAME = "key_G";
	/**
	 * 외리키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String FOREIGN_KEY_IMAGE_NAME = "key_B";

	/**
	 * 생성자 fxml을 로드한다.
	 *
	 * @throws Exception
	 */
	public AbstractTableColumnInformationController() throws Exception {
		super(KEY_TABLE_COLUMNS_INFORMATION);
	}

	@FXML
	public void initialize() {
		colNumber.setCellValueFactory(new NumberingCellValueFactory<TableColumnMetaVO>(tbColumns));

		colKeyType.setCellFactory(param -> new TableCell<TableColumnMetaVO, KEY_TYPE>() {

			@Override
			protected void updateItem(KEY_TYPE item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {

					setGraphic(null);

				} else {

					ImageView image = null;
					switch (item) {
					case PRI:
						image = getImage(PRIMAKRY_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					case MULTI:
						image = getImage(MULTI_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					case FOREIGN:
						image = getImage(FOREIGN_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					default:
						image = new ImageView();
					}

					setGraphic(image);
				}

			}

		});

		colReference.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TableColumnMetaVO, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<TableColumnMetaVO, String> param) {

				TableColumnMetaVO value = param.getValue();
				SimpleStringProperty stringProperty = new SimpleStringProperty();
				if (value != null && value.getRefs() != null) {
					Optional<String> reduce = value.getRefs().stream()
							.map(v -> v.getPkColumnName() + " ->  [ " + v.getFkTableName() + "-" + v.getFkColumnName() + " ]")
							.reduce((str1, str2) -> str1.concat("\n").concat(str2));
					reduce.ifPresent(stringProperty::setValue);

				}
				return stringProperty;
			}
		});

		colKeyType.setCellValueFactory(new PropertyValueFactory<>("keyType"));
		colKeyType.setStyle("-fx-alignment:center");

		this.tbColumns.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		FxUtil.installClipboardKeyEvent(this.tbColumns);


	}

	/**
	 * resources패키지로부터 image를 가져오는 처리
	 *
	 * @param name
	 * @return
	 */
	static ImageView getImage(String name) {
		try {
			String name2 = "META-INF/images/keyImages/" + name + ".png";
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(name2);
			return new ImageView(new Image(is));
		} catch (Exception e) {

			// not important...
		}

		return new ImageView();
	}

	@Override
	public void setParentFrame(TableInformationFrameView tableInformationFrameView) {
		this.parent = tableInformationFrameView;
	}

	@Override
	public void init() throws Exception {
		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		String databaseName = metadata.getDatabaseName();
		String tableName = metadata.getTableName();

		if (ValueUtil.isEmpty(tableName)) {
			throw new NullPointerException("tableName 이 비었습니다.");
		}

		List<TableColumnMetaVO> result = Collections.emptyList();

		//2016-11-26 by kyj 일단 모든 데이터베이스에서 다 조회가능한 인덱스 조회법으로 수정
		String sql = "";/* getTableColumnsSQL(databaseName, tableName); */
		if (ValueUtil.isEmpty(sql)) {
			result = this.parent.queryForMeta(new ConverterFunction(databaseName, tableName));
		} else {
			try {
				result = this.parent.query(sql, rowMapper());
			} catch (Exception e) {
				result = this.parent.queryForMeta(new ConverterFunction(databaseName, tableName));
			}
		}

		tbColumns.getItems().addAll(result);


//		avaliableTypes();
	}

	/**
	 * 테이블코멘트정보를 리턴하는 SQL문을 반환
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract String getTableColumnsSQL(String databaseName, String tableName) throws Exception;

	/**
	 * 쿼리 결과에 따란 데이터매퍼를 설정한후 반환한다.
	 *
	 * @return
	 */
	public abstract RowMapper<TableColumnMetaVO> rowMapper();

	@Override
	public void clear() throws Exception {
		tbColumns.getItems().clear();
	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}

//	public void avaliableTypes() throws SQLException {
//
//
//		DatabaseMetaData metaData = this.parent.getConnection().getMetaData();
//		ResultSet attributes = metaData.getAttributes(null, null, null, null);
//
//		ResultSetMetaData rMeta = attributes.getMetaData();
//		int columnCount = rMeta.getColumnCount();
//
//		while (attributes.next()) {
//			for (int i = 1; i < columnCount; i++) {
//				String value = attributes.getString(i);
//				String columnName = rMeta.getColumnName(i);
//				System.out.println(String.format("%s - %s", columnName, value));
//			}
//		}
//
//		//		this.parent.getConnection().getMetaData().
//	}

	/**
	 * @author KYJ
	 *
	 */
	private class ConverterFunction implements Function<DatabaseMetaData, List<TableColumnMetaVO>> {
		private String databaseName, tableName;

		public ConverterFunction(String databaseName, String tableName) {
			this.databaseName = databaseName;
			this.tableName = tableName;
		}

		/**
		 * Retrieves a description of table columns available in the specified
		 * catalog.
		 *
		 * <P>
		 * Only column descriptions matching the catalog, schema, table and
		 * column name criteria are returned. They are ordered by
		 * <code>TABLE_CAT</code>,<code>TABLE_SCHEM</code>,
		 * <code>TABLE_NAME</code>, and <code>ORDINAL_POSITION</code>.
		 *
		 * <P>
		 * Each column description has the following columns:
		 * <OL>
		 * <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be
		 * <code>null</code>)
		 * <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be
		 * <code>null</code>)
		 * <LI><B>TABLE_NAME</B> String {@code =>} table name
		 * <LI><B>COLUMN_NAME</B> String {@code =>} column name
		 * <LI><B>DATA_TYPE</B> int {@code =>} SQL type from java.sql.Types
		 * <LI><B>TYPE_NAME</B> String {@code =>} Data source dependent type
		 * name, for a UDT the type name is fully qualified
		 * <LI><B>COLUMN_SIZE</B> int {@code =>} column size.
		 * <LI><B>BUFFER_LENGTH</B> is not used.
		 * <LI><B>DECIMAL_DIGITS</B> int {@code =>} the number of fractional
		 * digits. Null is returned for data types where DECIMAL_DIGITS is not
		 * applicable.
		 * <LI><B>NUM_PREC_RADIX</B> int {@code =>} Radix (typically either 10
		 * or 2)
		 * <LI><B>NULLABLE</B> int {@code =>} is NULL allowed.
		 * <UL>
		 * <LI>columnNoNulls - might not allow <code>NULL</code> values
		 * <LI>columnNullable - definitely allows <code>NULL</code> values
		 * <LI>columnNullableUnknown - nullability unknown
		 * </UL>
		 * <LI><B>REMARKS</B> String {@code =>} comment describing column (may
		 * be <code>null</code>)
		 * <LI><B>COLUMN_DEF</B> String {@code =>} default value for the column,
		 * which should be interpreted as a string when the value is enclosed in
		 * single quotes (may be <code>null</code>)
		 * <LI><B>SQL_DATA_TYPE</B> int {@code =>} unused
		 * <LI><B>SQL_DATETIME_SUB</B> int {@code =>} unused
		 * <LI><B>CHAR_OCTET_LENGTH</B> int {@code =>} for char types the
		 * maximum number of bytes in the column
		 * <LI><B>ORDINAL_POSITION</B> int {@code =>} index of column in table
		 * (starting at 1)
		 * <LI><B>IS_NULLABLE</B> String {@code =>} ISO rules are used to
		 * determine the nullability for a column.
		 * <UL>
		 * <LI>YES --- if the column can include NULLs
		 * <LI>NO --- if the column cannot include NULLs
		 * <LI>empty string --- if the nullability for the column is unknown
		 * </UL>
		 * <LI><B>SCOPE_CATALOG</B> String {@code =>} catalog of table that is
		 * the scope of a reference attribute (<code>null</code> if DATA_TYPE
		 * isn't REF)
		 * <LI><B>SCOPE_SCHEMA</B> String {@code =>} schema of table that is the
		 * scope of a reference attribute (<code>null</code> if the DATA_TYPE
		 * isn't REF)
		 * <LI><B>SCOPE_TABLE</B> String {@code =>} table name that this the
		 * scope of a reference attribute (<code>null</code> if the DATA_TYPE
		 * isn't REF)
		 * <LI><B>SOURCE_DATA_TYPE</B> short {@code =>} source type of a
		 * distinct type or user-generated Ref type, SQL type from
		 * java.sql.Types (<code>null</code> if DATA_TYPE isn't DISTINCT or
		 * user-generated REF)
		 * <LI><B>IS_AUTOINCREMENT</B> String {@code =>} Indicates whether this
		 * column is auto incremented
		 * <UL>
		 * <LI>YES --- if the column is auto incremented
		 * <LI>NO --- if the column is not auto incremented
		 * <LI>empty string --- if it cannot be determined whether the column is
		 * auto incremented
		 * </UL>
		 * <LI><B>IS_GENERATEDCOLUMN</B> String {@code =>} Indicates whether
		 * this is a generated column
		 * <UL>
		 * <LI>YES --- if this a generated column
		 * <LI>NO --- if this not a generated column
		 * <LI>empty string --- if it cannot be determined whether this is a
		 * generated column
		 * </UL>
		 * </OL>
		 *
		 * <p>
		 * The COLUMN_SIZE column specifies the column size for the given
		 * column. For numeric data, this is the maximum precision. For
		 * character data, this is the length in characters. For datetime
		 * datatypes, this is the length in characters of the String
		 * representation (assuming the maximum allowed precision of the
		 * fractional seconds component). For binary data, this is the length in
		 * bytes. For the ROWID datatype, this is the length in bytes. Null is
		 * returned for data types where the column size is not applicable.
		 *
		 * @param catalog
		 *            a catalog name; must match the catalog name as it is
		 *            stored in the database; "" retrieves those without a
		 *            catalog; <code>null</code> means that the catalog name
		 *            should not be used to narrow the search
		 * @param schemaPattern
		 *            a schema name pattern; must match the schema name as it is
		 *            stored in the database; "" retrieves those without a
		 *            schema; <code>null</code> means that the schema name
		 *            should not be used to narrow the search
		 * @param tableNamePattern
		 *            a table name pattern; must match the table name as it is
		 *            stored in the database
		 * @param columnNamePattern
		 *            a column name pattern; must match the column name as it is
		 *            stored in the database
		 * @return <code>ResultSet</code> - each row is a column description
		 * @exception SQLException
		 *                if a database access error occurs
		 * @see #getSearchStringEscape
		 */
		@Override
		public List<TableColumnMetaVO> apply(DatabaseMetaData t) {

			List<TableColumnMetaVO> list = new ArrayList<>();
			try {

				ResultSet resultSet = t.getColumns(null, this.databaseName, this.tableName, null);
				if (resultSet.getRow() <= 0)
					resultSet = t.getColumns(this.databaseName, null, this.tableName, null);

				Map<String, String> primaryKey = getPrimaryKey(t);

				Set<ReferenceKey> exportKey = getReferenceKey(t);
				boolean existsReferenceKey = false;
				if (exportKey != null && !exportKey.isEmpty()) {
					existsReferenceKey = true;
				}

				while (resultSet.next()) {

					String columnName = resultSet.getString("COLUMN_NAME");
					String dataType = resultSet.getString("TYPE_NAME");
					String columSize = resultSet.getString("COLUMN_SIZE");
					String ordianl = resultSet.getString("ORDINAL_POSITION");
					String remark = resultSet.getString("REMARKS");
					String defaultValue = resultSet.getString("COLUMN_DEF");

					TableColumnMetaVO vo = new TableColumnMetaVO();
					vo.setColumnName(columnName);
					vo.setSortOrder(ordianl);
					vo.setDefaultValue(defaultValue);
					int nullable = resultSet.getInt("NULLABLE");
					switch (nullable) {
					case 0:
						vo.setIsNullable("Not Null");
						break;

					case 1:
						vo.setIsNullable("Nullable");
						break;

					default:
						vo.setIsNullable("Unknown");
						break;
					}

					if (primaryKey.containsKey(columnName)) {
						vo.setKeyType(KEY_TYPE.PRI);
					}

					if (existsReferenceKey) {
						vo.setRefs(exportKey.stream().filter(r -> columnName.equals(r.getPkColumnName())).collect(Collectors.toList()));
					}

					vo.setDataLength(columSize);
					vo.setDataType(dataType);
					vo.setRemark(remark);
					list.add(vo);
				}
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			return list;
		}

		/**
		 * @작성자 : KYJ
		 * @작성일 : 2016. 11. 28.
		 * @param t
		 * @return
		 * @throws SQLException
		 */
		private Set<ReferenceKey> getReferenceKey(DatabaseMetaData t) throws SQLException {
			ResultSet exportedKeys = t.getExportedKeys(null, this.databaseName, this.tableName);
			if (exportedKeys.getRow() <= 0)
				exportedKeys = t.getExportedKeys(this.databaseName, null, this.tableName);

			Set<ReferenceKey> referenceSet = new TreeSet<ReferenceKey>();
			while (exportedKeys.next()) {

				ReferenceKey referenceKey = new ReferenceKey();
				referenceKey.setPkColumnName(exportedKeys.getString("PKCOLUMN_NAME"));
				referenceKey.setPkTableName(exportedKeys.getString("PKTABLE_NAME"));
				referenceKey.setFkColumnName(exportedKeys.getString("FKCOLUMN_NAME"));
				referenceKey.setFkTableName(exportedKeys.getString("FKTABLE_NAME"));
				referenceSet.add(referenceKey);
			}

			return referenceSet;
		}

		private Map<String, String> getPrimaryKey(DatabaseMetaData t) throws SQLException {
			ResultSet presult = t.getPrimaryKeys(null, this.databaseName, this.tableName);
			if (presult.getRow() <= 0)
				presult = t.getPrimaryKeys(this.databaseName, null, this.tableName);

			Map<String, String> pkMap = new TreeMap<String, String>();
			while (presult.next()) {
				pkMap.put(presult.getString("COLUMN_NAME"), presult.getString("PK_NAME"));
			}
			return pkMap;
		}

	}
}
