/********************************
 *	프로젝트 : FxVoEditor
 *	패키지   : com.kyj.fx.voeditor.core.model.meta
 *	작성일   : 2015. 10. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.core.model.meta;

/**
 * 이 값이 VO의 필드에 존재하는경우 JAVAFX UI에서 컬럼 헤더명으로 자동으로 표시되는 기능을 하게된다.
 * 
 * @author KYJ
 *
 */
public @interface COLUMN {

	/**
	 * 컬럼명 속성
	 * 
	 * @Date 2015. 10. 20.
	 * @return
	 * @User KYJ
	 */
	public String value();
}
