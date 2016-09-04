/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.List;
import java.util.function.Supplier;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public abstract class AbstractMacroFavorDAO {

	private Supplier<Connection> connectionSupplier;

	public AbstractMacroFavorDAO(Supplier<Connection> connectionSupplier) {
		this.connectionSupplier = connectionSupplier;
	}

	public Connection getConnection() {
		return connectionSupplier.get();
	}

	public abstract List<MacroItemVO> getRoots() throws Exception;

	public abstract List<MacroItemVO> getChildrens(MacroItemVO vo) throws Exception;

}
