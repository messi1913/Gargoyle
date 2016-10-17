/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.parser;

import kyj.Fx.dao.wizard.core.RetrunStatement;

/**
 * @author KYJ
 *
 */
public class GargoyleRetrunStatement extends RetrunStatement {

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.IRetrunStatement#returnKeyword()
	 */
	public String returnKeyword() {
		return "return ";
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.IRetrunStatement#getReturnStatement(java.lang.String)
	 */
	public String getReturnStatement(String statement) {
		return new StringBuffer().append(" query( ").append(statement).append(");").toString();
	}

}
