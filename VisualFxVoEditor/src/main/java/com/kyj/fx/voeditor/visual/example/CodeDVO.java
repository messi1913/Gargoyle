/**
 * package : com.kyj.fx.voeditor.visual.component
 *	fileName : CodeDVO.java
 *	date      : 2015. 11. 11.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.example;

/**
 * @author KYJ
 *
 */
public class CodeDVO {
	private String codeNm;
	private String code;

	public CodeDVO(String codeNm, String code) {
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CodeDVO [codeNm=" + codeNm + ", code=" + code + "]";
	}

}
