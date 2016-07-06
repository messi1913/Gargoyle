/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core.model.vo;

/**
 * @author KYJ
 *
 */
public class FxVo implements Cloneable {
	private boolean isBuild;
	private StringBuffer packagePart;
	private StringBuffer importPart;
	private StringBuffer classPart;
	private StringBuffer constructPart;
	private StringBuffer fieldPart;
	private StringBuffer propertyPart;
	private StringBuffer methodPart;
	private StringBuffer toStringPart;

	public FxVo() {
		super();
		this.packagePart = new StringBuffer();
		this.importPart = new StringBuffer();
		this.classPart = new StringBuffer();
		this.constructPart = new StringBuffer();
		this.fieldPart = new StringBuffer();
		this.propertyPart = new StringBuffer();
		this.methodPart = new StringBuffer();
		this.toStringPart = new StringBuffer();
	}

	public StringBuffer getPackagePart() {
		return packagePart;
	}

	public void setPackagePart(StringBuffer packagePart) {
		this.packagePart = packagePart;
	}

	public StringBuffer getImportPart() {
		return importPart;
	}

	public void setImportPart(StringBuffer importPart) {
		this.importPart = importPart;
	}

	public StringBuffer getClassPart() {
		return classPart;
	}

	public void setClassPart(StringBuffer classPart) {
		this.classPart = classPart;
	}

	public StringBuffer getFieldPart() {
		return fieldPart;
	}

	public void setFieldPart(StringBuffer fieldPart) {
		this.fieldPart = fieldPart;
	}

	public StringBuffer getPropertyPart() {
		return propertyPart;
	}

	public void setPropertyPart(StringBuffer propertyPart) {
		this.propertyPart = propertyPart;
	}

	public StringBuffer getMethodPart() {
		return methodPart;
	}

	public void setMethodPart(StringBuffer methodPart) {
		this.methodPart = methodPart;
	}

	public StringBuffer getToStringPart() {
		return toStringPart;
	}

	public void setToStringPart(StringBuffer toStringPart) {
		this.toStringPart = toStringPart;
	}

	public boolean isBuild() {
		return isBuild;
	}

	public void setBuild(boolean isBuild) {
		this.isBuild = isBuild;
	}

	/**
	 * @return the constructPart
	 */
	public StringBuffer getConstructPart() {
		return constructPart;
	}

	/**
	 * @param constructPart
	 *            the constructPart to set
	 */
	public void setConstructPart(StringBuffer constructPart) {
		this.constructPart = constructPart;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(packagePart.toString());
		sb.append(importPart.toString());
		sb.append("\n");
		sb.append(classPart.toString()).append("\n");
		sb.append("{ \n");
		sb.append(fieldPart.toString()).append("\n");
		sb.append(constructPart.toString());
		sb.append(methodPart.toString());
		sb.append("} \n");
		return sb.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
