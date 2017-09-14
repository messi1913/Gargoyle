/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

/**
 * 테이블에 속한 인덱스 정보를 조회한다.
 *
 * @author KYJ
 *
 */
public abstract class AbstractTableIndexInformationController extends AbstractTableInfomation {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTableIndexInformationController.class);


	@FXML
	private TreeTableView<TableIndexNode> treeIndex;

	@FXML
	private TreeTableColumn<TableIndexNode, String> tcName;

	@FXML
	private TreeTableColumn<TableIndexNode, String> tcType;

	@FXML
	private TreeTableColumn<TableIndexNode, String> tcNoneUnique;
	/**
	 * 테이블 index에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_INDEX_INFOMATION = TableInformationFrameView.KEY_TABLE_INDEX_INFOMATION;

	/**
	 * 생성자
	 *
	 * @throws Exception
	 */
	public AbstractTableIndexInformationController() throws Exception {
		super(KEY_TABLE_INDEX_INFOMATION);
	}



	/**
	 * UI 기능 초기화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 */
	@FXML
	public void initialize() {

		tcName.setCellValueFactory(param -> {
			TableIndexNode value = param.getValue().getValue();
			SimpleStringProperty str = new SimpleStringProperty();
			if (value instanceof TableIndexLeaf) {
				String toString = ((TableIndexLeaf) value).getColumnNane();
				str.setValue(toString);
			} else {
				String constraintName = value.getConstraintName();
				str.setValue(constraintName);
			}
			return str;
		});

		tcType.setCellValueFactory(param -> {
			TableIndexNode value = param.getValue().getValue();
			SimpleStringProperty str = new SimpleStringProperty();
			if (!(value instanceof TableIndexLeaf)) {
				String constraintType = value.getType();
				str.setValue(constraintType);
			}
			return str;
		});

		tcNoneUnique.setCellValueFactory(param ->{
			TableIndexNode value = param.getValue().getValue();
			SimpleStringProperty str = new SimpleStringProperty();
			if (!(value instanceof TableIndexLeaf)) {
				boolean noneUnique = value.isNoneUnique();
				str.setValue(noneUnique ? "yes" : "no");
			}
			return str;
		});
	}

	/**
	 * @author KYJ
	 *
	 */
	private class ConverterFunction implements Function<DatabaseMetaData, List<TableIndexNode>> {
		private String databaseName, tableName;

		public ConverterFunction(String databaseName, String tableName) {
			this.databaseName = databaseName;
			this.tableName = tableName;
		}

		/**
		 * Retrieves a description of the given table's indices and statistics.
		 * They are ordered by NON_UNIQUE, TYPE, INDEX_NAME, and
		 * ORDINAL_POSITION.
		 *
		 * <P>
		 * Each index column description has the following columns:
		 * <OL>
		 * <LI><B>TABLE_CAT</B> String {@code =>} table catalog (may be
		 * <code>null</code>)
		 * <LI><B>TABLE_SCHEM</B> String {@code =>} table schema (may be
		 * <code>null</code>)
		 * <LI><B>TABLE_NAME</B> String {@code =>} table name
		 * <LI><B>NON_UNIQUE</B> boolean {@code =>} Can index values be
		 * non-unique. false when TYPE is tableIndexStatistic
		 * <LI><B>INDEX_QUALIFIER</B> String {@code =>} index catalog (may be
		 * <code>null</code>); <code>null</code> when TYPE is
		 * tableIndexStatistic
		 * <LI><B>INDEX_NAME</B> String {@code =>} index name; <code>null</code>
		 * when TYPE is tableIndexStatistic
		 * <LI><B>TYPE</B> short {@code =>} index type:
		 * <UL>
		 * <LI>tableIndexStatistic - this identifies table statistics that are
		 * returned in conjuction with a table's index descriptions
		 * <LI>tableIndexClustered - this is a clustered index
		 * <LI>tableIndexHashed - this is a hashed index
		 * <LI>tableIndexOther - this is some other style of index
		 * </UL>
		 * <LI><B>ORDINAL_POSITION</B> short {@code =>} column sequence number
		 * within index; zero when TYPE is tableIndexStatistic
		 * <LI><B>COLUMN_NAME</B> String {@code =>} column name;
		 * <code>null</code> when TYPE is tableIndexStatistic
		 * <LI><B>ASC_OR_DESC</B> String {@code =>} column sort sequence, "A"
		 * {@code =>} ascending, "D" {@code =>} descending, may be
		 * <code>null</code> if sort sequence is not supported;
		 * <code>null</code> when TYPE is tableIndexStatistic
		 * <LI><B>CARDINALITY</B> long {@code =>} When TYPE is
		 * tableIndexStatistic, then this is the number of rows in the table;
		 * otherwise, it is the number of unique values in the index.
		 * <LI><B>PAGES</B> long {@code =>} When TYPE is tableIndexStatisic then
		 * this is the number of pages used for the table, otherwise it is the
		 * number of pages used for the current index.
		 * <LI><B>FILTER_CONDITION</B> String {@code =>} Filter condition, if
		 * any. (may be <code>null</code>)
		 * </OL>
		 *
		 * @param catalog
		 *            a catalog name; must match the catalog name as it is
		 *            stored in this database; "" retrieves those without a
		 *            catalog; <code>null</code> means that the catalog name
		 *            should not be used to narrow the search
		 * @param schema
		 *            a schema name; must match the schema name as it is stored
		 *            in this database; "" retrieves those without a schema;
		 *            <code>null</code> means that the schema name should not be
		 *            used to narrow the search
		 * @param table
		 *            a table name; must match the table name as it is stored in
		 *            this database
		 * @param unique
		 *            when true, return only indices for unique values; when
		 *            false, return indices regardless of whether unique or not
		 * @param approximate
		 *            when true, result is allowed to reflect approximate or out
		 *            of data values; when false, results are requested to be
		 *            accurate
		 * @return <code>ResultSet</code> - each row is an index column
		 *         description
		 * @exception SQLException
		 *                if a database access error occurs
		 */
		@Override
		public List<TableIndexNode> apply(DatabaseMetaData t) {

			List<TableIndexNode> list = new ArrayList<>();
			try {

				// ResultSet pResult = t.getPrimaryKeys(this.databaseName, null,
				// this.tableName);
				// while (pResult.next()) {
				//
				//// String constraintName = pResult.getString("PK_NAME");
				// String columnName = pResult.getString("COLUMN_NAME");
				// TableIndexNode tableIndexNode = new TableIndexNode("Primary
				// Key", columnName);
				// tableIndexNode.setColumnNane(columnName);
				// list.add(tableIndexNode);
				// }

				/*
				* tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
				◦ tableIndexClustered - this is a clustered index
				◦ tableIndexHashed - this is a hashed index
				◦ tableIndexOther - this is some other style of index
				 */

				ResultSet resultSet = t.getIndexInfo(null, this.databaseName, this.tableName, false, false);
				if (resultSet.getRow() <= 0)
					resultSet = t.getIndexInfo(this.databaseName, null, this.tableName, false, false);

				// int row = resultSet.getRow();
				// System.out.println(row);

				while (resultSet.next()) {

					// test
					{
//						String dbCatalog = resultSet.getString("TABLE_CAT");
//						String dbSchema = resultSet.getString("TABLE_SCHEM");
//						String dbTableName = resultSet.getString("TABLE_NAME");
//						boolean dbNoneUnique = resultSet.getBoolean("NON_UNIQUE");
//						String dbIndexQualifier = resultSet.getString("INDEX_QUALIFIER");
//						String dbIndexName = resultSet.getString("INDEX_NAME");
//						short dbType = resultSet.getShort("TYPE");
//						short dbOrdinalPosition = resultSet.getShort("ORDINAL_POSITION");
//						String dbColumnName = resultSet.getString("COLUMN_NAME");
//						String dbAscOrDesc = resultSet.getString("ASC_OR_DESC");
//						int dbCardinality = resultSet.getInt("CARDINALITY");
//						int dbPages = resultSet.getInt("PAGES");
//						String dbFilterCondition = resultSet.getString("FILTER_CONDITION");
//
//						System.out.println("index name=" + dbIndexName);
//						System.out.println("table=" + dbTableName);
//						System.out.println("column=" + dbColumnName);
//						System.out.println("catalog=" + dbCatalog);
//						System.out.println("schema=" + dbSchema);
//						System.out.println("nonUnique=" + dbNoneUnique);
//						System.out.println("indexQualifier=" + dbIndexQualifier);
//						System.out.println("type=" + dbType);
//						System.out.println("ordinalPosition=" + dbOrdinalPosition);
//						System.out.println("ascendingOrDescending=" + dbAscOrDesc);
//						System.out.println("cardinality=" + dbCardinality);
//						System.out.println("pages=" + dbPages);
//						System.out.println("filterCondition=" + dbFilterCondition);
					}

					/*
					* tableIndexStatistic - this identifies table statistics that are returned in conjuction with a table's index descriptions
					◦ tableIndexClustered - this is a clustered index
					◦ tableIndexHashed - this is a hashed index
					◦ tableIndexOther - this is some other style of index
					 */
					String indexName = resultSet.getString("INDEX_NAME");
					int _type = resultSet.getInt(7);
					String type = "";
					switch(_type){
					case 0:
						type =  "tableIndexStatistic";
						break;
					case 1:
						type =  "tableIndexClustered";
						break;
					case 2:
						type =  "tableIndexHashed";
						break;
					case 3:
						type =  "tableIndexOther";
						break;

					}
					String columnName = resultSet.getString(9);
					boolean dbNoneUnique = resultSet.getBoolean("NON_UNIQUE");

					TableIndexLeaf tableIndexNode = new TableIndexLeaf(type, indexName);
					tableIndexNode.setColumnNane(columnName);
					tableIndexNode.setNoneUnique(dbNoneUnique);

					list.add(tableIndexNode);
				}
			} catch (SQLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			return list;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.table.ItableInformation#init()
	 */
	@Override
	public void init() throws Exception {
		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		String databaseName = metadata.getDatabaseName();
		String tableName = metadata.getTableName();

		if (ValueUtil.isEmpty(tableName)) {
			throw new NullPointerException("tableName이 비었습니다.");
		}

		//2016-11-26 by kyj 일단 모든 데이터베이스에서 다 조회가능한 인덱스 조회법으로 수정
		String sql = "";/*getIndexSQL(databaseName, tableName);*/
		List<TableIndexNode> result = Collections.emptyList();

		if (ValueUtil.isEmpty(sql)) {
			result = this.parent.queryForMeta(new ConverterFunction(databaseName, tableName));
		} else {
			try {
				result = this.parent.query(sql, mapper());
			} catch (Exception e) {
				result = this.parent.queryForMeta(new ConverterFunction(databaseName, tableName));
			}
		}

		treeIndex.setRoot(convertTreeRoot(result));

	}

	/**
	 * 테이블 인덱스를 조회하는 SQL문 반환.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @param databaseName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public abstract String getIndexSQL(String databaseName, String tableName) throws Exception;

	/**
	 * SQL에 해당하는결과를 바인딩하는 맵퍼 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @return
	 */
	public abstract RowMapper<TableIndexNode> mapper();

	/**
	 * 결과데이터를 이용하여 Tree를 구성하는 로직 구현
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @param items
	 * @return
	 */
	public TreeItem<TableIndexNode> convertTreeRoot(List<TableIndexNode> items) {

		// 계층구조 작성, list항목을 그룹핑처리하는 작업.
		List<TableIndexNode> collect = items.stream().collect(new Supplier<List<TableIndexNode>>() {

			@Override
			public List<TableIndexNode> get() {
				List<TableIndexNode> arrayList = new ArrayList<>();
				arrayList.add(new TableIndexNode("root", "root"));
				return arrayList;
			}

		}, (collections, next) -> {
			// root
			TableIndexNode tableIndexNode = collections.get(0);
			List<TableIndexNode> childrens = tableIndexNode.getChildrens();

			String type = next.getType();
			String constraintName = next.getConstraintName();
			// root밑 부모 추가.
			if (childrens.isEmpty() || !childrens.get(childrens.size() - 1).getType().equals(type)) {
				TableIndexNode e = new TableIndexNode(type, constraintName);
				e.getChildrens().add(next);
				childrens.add(e);

			} else {
				childrens.get(childrens.size() - 1).getChildrens().add(next);
			}

		} , (a, b) -> {
			/* 아래함수는 동작하지않는데 일단 지켜보자. */
			a.addAll(b);
		});

		TableIndexNode root = collect.get(0);
		return new IndexTreeItem().createNode(root);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.table.ItableInformation#clear()
	 */
	@Override
	public void clear() throws Exception {
		treeIndex.getRoot().getChildren().clear();
	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}
}
