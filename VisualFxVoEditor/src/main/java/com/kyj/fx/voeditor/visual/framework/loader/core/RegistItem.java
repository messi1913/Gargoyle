/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service
 *	작성일   : 2016. 4. 8.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.core;

import javafx.scene.Parent;

/**
 *
 * 트리거뷰에 대한 메타데이터를 정의한다.
 *
 * 이 변수는 MakeTriggerFrameComposite클래스에서 처리한다.
 *
 * 해당클래스로 등록시 등록하는 화면은 반드시 fxml에 controller를 선언한 화면이 되도록한다.
 *
 * @author KYJ
 *
 */
public class RegistItem {

	/**
	 * 화면에 대한 id를 임시정의 이 id를 통해 탭이 중복등록되었는지 유효성 검증이 가능하다.
	 *
	 * @최초생성일 2016. 4. 18.
	 */
	public String id;
	/**
	 * title을 등록하는경우 탭명으로 표시된다.
	 *
	 * @최초생성일 2016. 4. 18.
	 */
	private String title;
	/**
	 * 화면에 표시할 노드
	 *
	 * @최초생성일 2016. 4. 18.
	 */
	private Parent node;

	/**
	 * 
	 * 생성자 <br/>
	 * 
	 * @param id
	 * @param title
	 * @param parent
	 * @throws Exception
	 */
	public RegistItem(String id, String title, Parent parent) throws Exception {
		this.id = id;
		this.title = title;

		this.node = parent;

	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	public final Parent getNode() {
		return node;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
