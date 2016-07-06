/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core.model.meta;

import java.lang.reflect.Modifier;

/**
 * @author KYJ
 *
 */
public class ClassMeta {

	private String packageName;

	/**
	 * 메타정보 KYJ
	 */
	private int modifier;

	/**
	 * 클래스명
	 */
	private String name;

	/**
	 * 상속클래스
	 */
	private Class<?> extendClassName;

	/**
	 * 로딩되지 않는 Class<?> 인경우 String 문자열로 대체
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String extendClassNameStr;

	/**
	 * 인터페이스 목록
	 */
	private Class<?>[] interfaceNames;

	/**
	 * 클래스 설명
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String desc;

	public ClassMeta(String name) {
		this("", name, null, null);
	}

	public ClassMeta(String packageName, String name) {
		this(packageName, name, null, null);
	}

	public ClassMeta(String packageName, String name, Class<?> extendClassName) {
		this(packageName, name, extendClassName, null);
	}

	public ClassMeta(String name, Class<?> extendClassName) {
		this("", name, extendClassName, null);
	}

	public ClassMeta(String packageName, String name, Class<?> extendClassName, Class<?>[] interfaceNames) {
		this.packageName = packageName;
		this.modifier = Modifier.PUBLIC;
		this.extendClassName = extendClassName;
		this.interfaceNames = interfaceNames;
		this.name = name;

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

	public Class<?> getExtendClassName() {
		return extendClassName;
	}

	public void setExtendClassName(Class<?> extendClassName) {
		this.extendClassName = extendClassName;
	}

	public Class<?>[] getInterfaceNames() {
		return interfaceNames;
	}

	public void setInterfaceNames(Class<?>[] interfaceNames) {
		this.interfaceNames = interfaceNames;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the extendClassNameStr
	 */
	public String getExtendClassNameStr() {
		return extendClassNameStr;
	}

	/**
	 * @param extendClassNameStr
	 *            the extendClassNameStr to set
	 */
	public void setExtendClassNameStr(String extendClassNameStr) {
		this.extendClassNameStr = extendClassNameStr;
	}

}
