/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql
 *	작성일   : 2017. 11. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureItemTree;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.SchemaItemTree;

/**
 * @author KYJ
 *
 */
public class MSSQLProcedureItemTree extends ProcedureItemTree<String> {

	public MSSQLProcedureItemTree() throws Exception {
	}

	public MSSQLProcedureItemTree(SchemaItemTree<String> parent, String cat, String schem, String procedureName, String remark)
			throws Exception {
		super(parent, cat, schem, procedureName, remark);
	}

	@Override
	public String readProcedureContent() {

		try {
			MSSQLProcedureReader reader = new MSSQLProcedureReader(getConSupplier());
			return reader.readProcedure(getCat(), getProcedureName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.readProcedureContent();
	}

}
