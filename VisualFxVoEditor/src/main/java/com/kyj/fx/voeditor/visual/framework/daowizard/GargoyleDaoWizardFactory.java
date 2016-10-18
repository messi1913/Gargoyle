/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.daowizard
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.daowizard;

import java.util.function.BiFunction;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.functions.DbUtilBaseDaoWizardFunction;
import com.kyj.fx.voeditor.visual.functions.TypeChangedGenerateDaoWizardFunction;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class GargoyleDaoWizardFactory {

	public enum Wizardtype {
		meerkatbase, dbutilbase;
	}

	public static BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> get(Wizardtype typeName,
			ClassMeta classMeta, TbmSysDaoDVO tbmSysDaoDVO) {

		BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> result = null;
		switch (typeName) {
		case meerkatbase:
			result = typeChangedGenerateDaoWizardFunction();
			break;
		case dbutilbase:
			result = bbUtilBaseDaoWizardFunction();
			break;
		default:
			result = typeChangedGenerateDaoWizardFunction();
		}

		return result;

	}

	/**
	 * 타입을 변환할 수 있는 기능을 제공하기위한 컨버터
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 26.
	 * @return
	 */
	private static BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> typeChangedGenerateDaoWizardFunction() {
		return new TypeChangedGenerateDaoWizardFunction<ClassMeta, TbmSysDaoDVO, TbpSysDaoMethodsDVO, FieldMeta>();
	}

	/**
	 * 타입을 변환할 수 있는 기능을 제공하기위한 컨버터
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 26.
	 * @return
	 */
	private static BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> bbUtilBaseDaoWizardFunction() {
		return new DbUtilBaseDaoWizardFunction<ClassMeta, TbmSysDaoDVO, TbpSysDaoMethodsDVO, FieldMeta>();
	}

}
