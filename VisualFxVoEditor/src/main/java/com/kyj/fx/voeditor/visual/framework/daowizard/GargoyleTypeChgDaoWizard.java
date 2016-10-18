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
import kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod;
import kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement;
import kyj.Fx.dao.wizard.core.IExtractDaoClass;
import kyj.Fx.dao.wizard.core.IExtractDaoMethod;
import kyj.Fx.dao.wizard.core.RetrunStatement;
import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class GargoyleTypeChgDaoWizard<C extends ClassMeta, DAO extends TbmSysDaoDVO, M extends TbpSysDaoMethodsDVO, F extends FieldMeta>
		extends DaoWizard<C, M, F> {

	public GargoyleTypeChgDaoWizard(C classMeta, List<M> methods) {
		super(classMeta, methods);
	}

	public GargoyleTypeChgDaoWizard(C classMeta, M[] methods) {
		super(classMeta, methods);
	}

	public GargoyleTypeChgDaoWizard(String className, M... methods) {
		super(className, methods);
	}
	
	

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.DaoWizard#getIExtractClass()
	 */
	@Override
	protected IExtractDaoClass<ClassMeta> getIExtractClass() {
		// TODO Auto-generated method stub
		return super.getIExtractClass();
	}

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.DaoWizard#getIExtractMethod()
	 */
	@Override
	protected IExtractDaoMethod<M> getIExtractMethod() {
		return new BaseFxExtractDaoMethod<M>() {

			/* (non-Javadoc)
			 * @see kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod#daoBaseResultSetStatement(kyj.Fx.dao.wizard.core.model.vo.FxDao, kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO)
			 */
			@Override
			protected DaoBaseResultSetStatement<BaseResultMapper<M>, M> daoBaseResultSetStatement(FxDao vo, M t) {
				BaseResultMapper<M> baseResultMapper = baseResultMapper(vo, t);
				RetrunStatement returnStatement = new GargoyleRetrunStatement();
				return new GargoyleDaoBaseFxExtractDaoMethod<>(baseResultMapper, returnStatement, 2);
			}
		};
	}

}
