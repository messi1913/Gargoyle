package com.kyj.fx.voeditor.visual.main.model.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;

/**
 * @author KYJ
 *
 */
public class TbmSysDaoMethodsHDVO extends AbstractDVO {

	private StringProperty histTsp;
	private StringProperty packageName;
	private StringProperty className;
	private StringProperty methodName;
	private StringProperty resultVoClass;
	private StringProperty sqlBody;
	private StringProperty methodDesc;
	private StringProperty dmlType;
	private StringProperty fstRegDt;

	public TbmSysDaoMethodsHDVO() {
		this.histTsp = new SimpleStringProperty();
		this.packageName = new SimpleStringProperty();
		this.className = new SimpleStringProperty();
		this.methodName = new SimpleStringProperty();
		this.resultVoClass = new SimpleStringProperty();
		this.sqlBody = new SimpleStringProperty();
		this.methodDesc = new SimpleStringProperty();
		this.dmlType = new SimpleStringProperty();
		this.fstRegDt = new SimpleStringProperty();
	}

	public void setHistTsp(String histTsp) {
		this.histTsp.set(histTsp);
	}

	public String getHistTsp() {
		return histTsp.get();
	}

	public StringProperty histTspProperty() {
		return histTsp;
	}

	public void setPackageName(String packageName) {
		this.packageName.set(packageName);
	}

	public String getPackageName() {
		return packageName.get();
	}

	public StringProperty packageNameProperty() {
		return packageName;
	}

	public void setClassName(String className) {
		this.className.set(className);
	}

	public String getClassName() {
		return className.get();
	}

	public StringProperty classNameProperty() {
		return className;
	}

	public void setMethodName(String methodName) {
		this.methodName.set(methodName);
	}

	public String getMethodName() {
		return methodName.get();
	}

	public StringProperty methodNameProperty() {
		return methodName;
	}

	public void setResultVoClass(String resultVoClass) {
		this.resultVoClass.set(resultVoClass);
	}

	public String getResultVoClass() {
		return resultVoClass.get();
	}

	public StringProperty resultVoClassProperty() {
		return resultVoClass;
	}

	public void setSqlBody(String sqlBody) {
		this.sqlBody.set(sqlBody);
	}

	public String getSqlBody() {
		return sqlBody.get();
	}

	public StringProperty sqlBodyProperty() {
		return sqlBody;
	}

	public void setMethodDesc(String methodDesc) {
		this.methodDesc.set(methodDesc);
	}

	public String getMethodDesc() {
		return methodDesc.get();
	}

	public StringProperty methodDescProperty() {
		return methodDesc;
	}

	public void setDmlType(String dmlType) {
		this.dmlType.set(dmlType);
	}

	public String getDmlType() {
		return dmlType.get();
	}

	public StringProperty dmlTypeProperty() {
		return dmlType;
	}

	public void setFstRegDt(String fstRegDt) {
		this.fstRegDt.set(fstRegDt);
	}

	public String getFstRegDt() {
		return fstRegDt.get();
	}

	public StringProperty fstRegDtProperty() {
		return fstRegDt;
	}
}
