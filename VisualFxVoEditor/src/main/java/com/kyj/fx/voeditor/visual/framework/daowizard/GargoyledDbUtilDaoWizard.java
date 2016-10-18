/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.daowizard;

import java.util.List;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.IExtractDaoMethod;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class GargoyledDbUtilDaoWizard<C extends ClassMeta, DAO extends TbmSysDaoDVO, M extends TbpSysDaoMethodsDVO, F extends FieldMeta>
		extends DaoWizard<C, M, F> {

	public GargoyledDbUtilDaoWizard(C classMeta, List<M> methods) {
		super(classMeta, methods);
	}

	public GargoyledDbUtilDaoWizard(C classMeta, M[] methods) {
		super(classMeta, methods);
	}

	public GargoyledDbUtilDaoWizard(String className, M... methods) {
		super(className, methods);
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.DaoWizard#getIExtractMethod()
	 */
	@Override
	protected IExtractDaoMethod<M> getIExtractMethod() {
		return new GargoyleDbUtilFxExtractDaoMethod<>();
	}

}
