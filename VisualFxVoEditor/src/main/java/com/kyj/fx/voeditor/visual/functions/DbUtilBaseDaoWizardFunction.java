/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2015. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.util.function.BiFunction;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.framework.daowizard.GargoyledDbUtilDaoWizard;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class DbUtilBaseDaoWizardFunction<C extends ClassMeta, DAO extends TbmSysDaoDVO, M extends TbpSysDaoMethodsDVO, F extends FieldMeta>
		implements BiFunction<C, DAO, DaoWizard<C, M, F>> {

	@Override
	public DaoWizard<C, M, F> apply(C t, DAO vo) {

		String className = t.getName();
		String packageName = t.getPackageName();
		Class<?> extendsBaseClass = t.getExtendClassName();

		vo.setClassName(className);
		vo.setPackageName(packageName);
		vo.setExtendsClassName(extendsBaseClass == null ? "" : extendsBaseClass.getName());

		//		DaoWizard<C, M, F> daowizard = (DaoWizard<C, M, F>) new TypeChgDaoWizard<>(t, vo.getTbpSysDaoMethodsDVOList());

		DaoWizard<C, M, F> daowizard = new GargoyledDbUtilDaoWizard(t, vo.getTbpSysDaoMethodsDVOList());

		daowizard.build();
		return daowizard;
	}

}
