/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

/**
 *
 * 키 유형에따른 정확한 분류처리를 함.
 *
 * DBMS마다 키 유형을 정의하는부분이 다르기 때문에
 *
 * DBMS 조건에 맞게 구현한다.
 *
 * @author KYJ
 *
 */
@FunctionalInterface
public interface IKeyType {

	/**
	 * 키의 유형을 정의
	 *
	 * @author KYJ
	 *
	 */
	public static enum KEY_TYPE {
		NOMAL, PRI, MULTI, FOREIGN
	}

	/**
	 * 특정 DBMS에서 리턴해주는 값을 참고하여 그 값이 기본키인지 외래키인지등을 구분하여 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 21.
	 * @param check
	 * @return
	 */
	KEY_TYPE getType(String check);

	/**
	 * 기본키유형인지 유무
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 21.
	 * @param type
	 * @return
	 */
	default boolean isPrimaryKey(KEY_TYPE type) {
		return KEY_TYPE.PRI == type;
	}

	/**
	 * 멀티키유형인지 유무
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 21.
	 * @param type
	 * @return
	 */
	default boolean isMultiKey(KEY_TYPE type) {
		return KEY_TYPE.MULTI == type;
	}

	/**
	 * 외래키 유형인지 유무
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 21.
	 * @param type
	 * @return
	 */
	default boolean isForeignKey(KEY_TYPE type) {
		return KEY_TYPE.FOREIGN == type;
	}

}
