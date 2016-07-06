/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2015. 10. 29.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core;

import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;

/**
 * @author KYJ
 *
 */
public interface IResultSetConverter {

	/**
	 * 변수를 받고 DAO ResultSet문장을 완성시킨다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param col
	 * @return
	 */
	public String convert(String varName, String resultSetVarName, TbpSysDaoColumnsDVO col);

	/**
	 * 데이터베이스 컬럼을 입력받고 그 컬럼의 타입에 맞는 형식을 리턴한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param dbColumnName
	 * @return
	 */
	public String getTypeTo(String dbColumnName);

}
