/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2015. 11. 30.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

/**
 * JSON 형태로 보관된 데이터베이스 셋
 * 
 * @author KYJ
 *
 */
public class Code {

	private String codeNm;
	private Object code;

	public Code(String codeNm, Object code) {
		super();
		this.codeNm = codeNm;
		this.code = code;
	}

	public String getCodeNm() {
		return codeNm;
	}

	public void setCodeNm(String codeNm) {
		this.codeNm = codeNm;
	}

	public Object getCode() {
		return code;
	}

	public void setCode(Object code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return codeNm;
	}

}
