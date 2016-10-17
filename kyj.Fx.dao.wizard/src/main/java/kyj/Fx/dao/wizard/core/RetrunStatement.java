/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

/**
 *  리턴문을 기술하는 처리를 담당
 * @author KYJ
 *
 */
public class RetrunStatement implements IRetrunStatement {

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.IRetrunStatement#returnKeyword()
	 */
	public String returnKeyword() {
		return "return";
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.IRetrunStatement#getReturnStatement(java.lang.String)
	 */
	public String getReturnStatement(String statement) {
		return new StringBuffer().append(" query( ").append(statement).append(");").toString();
	}

}
