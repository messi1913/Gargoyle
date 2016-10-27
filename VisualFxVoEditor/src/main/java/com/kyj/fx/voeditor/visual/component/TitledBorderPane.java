/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 *
 * 일반 BorderPane 형태인데 Top에 해당하는 위치에 Label을 넣어
 * Title로 사용할 수 있게 템플릿을 지정
 * @author KYJ
 *
 */
public class TitledBorderPane extends BorderPane {

	/**
	 * Title,  it located in borderpane of top
	 * @최초생성일 2016. 10. 27.
	 */
	private Label lblTitle = new Label();

	/**
	 * Construnct.
	 * @param title
	 * @param content
	 */
	public TitledBorderPane(String title, Node content) {
		this.setCenter(content);
		this.setTop(lblTitle);
		setTitle(title);
	}

	/**
	 * setTitle
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @param title
	 */
	public void setTitle(String title) {
		this.lblTitle.setText(title);
	}

	/**
	 *  get Title Label
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 27.
	 * @return
	 */
	public Label getTitleLabel() {
		return lblTitle;
	}
}
