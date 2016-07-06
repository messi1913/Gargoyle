/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

/**
 * 테이블에 속한 인덱스 정보를 조회한다.
 *
 * @author KYJ
 *
 */
public abstract class AbstractTableIndexInformationController extends BorderPane implements ItableInformation {

	private TableInformationFrameView parent;

	@FXML
	private TreeTableView<TableIndexNode> treeIndex;

	@FXML
	private TreeTableColumn<TableIndexNode, String> tcName;

	@FXML
	private TreeTableColumn<TableIndexNode, String> tcType;

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
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(TableInformationFrameView.class.getResource(KEY_TABLE_INDEX_INFOMATION));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.sql.table.ItableInformation#
	 * setParentFrame(com.kyj.fx.voeditor.visual.component.sql.table.
	 * TableInformationFrameView)
	 */
	@Override
	public void setParentFrame(TableInformationFrameView parent) {
		this.parent = parent;
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

		String sql = getIndexSQL(databaseName, tableName);
		List<TableIndexNode> query = this.parent.query(sql, mapper());

		TreeItem<TableIndexNode> convertTreeRoot = convertTreeRoot(query);

		treeIndex.setRoot(convertTreeRoot);

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
