/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core.model.meta
 *	작성일   : 2015. 10. 20.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kyj.Fx.dao.wizard.core.BaseFxExtractDaoClass;
import kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod;
import kyj.Fx.dao.wizard.core.IExtractDaoClass;
import kyj.Fx.dao.wizard.core.IExtractDaoMethod;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

import com.kyj.fx.voeditor.core.BaseFxExtractConstructor;
import com.kyj.fx.voeditor.core.BaseFxExtractField;
import com.kyj.fx.voeditor.core.IExtractConstructor;
import com.kyj.fx.voeditor.core.IExtractField;
import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.meta.MethodMeta;

/**
 * Generate VO Class
 * 
 * @author KYJ
 *
 */
public class DaoWizard<C extends ClassMeta, M extends TbpSysDaoMethodsDVO, F extends FieldMeta> {

	private String className;
	private C classMeta;
	private List<M> methods;
	/**
	 * daoWizard에서 fields는 사용할일이 없을것같음. 하지만 만약을 위해 임시적으로 생성.
	 * 
	 * @최초생성일 2015. 10. 28.
	 */
	// private F[] fields;
	private FxDao fxDao;

	/**
	 * 생성자 KYJ
	 * 
	 * @param className
	 * @param methods
	 */
	@SuppressWarnings("unchecked")
	public DaoWizard(String className, M... methods) {
		this((C) new ClassMeta(className), methods);
	}

	/**
	 * 생성자 KYJ
	 * 
	 * @param className
	 * @param methods2
	 */
	public DaoWizard(C classMeta, M[] methods) {
		this(classMeta, Arrays.asList(methods));
	}

	/**
	 * 생성자 KYJ
	 * 
	 * @param classMeta
	 * @param methods
	 */
	public DaoWizard(C classMeta, List<M> methods) {
		checkVarName(methods);
		this.className = classMeta.getName();
		this.classMeta = classMeta;
		this.methods = methods;
		this.fxDao = new FxDao();
	}

	/**
	 * @Date 2015. 10. 20.
	 * @param methods
	 * @User KYJ
	 */
	private void checkVarName(List<M> methods) {
		Set<String> set = new HashSet<String>();
		for (MethodMeta meta : methods) {
			String name = meta.getName();
			if (name == null)
				continue;
			if (set.contains(name)) {
				throw new IllegalArgumentException("duplicated parameter names");
			}
			set.add(name);
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
		getIExtractClass().work(this.fxDao, classMeta);
		// getIExtractField().work(this.fxVo, fields);
		// getIExtractConstruct().work(this.fxDao, classMeta, fields);
		getIExtractMethod().work(this.fxDao, methods);

		this.fxDao.setBuild(true);
	}

	private void setPackage(ClassMeta classMeta) {

		String packageName = classMeta.getPackageName();
		if (packageName == null || packageName.isEmpty())
			return;

		this.fxDao.getPackagePart().append("package ").append(packageName).append(";\n");

	}

	/**
	 * 클래스정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractDaoClass<ClassMeta> getIExtractClass() {
		return new BaseFxExtractDaoClass();
	}

	/**
	 * 필드 정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractField getIExtractField() {
		return new BaseFxExtractField();
	}

	/**
	 * 필드 정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractConstructor getIExtractConstruct() {
		return new BaseFxExtractConstructor();
	}

	/**
	 * 메소드정보 추출 클래스 반환
	 *
	 * @Date 2015. 10. 11.
	 * @return
	 * @User KYJ
	 */
	private IExtractDaoMethod<M> getIExtractMethod() {
		return new BaseFxExtractDaoMethod<M>();
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

		if (!this.fxDao.isBuild())
			throw new IllegalStateException("first call Build func...");
		return fxDao.toString();
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

		if (!this.fxDao.isBuild())
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

	public void saveDatabase() {

	}
}
