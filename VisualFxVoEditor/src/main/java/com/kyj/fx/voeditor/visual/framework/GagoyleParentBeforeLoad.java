/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 6. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import javafx.scene.Parent;

/**
 * @author KYJ
 *
 */
public interface GagoyleParentBeforeLoad {

	/**
	 * 해당 Parent가 구현하고자하는 코드에 포함되는 객체인지 체크한후 맞다면 true를 아니다면 false를 리턴한다.
	 * 
	 * true/false값에 따라 beforeLoad 메소드 실행여부가 결정된다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 25.
	 * @param parent
	 * @return
	 */
	public boolean filter(Parent parent);

	public void beforeLoad(Parent parent);

	/**
	 * Parent Node를 메인프레임에 로드할지 유무를 결정함.
	 * 
	 * 기본값은 false
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 25.
	 * @return
	 */
	public default boolean isUnloadParent() {
		return false;
	}
}
