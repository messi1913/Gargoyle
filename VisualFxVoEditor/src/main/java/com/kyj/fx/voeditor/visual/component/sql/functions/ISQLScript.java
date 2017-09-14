/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : ICRUDScript.java
 *	date      : 2015. 11. 19.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

/**
 * @author KYJ
 *
 */
public interface ISQLScript {

	/**
	 * insert문 리턴
	 *
	 * @return
	 */
	public String insert();

	/**
	 * delete문 리턴
	 *
	 * @return
	 */
	public String delete();

	/**
	 * update문 리턴
	 *
	 * @return
	 */
	public String update();

	/**
	 * select문 리턴
	 *
	 * @return
	 */
	public String select();

	/**
	 * primarykey 조회 쿼리문 리턴
	 *
	 * @return
	 */
	public String primaryKey();
}
