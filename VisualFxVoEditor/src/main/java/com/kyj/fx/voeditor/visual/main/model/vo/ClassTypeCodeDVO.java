/**
 * KYJ
 * 2015. 10. 14.
 */
package com.kyj.fx.voeditor.visual.main.model.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class ClassTypeCodeDVO {

	private StringProperty commCode;
	private StringProperty commCodeNm;

	public ClassTypeCodeDVO() {
		commCode = new SimpleStringProperty();
		commCodeNm = new SimpleStringProperty();
	}

	public StringProperty commCodeProperty() {
		return commCode;
	}

	public StringProperty commCodeNmProperty() {
		return commCodeNm;
	}

	/**
	 * KYJ
	 * 
	 * @return the commCode
	 */
	public String getCommCode() {
		return commCode.get();
	}

	/**
	 * KYJ
	 * 
	 * @param commCode
	 *            the commCode to set
	 */
	public void setCommCode(String commCode) {
		this.commCode.set(commCode);
	}

	/**
	 * KYJ
	 * 
	 * @return the commCodeNm
	 */
	public String getCommCodeNm() {
		return commCodeNm.get();
	}

	/**
	 * KYJ
	 * 
	 * @param commCodeNm
	 *            the commCodeNm to set
	 */
	public void setCommCodeNm(String commCodeNm) {
		this.commCodeNm.set(commCodeNm);
	}

}
