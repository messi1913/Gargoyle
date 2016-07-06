/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * Generate VO Class
 * 
 * @author KYJ
 *
 */
public class VoEditor {

	private String className;
	private ClassMeta classMeta;
	private FieldMeta[] fields;
	private FxVo fxVo;

	/**
	 * 생성자 KYJ
	 * 
	 * @param className
	 * @param fields
	 */
	public VoEditor(String className, FieldMeta... fields) {
		this(new ClassMeta(className), fields);
	}

	/**
	 * 생성자 KYJ
	 * 
	 * @param className
	 * @param fields
	 */
	public VoEditor(ClassMeta classMeta, List<FieldMeta> fields) {
		FieldMeta[] arr = new FieldMeta[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			FieldMeta fieldMeta = fields.get(i);
			arr[i] = fieldMeta;
		}
		checkVarName(arr);
		this.className = classMeta.getName();
		this.classMeta = classMeta;
		this.fields = arr;
		this.fxVo = new FxVo();
	}

	/**
	 * 생성자 KYJ
	 * 
	 * @param classMeta
	 * @param fields
	 */
	public VoEditor(ClassMeta classMeta, FieldMeta... fields) {
		this.fxVo = new FxVo();
		this.classMeta = classMeta;
		this.className = classMeta.getName();
		this.fields = fields;

		checkVarName(fields);
	}

	/**
	 * 중복되는 변수명이 존재하는지를 체크한다.
	 * 
	 * @Date 2015. 10. 20.
	 * @param fields
	 * @User KYJ
	 */
	private void checkVarName(FieldMeta... fields) {
		Set<String> set = new HashSet<String>();
		for (FieldMeta meta : fields) {
			if (set.contains(meta.getName())) {
				throw new IllegalArgumentException("duplicated parameter names");
			}
			set.add(meta.getName());
		}
	}

	/**
	 * 객체생성후 반드시 build함수를 사용하여 메타정보를 생성할것.
	 *
	 * @Date 2015. 10. 11.
	 * @User KYJ
	 */
	public void build() {
		setPackage(classMeta);
		getIExtractClass().work(this.fxVo, classMeta);
		getIExtractField().work(this.fxVo, fields);
		getIExtractConstruct().work(this.fxVo, classMeta, fields);
		getIExtractSetterGetter().work(this.fxVo, fields);

		this.fxVo.setBuild(true);
	}

	private void setPackage(ClassMeta classMeta) {

		String packageName = classMeta.getPackageName();
		if (packageName == null || packageName.isEmpty())
			return;

		this.fxVo.getPackagePart().append("package ").append(packageName).append(";\n");

	}

	/**
	 * 필드 정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractField<FieldMeta> getIExtractField() {
		return new BaseFxExtractField();
	}

	/**
	 * 필드 정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractConstructor<FieldMeta> getIExtractConstruct() {
		return new BaseFxExtractConstructor();
	}

	/**
	 * 클래스정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractClass<ClassMeta> getIExtractClass() {
		return new BaseFxExtractClass();
	}

	private IExtractSetterGetter<FieldMeta> getIExtractSetterGetter() {
		return new BaseFxExtractSetterGetter();
	}

	public String getClassName() {
		return className;
	}

	/**
	 * 결과를 텍스트로 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @throws Exception
	 * @User KYJ
	 */
	public String toText() throws Exception {

		if (!this.fxVo.isBuild())
			throw new IllegalStateException("first call Build func...");
		return fxVo.toString();
	}

	/**
	 * 결과를 파일로 반환
	 *
	 * @Date 2015. 10. 12.
	 * @param filePathName
	 * @return
	 * @throws Exception
	 * @User KYJ
	 */
	public File toFile(String filePathName) throws Exception {

		if (!this.fxVo.isBuild())
			throw new IllegalStateException("first call Build func...");

		String fileName = getClassName();
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePathName + File.separator + fileName + ".java");
			writer.write(toText());
		} finally {
			if (writer != null)
				writer.close();
		}

		return new File(filePathName);
	}
}
