package com.kyj.fx.voeditor.core.model.meta;

/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core.model.meta
 *	작성일   : 2015. 10. 20.
 *	작성자   : KYJ
 *******************************/
import java.util.List;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;

/**
 * DAO Wizard내의 코드를 기술하기 위한 메타관리 클래스
 * 
 * @author KYJ
 *
 */
public class MethodMeta {

	private ClassMeta parent;
	public MethodMeta(ClassMeta parent) {
		this.parent = parent;
	}

	/**
	 * 메서드 명을 정의
	 */
	private String name;

	/**
	 * 접근지정자등이 포함되는 modifier
	 */
	private int modifier;

	/**
	 * 메소드 파라미터
	 */
	private List<ClassMeta> parameters;
	/**
	 * 리턴타입
	 */
	private ClassMeta returnType;

	/**
	 * 예외처리 클래스
	 * 
	 * 하나라고만 가정하고 처리
	 */
	private Exception exceptionClass;

	/**
	 * 메소드내용을 기술하는 body부
	 */
	private String body;

	/**
	 * KYJ
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * KYJ
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * KYJ
	 * 
	 * @return the modifier
	 */
	public int getModifier() {
		return modifier;
	}

	/**
	 * KYJ
	 * 
	 * @param modifier
	 *            the modifier to set
	 */
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	/**
	 * KYJ
	 * 
	 * @return the parameters
	 */
	public List<ClassMeta> getParameters() {
		return parameters;
	}

	/**
	 * KYJ
	 * 
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(List<ClassMeta> parameters) {
		this.parameters = parameters;
	}

	/**
	 * KYJ
	 * 
	 * @return the returnType
	 */
	public ClassMeta getReturnType() {
		return returnType;
	}

	/**
	 * KYJ
	 * 
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(ClassMeta returnType) {
		this.returnType = returnType;
	}

	/**
	 * KYJ
	 * 
	 * @return the exceptionClass
	 */
	public Exception getExceptionClass() {
		return exceptionClass;
	}

	/**
	 * KYJ
	 * 
	 * @param exceptionClass
	 *            the exceptionClass to set
	 */
	public void setExceptionClass(Exception exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/**
	 * KYJ
	 * 
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * KYJ
	 * 
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the parent
	 */
	public ClassMeta getParent() {
		return parent;
	}

}
