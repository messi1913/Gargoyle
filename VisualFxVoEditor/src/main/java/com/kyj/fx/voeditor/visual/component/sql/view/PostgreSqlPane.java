/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.postgre.PostgreDatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.postgre.PostgreTableItemTree;
import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

/**
 * 포스트그레 SQL 패널
 *
 * @author KYJ
 *
 */
public class PostgreSqlPane extends CommonsSqllPan {

	private static Logger LOGGER = LoggerFactory.getLogger(PostgreSqlPane.class);

	public PostgreSqlPane() {
		super(ResourceLoader.DBMS_SUPPORT_POSTGRE);
	}

	public PostgreSqlPane(String dbms) {
		super(dbms);
	}

	@Override
	public TreeItem<DatabaseItemTree<String>> apply(String t, Supplier<Connection> conSupplier) {
		try {
			DatabaseItemTree<String> databaseItemTree = new PostgreDatabaseItemTree("databases", conSupplier);
			TreeItem<DatabaseItemTree<String>> createNode = new DatabaseTreeNode().createNode(databaseItemTree);
			return createNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#menuExportMergeScriptOnAction(javafx.event.ActionEvent)
	 */
	@Override
	public void menuExportMergeScriptOnAction(ActionEvent e) {

//		TableView<Map<String, Object>> tbResult = getTbResult();


		List<Map<String, Object>> items = getSelectedTabResultItems();
//		TreeView<DatabaseItemTree<String>> schemaTree = getSchemaTree();
//
//		List<String> schemaList = schemaTree.getRoot().getChildren().stream().map(v -> v.getValue().getName()).collect(Collectors.toList());
//
		String defaultSchema = "";
		try {
			Map<String, Object> findOne = DbUtil.findOne(connectionSupplier.get(), "select current_schema() as currentschema");
			if (findOne != null && !findOne.isEmpty()) {
				defaultSchema = ValueUtil.decode(findOne.get("currentschema"), "").toString();
			}
		} catch (Exception e4) {
			e4.printStackTrace();
		}
//
		final String _defaultSchema = defaultSchema;
//
//		if (items.isEmpty())
//			return;
//
//		// TODO :: DBMS에 따라 Merge문 생성 로직 분기 처리 필요.
//
//		Optional<Pair<String, String[]>> showInputDialog = DialogUtil.showInputCustomDialog(tbResult.getScene().getWindow(), "table Name",
//				"테이블명을 입력하세요.", new CustomInputDialogAction<GridPane, String[]>() {
//
//					TextField txtSchema;
//					TextField txtTable;
//
//					@Override
//					public GridPane getNode() {
//						GridPane gridPane = new GridPane();
//						txtSchema = new TextField();
//						txtTable = new TextField();
//
//						FxUtil.installAutoTextFieldBinding(txtSchema, () -> {
//							return schemaList;
//						});
//
//						FxUtil.installAutoTextFieldBinding(txtTable, () -> {
//							return searchPattern(txtSchema.getText(), txtTable.getText()).stream().map(v -> v.getValue().getName())
//									.collect(Collectors.toList());
//						});
//						txtSchema.setText(_defaultSchema);
//
//						//Default TableName
//						TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
//						if (null != selectedItem) {
//							DatabaseItemTree<String> value = selectedItem.getValue();
//							if (value instanceof TableItemTree) {
//								txtTable.setText(value.getName());
//							}
//						}
//
//						Label label = new Label("Schema : ");
//						Label label2 = new Label("Table : ");
//						gridPane.add(label, 0, 0);
//						gridPane.add(label2, 1, 0);
//						gridPane.add(txtSchema, 0, 1);
//						gridPane.add(txtTable, 1, 1);
//						return gridPane;
//					}
//
//					@Override
//					public String[] okClickValue() {
//
//						String schema = txtSchema.getText().trim();
//						String table = txtTable.getText().trim();
//
//						String[] okValue = new String[2];
//						okValue[0] = schema;
//						okValue[1] = table;
//						return okValue;
//					}
//
//					@Override
//					public String[] cancelClickValue() {
//						return null;
//					}
//
//				});

		Optional<Pair<String, String[]>> showTableInputDialog = showTableInputDialog(v -> v.getName());
		showTableInputDialog.ifPresent(op -> {
			if (!"OK".equals(op.getKey()))
				return;
			String[] resultValue = op.getValue();
			try {
				StringBuilder clip = new StringBuilder();

				PostgreTableItemTree dirtyTreeItem = new PostgreTableItemTree();
				String schemaName = (resultValue[0] == null || resultValue[0].isEmpty()) ? _defaultSchema : resultValue[0];
				String tableName = resultValue[1];

				String childrenSQL = dirtyTreeItem.getChildrenSQL(schemaName, tableName);
				List<Map<String, Object>> select = DbUtil.select(childrenSQL);

				List<String> pkList = new ArrayList<>();
				List<String> notPkList = new ArrayList<>();
				List<String> columnList = new ArrayList<>();
				for (Map<String, Object> map : select) {
					String columnName = map.get("column_name").toString();
					Object primaryKeyYn = map.get("primary_yn");
					columnList.add(columnName);
					if ("Y".equals(primaryKeyYn)) {
						pkList.add(columnName);
					} else {
						notPkList.add(columnName);
					}
				}

				// Map<String, Object> map = items.get(0);
				// final Set<String> columnList = map.keySet();
				// 클립보드 복사
				String mergePreffix = String.format("with upsert as ( update %s set  ", tableName);
				String mergeMiddle = " returning * )\n";
				String collect = columnList.stream()/* .map(str -> str) */.collect(Collectors.joining(", ", "(", ")"));
				String insertPreffix = String.format("insert into %s", tableName);

				String insertend = " where not exists (select * from upsert);\n";

				List<String> valueList = items.stream().map(v -> {
					return ValueUtil.toJSONObject(v);
				}).map(v -> {
					String updateSetSql = notPkList.stream().map(str -> {
						if (str == null)
							return null;
						else {
							JsonElement jsonElement = v.get(str);
							if (jsonElement == null) {
								return str.concat("=").concat("null");
							} else {
								String dataValue = jsonElement.toString();
								dataValue = dataValue.substring(1, dataValue.length() - 1);
								if (dataValue.indexOf("'") >= 0) {
									dataValue = StringUtils.replace(dataValue, "'", "''");
								}
								return str.concat("=").concat("'").concat(dataValue).concat("'");
							}
						}
					}).collect(Collectors.joining(", "));

					String updateWhereSql = pkList.stream().map(str -> {
						if (str == null)
							return null;
						else {

							if (null == v.get(str))
								return null;

							String dataValue = v.get(str).toString();

							dataValue = dataValue.substring(1, dataValue.length() - 1);
							if (dataValue.indexOf("'") >= 0) {
								try {
									dataValue = StringUtils.replace(dataValue, "'", "''");
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}

							return str.concat(" = ").concat("'").concat(dataValue).concat("'");
						}
					}).filter(str -> str != null).collect(Collectors.joining(" and "));

					String insertValueSql = columnList.stream().map(str -> {
						if (str == null)
							return null;
						else {

							JsonElement jsonElement = v.get(str);
							if (jsonElement == null) {
								return "null";
							} else {
								String dataValue = jsonElement.toString();
								dataValue = dataValue.substring(1, dataValue.length() - 1);
								if (dataValue.indexOf("'") >= 0) {
									dataValue = StringUtils.replace(dataValue, "'", "''");
								}
								return "'".concat(dataValue).concat("'");
							}
						}
					}).collect(Collectors.joining(", "));

					return new StringBuilder().append(mergePreffix).append(updateSetSql).append(" where ").append(updateWhereSql)
							.append(mergeMiddle).append(insertPreffix).append(collect).append(" select ").append(insertValueSql)
							.append(insertend).toString();

				}).collect(Collectors.toList());

				valueList.forEach(str -> {
					clip.append(str);
				});



				SqlKeywords parent = new SqlKeywords();
				parent.setContent(clip.toString());
//				SimpleTextView parent = new SimpleTextView(clip.toString());
				parent.setWrapText(false);
				parent.setPrefSize(1200d, 800d);
				FxUtil.createStageAndShow(parent, stage -> {
					stage.initOwner(getScene().getWindow());
					stage.setTitle(String.format("[Merge Script] Table : %s", tableName));
				});

			} catch (Exception e2) {
				LOGGER.error(ValueUtil.toString(e2));
				DialogUtil.showExceptionDailog(e2, "에러발생, 테이블을 잘못 선택하셨을 수 있습니다.");

			}
		});

	}

//	public List<TreeItem<DatabaseItemTree<String>>> searchSchemaTreeItemPattern(String schema) {
//		TreeItem<DatabaseItemTree<String>> root = getSchemaTree().getRoot();
//		List<TreeItem<DatabaseItemTree<String>>> treeItem = new ArrayList<>();
//		// schema
//		//		if (ValueUtil.isNotEmpty(schema)) {
//		for (TreeItem<DatabaseItemTree<String>> w : root.getChildren()) {
//			String _schemaName = w.getValue().toString();
//
//			if (_schemaName.indexOf(schema) >= 0) {
//				treeItem.add(w);
//			}
//		}
//		//		}
//		return treeItem;
//	}
//
//	public List<TreeItem<DatabaseItemTree<String>>> searchPattern(String schema, String tableName) {
//		List<TreeItem<DatabaseItemTree<String>>> searchPattern = searchSchemaTreeItemPattern(schema);
//		if (searchPattern.isEmpty())
//			return Collections.emptyList();
//
//		return searchPattern.stream().flatMap(root -> {
//
//			List<TreeItem<DatabaseItemTree<String>>> subList = new ArrayList<>();
//			for (TreeItem<DatabaseItemTree<String>> w : root.getChildren()) {
//				String _schemaName = w.getValue().toString();
//
//				if (_schemaName.indexOf(tableName) >= 0) {
//					subList.add(w);
//				}
//			}
//			return subList.stream();
//		}).collect(Collectors.toList());
//
//	}
}
