/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.prcd.mssql
 *	작성일   : 2017. 11. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.prcd.mssql;

import com.kyj.fx.voeditor.visual.component.sql.dbtree.commons.ProcedureReader;
import com.kyj.fx.voeditor.visual.component.sql.dbtree.mssql.MSSQLProcedureReader;
import com.kyj.fx.voeditor.visual.component.sql.functions.ConnectionSupplier;
import com.kyj.fx.voeditor.visual.component.sql.prcd.commons.ProcedureCallComposite;

/**
 * @author KYJ
 *
 */
public class MssqlProcedureCallComposite<T> extends ProcedureCallComposite<T> {

	private ConnectionSupplier connectionSupplier;

	public MssqlProcedureCallComposite(ConnectionSupplier connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	@Override
	public ProcedureReader getProcedureReader() {
		try {
			return new MSSQLProcedureReader(connectionSupplier());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConnectionSupplier connectionSupplier() {
		return connectionSupplier;
	}

}
