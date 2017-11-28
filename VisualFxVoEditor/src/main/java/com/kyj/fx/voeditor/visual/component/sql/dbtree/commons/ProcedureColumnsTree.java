/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.commons
 *	작성일   : 2017. 11. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.DbTreePackageInfo;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @author KYJ
 *
 */
public class ProcedureColumnsTree<T> extends ProcedureItemTree<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureColumnsTree.class);
	/**
	 * @throws Exception
	 */
	public ProcedureColumnsTree() throws Exception {
		super();
		
	}

	// private String cat, schem, procedureName, type, colName;
	private Map<String, Object> prop;
	// public ProcedureColumnsTree(ProcedureItemTree<T> parent, String cat,
	// String schem, String procedureName, String colName, String type)
	// throws Exception {
	// this.cat = cat;
	// this.schem = schem;
	// this.procedureName = procedureName;
	// this.colName = colName;
	// this.type = type;
	// }

	public ProcedureColumnsTree(ProcedureItemTree<T> parent, Map<String, Object> prop) throws Exception {
		this.prop = prop;
		LOGGER.debug(prop.toString());
		// Object cat = m.get("PROCEDURE_CAT");
		// Object schem = m.get("PROCEDURE_SCHEM");
		// String procedureName = getProcedureName();
		// Object colName = m.get("COLUMN_NAME");
		// Object type = m.get("COLUMN_TYPE");
	}

	@Override
	public String getChildrenSQL(String... conditions) {
		return null;
	}

	@Override
	public void read() throws Exception {

	}

	@Override
	public Node createGraphics() {
		Image fxImage = new Image(DbTreePackageInfo.class.getResourceAsStream("column.png"), 15d, 15d, false, false);
		return new ImageView(fxImage);
	}

	@Override
	public String getName() {
		
		//PRECISION - 데이터베이스에서 표현하는 크기
		//LENGTH  바이트 크기
		return String.format("%s  ( %s, %s )", prop.get("COLUMN_NAME"), prop.get("TYPE_NAME"), prop.get("PRECISION"));
		// return prop.get("COLUMN_NAME").toString();
	}

	@Override
	public ObservableList<TreeItem<DatabaseItemTree<T>>> applyChildren(List<Map<String, Object>> items) throws Exception {
		return null;
	}

}
