/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

/**
 * 리턴문을 기술하는 처리를 담당
 * @author KYJ
 *
 */
public interface IRetrunStatement {

	/**
	 * 리턴문에 선언되는 키워드를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 17.
	 * @return
	 */
	String returnKeyword();

	/**
	 * 키워드 이후에 처리할 statement 기술.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 17.
	 * @param statement
	 * @return
	 */
	String getReturnStatement(String statement);
}
