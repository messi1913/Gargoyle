/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

import com.kyj.fx.voeditor.visual.component.config.view.RunConfigTab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * @author KYJ
 *
 */
public interface IRunableItem {

	/**
	 * class package name
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public abstract String getClassPackageName();

	/**
	 * 대표 이미지
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public abstract Image getImage();

	/**
	 * 아이템에 대한 이름
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public default String getName() {
		return getRunableType().name();
	}

	/**
	 * ListView에 보여지게 될 DisplayName
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 6.
	 * @return
	 */
	public default String getDisplayName() {
		return getClassPackageName();
	}

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
	public default ObservableList<RunConfigTab> getTabs() {
		return FXCollections.observableArrayList();
	}

	/**
	 * 해당 아이템에 대한 타이틀
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public String getTitle();

}
