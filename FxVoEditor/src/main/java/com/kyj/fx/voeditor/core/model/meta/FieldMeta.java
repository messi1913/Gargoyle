/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core.model.meta;

import javafx.beans.property.Property;

/**
 * 필드를 생성하기 위한 기본정보
 * 
 * @author KYJ
 *
 */
public class FieldMeta {

	/**
	 * UI 표현명
	 */
	private String alias;

	/**
	 * 기본키속성인지 여부
	 */
	private boolean primarykey;

	/**
	 * 메타정보
	 */
	private int modifier;
	/**
	 * 필드명
	 */
	private String name;
	/**
	 * 필드타입
	 */
	private Class<?> fieldType;

	/**
	 * 인스턴스 타입
	 * 
	 * @최초생성일 2015. 10. 15.
	 */
	private Class<?> instanceType;

	public FieldMeta(Class<?> fieldType) {
		super();
		this.fieldType = fieldType;
	}

	public FieldMeta(Class<?> fieldType, Class<?> instanceType) {
		super();
		this.fieldType = fieldType;
		this.instanceType = instanceType;
	}

	public FieldMeta(int modifier, String name, Class<?> fieldType) {
		super();
		this.modifier = modifier;
		this.name = name;
		this.fieldType = fieldType;
	}

	public FieldMeta(int modifier, String name, Class<?> fieldType, Class<?> instanceType) {
		super();
		this.modifier = modifier;
		this.name = name;
		this.fieldType = fieldType;
		this.instanceType = instanceType;
	}

	/**
	 * 자바fx 프로퍼티 베이스인지 확인
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	public boolean isJavaFxProperty() {
		return Property.class.isAssignableFrom(fieldType);
	}

	public Class<?> getFieldType() {
		return fieldType;
	}

	public void setFieldType(Class<?> fieldType) {
		this.fieldType = fieldType;
	}

	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the instanceType
	 */
	public Class<?> getInstanceType() {
		return instanceType;
	}

	/**
	 * @param instanceType
	 *            the instanceType to set
	 */
	public void setInstanceType(Class<?> instanceType) {
		this.instanceType = instanceType;
	}

	/**
	 * KYJ
	 * 
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * KYJ
	 * 
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * KYJ
	 * 
	 * @return the primarykey
	 */
	public boolean isPrimarykey() {
		return primarykey;
	}

	/**
	 * KYJ
	 * 
	 * @param primarykey
	 *            the primarykey to set
	 */
	public void setPrimarykey(boolean primarykey) {
		this.primarykey = primarykey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FieldMeta [modifier=" + modifier + ", name=" + name + ", fieldType=" + fieldType + ", instanceType=" + instanceType + "]";
	}

}
