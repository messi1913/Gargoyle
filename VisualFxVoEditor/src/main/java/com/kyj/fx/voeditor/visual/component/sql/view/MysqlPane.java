/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2015. 11. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.DatabaseTreeNode;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.DatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.TableItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.mysql.MySQLDatabaseItemTree;
import com.kyj.fx.voeditor.visual.component.text.SimpleTextView;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * MYSQL 조회 패널
 *
 * @author KYJ
 *
 */

public class MysqlPane extends CommonsSqllPan {
	// private static Logger LOGGER = LoggerFactory.getLogger(MysqlPane.class);

	public MysqlPane() {
		super(ResourceLoader.DBMS_SUPPORT_MY_SQL);
	}

	public MysqlPane(String dbms) {
		super(dbms);
	}

	@Override
	public TreeItem<DatabaseItemTree<String>> apply(String t, Supplier<Connection> conSupplier) {
		try {
			DatabaseItemTree<String> databaseItemTree = new MySQLDatabaseItemTree("databases", conSupplier);
			TreeItem<DatabaseItemTree<String>> createNode = new DatabaseTreeNode().createNode(databaseItemTree);
			return createNode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#
	 * menuExportMergeScriptOnAction(javafx.event.ActionEvent)
	 */
	@Override
	public void menuExportMergeScriptOnAction(ActionEvent e) {
		DialogUtil.showMessageDialog((Stage) this.getScene().getWindow(), "Not yet support");

	}

	/*
	 * 100개의 데이터 보여주기 위해 처리하는 메소드
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.view.SqlPane#show100RowAction(
	 * javafx.event.ActionEvent)
	 */
	@Override
	public List<Map<String, Object>> show100RowAction() {

		String driver = getDriver();
		String wrapperedSQL = "";
		if (driver != null) {
			String key = ConfigResourceLoader.SQL_LIMIT_WRAPPER.replaceAll("\\{driver\\}", driver);
			wrapperedSQL = ConfigResourceLoader.getInstance().get(key);
		} else {
			wrapperedSQL = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_LIMIT_WRAPPER);
		}

		// JDBC와 JAVAFX간계 에러발생으로 생략.... 벤더에따라 에러가 발생하기도 함. 에러 발생시
		// Connection connection = connectionSupplier.get();
		//
		// // 코드 에러 범위 수정.
		//
		// try {
		// Properties clientInfo = connection.getClientInfo();
		// LOGGER.debug(String.format("Client Info : %s",
		// clientInfo.toString()));
		// } catch (Throwable e) {
		//
		// }

		TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
		if (selectedItem != null) {

			DatabaseItemTree<String> value = selectedItem.getValue();
			if (value instanceof TableItemTree) {
				TreeItem<DatabaseItemTree<String>> schemaTree = selectedItem.getParent();
				String schemaName = schemaTree.getValue().getName();
				// if(value.isValideSchemaName(schemaName))

				String tableName = "";
				/*
				 * 2016-07-12 SQLite에서는 스키마라는 개념이 존재하지않는다. Schema Name을 100개의로우를
				 * 보여주는 SQL에 적용할지 여부를 결정한다.
				 */
				if (value.isApplySchemaName(schemaName)) {
					tableName = String.format("`%s`.%s", schemaName, value.getName()); // schemaName.concat(".").concat(value.getName());
				} else {
					tableName = value.getName();
				}

				String sql = ValueUtil.getVelocityToText(wrapperedSQL, "usersql", "select * from ".concat(tableName));
				execute(sql);
			}
		}

		return null;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 25.
	 */
	@Override
	public void showEditableDataAction() {
		// Default TableName
		TreeItem<DatabaseItemTree<String>> selectedItem = getSchemaTree().getSelectionModel().getSelectedItem();
		if (null != selectedItem) {
			DatabaseItemTree<String> value = selectedItem.getValue();
			if (value instanceof TableItemTree) {

				@SuppressWarnings("rawtypes")
				TableItemTree tableItemTree = (TableItemTree) value;
				String schemaName = tableItemTree.getParent().getName();
				String tableName = tableItemTree.getName();
				String sql = String.format("edit `%s`.%s", schemaName, tableName);
				execiteEdit(sql);
			}
		}
	}

	@Override
	public void menuExportInsertScriptOnAction(ActionEvent e) {
		Optional<Pair<String, String[]>> showTableInputDialog = showTableInputDialog(f -> f.getName());

		// Optional<Pair<String, String>> showInputDialog =
		// DialogUtil.showInputDialog("table Name", "테이블명을 입력하세요.");
		if (showTableInputDialog == null)
			return;

		showTableInputDialog.ifPresent(op -> {

			if (op == null || op.getValue() == null)
				return;

			String schemaName = op.getValue()[0];
			String _tableName = op.getValue()[1];
			String tableName = "";
			if (ValueUtil.isNotEmpty(schemaName)) {
				tableName = String.format("`%s`.%s", schemaName, _tableName);
			}

			ObservableList<Map<String, Object>> items = getTbResult().getItems();
			Map<String, Object> map = items.get(0);
			final Set<String> keySet = map.keySet();
			// 클립보드 복사
			StringBuilder clip = new StringBuilder();

			String insertPreffix = "insert into " + tableName;
			String collect = keySet.stream()/* .map(str -> str) */.collect(Collectors.joining(",", "(", ")"));
			String insertMiddle = " values ";

			List<String> valueList = items.stream().map(v -> {
				return ValueUtil.toJSONObject(v);
			}).map(v -> {
				Iterator<String> iterator = keySet.iterator();
				List<Object> values = new ArrayList<>();
				while (iterator.hasNext()) {
					String columnName = iterator.next();
					Object value = v.get(columnName);
					values.add(value);
				}
				return values;
			}).map(list -> {

				return list.stream().map(str -> {
					if (str == null)
						return null;
					else {
						String convert = str.toString();
						convert = convert.substring(1, convert.length() - 1);
						if (convert.indexOf("'") >= 0) {
							try {
								convert = StringUtils.replace(convert, "'", "''");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
						return "'".concat(convert).concat("'");
					}
				}).collect(Collectors.joining(",", "(", ")"));

			}).map(str -> {
				/* SQL문 완성처리 */
				return new StringBuilder().append(insertPreffix).append(collect).append(insertMiddle).append(str).append(";\n").toString();
			}).collect(Collectors.toList());

			valueList.forEach(str -> {
				clip.append(str);
			});

			SimpleTextView parent = new SimpleTextView(clip.toString());
			parent.setWrapText(false);
			FxUtil.createStageAndShow(String.format("[InsertScript] Table : %s", tableName), parent, stage -> {
			});
		});

	}
}
