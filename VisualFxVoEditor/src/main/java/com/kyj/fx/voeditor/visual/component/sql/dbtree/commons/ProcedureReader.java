/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.dbtree.commons
 *	작성일   : 2017. 11. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.dbtree.commons;

import java.util.Map;

/**
 * 프로시저 읽어오는 함수 구현
 * 
 * @author KYJ
 *
 */
public interface ProcedureReader {

	/**
	 * 
	 * paramMap으로부터 리턴받은 값으로부터 프로시저 내용을 읽어오는 코드를 기술한다. <br/>
	 * 
	 * 
	 * <ol>
	 * <li>catalog String => procedure catalog (may be null)</li>
	 * <li>schema String => procedure schema (may be null)</li>
	 * <li>procedureName String => procedure name</li>
	 * </ol>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 29.
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public String readProcedure(Map<String, Object> paramMap) throws Exception;

}
