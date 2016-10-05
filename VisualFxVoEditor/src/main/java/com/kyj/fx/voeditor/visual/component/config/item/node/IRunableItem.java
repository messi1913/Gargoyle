/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

/**
 * @author KYJ
 *
 */
public interface IRunableItem{

	/**
	 * 실행시킬수 있는 아이템에 대한 정의
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public RunableType getRunableType();

	/**
	 * 설정화면에 보여줄 Tab리스트
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.tbm_sys_dao
	 * @return
	 */
	public default ObservableList<Tab> getTabs() {
		return FXCollections.observableArrayList();
	}

	/**
	 * 해당 아이템에 대한 타이틀
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public String getTitle();

	/**
	 * 아이템에 대한 이름
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public String getName();

}
