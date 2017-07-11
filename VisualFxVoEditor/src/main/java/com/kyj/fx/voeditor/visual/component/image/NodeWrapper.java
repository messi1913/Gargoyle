/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.image
 *	작성일   : 2017. 5. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.image;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class NodeWrapper extends BorderPane {

	private CheckBox checkbox;
	private boolean isBase64;

	/**
	 * 접속 URL 혹은 BASE64 텍스트를 리턴한다.
	 * 
	 * @최초생성일 2017. 5. 18.
	 */
	private String content;

	public NodeWrapper(Node center, String content, boolean isBase64) {
		this.checkbox = new CheckBox();
		setTop(this.checkbox);
		this.setCenter(center);
		this.content = content;
		this.isBase64 = isBase64;
	}

	public boolean isChecked() {
		return this.checkbox.isSelected();
	}

	public void setCheck(boolean b) {
		this.checkbox.setSelected(b);

	}

	public void clearCheck() {
		setCheck(false);
	}

	public CheckBox getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(CheckBox checkbox) {
		this.checkbox = checkbox;
	}

	public boolean isBase64() {
		return isBase64;
	}

	public void setBase64(boolean isBase64) {
		this.isBase64 = isBase64;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
