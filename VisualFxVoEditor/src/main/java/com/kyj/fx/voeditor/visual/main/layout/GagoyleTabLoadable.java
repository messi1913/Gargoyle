/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2016. 2. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import javafx.scene.Parent;

/**
 * 
 * 가고일에서 사용하는 탭 로더를 정의한다.
 * 
 * @author KYJ
 *
 */
interface GagoyleTabLoadable {

	/**
	 * 새로운탭을 로드함.
	 * 
	 * @param tableName
	 * @param parent
	 */
	void loadNewSystemTab(String tableName, Parent parent);

	/**
	 * 새로운 템을 로드함.
	 * 
	 * @param tabName
	 * @param fxmlName
	 */
	void loadNewSystemTab(String tabName, String fxmlName);
	
	
	
	
}
