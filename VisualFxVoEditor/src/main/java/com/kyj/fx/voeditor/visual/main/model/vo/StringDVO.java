/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2017. 11. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;

/**
 * @author KYJ
 *
 */
public class StringDVO extends AbstractDVO {

	private String value;

	/**
	 */
	public StringDVO() {

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
