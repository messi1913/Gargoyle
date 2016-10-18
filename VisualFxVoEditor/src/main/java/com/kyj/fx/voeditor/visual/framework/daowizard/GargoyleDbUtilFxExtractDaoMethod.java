/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.parser
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.daowizard;

import kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod;
import kyj.Fx.dao.wizard.core.DaoBaseResultSetStatement;
import kyj.Fx.dao.wizard.core.RetrunStatement;
import kyj.Fx.dao.wizard.core.model.vo.BaseResultMapper;
import kyj.Fx.dao.wizard.core.model.vo.FxDao;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class GargoyleDbUtilFxExtractDaoMethod<M extends TbpSysDaoMethodsDVO> extends BaseFxExtractDaoMethod<M> {

	/* (non-Javadoc)
	 * @see kyj.Fx.dao.wizard.core.BaseFxExtractDaoMethod#daoBaseResultSetStatement(kyj.Fx.dao.wizard.core.model.vo.FxDao, kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO)
	 */
	@Override
	protected DaoBaseResultSetStatement<BaseResultMapper<M>, M> daoBaseResultSetStatement(FxDao vo, M t) {
		BaseResultMapper<M> baseResultMapper = baseResultMapper(vo, t);
		RetrunStatement returnStatement = new GargoyleDbUtilRetrunStatement();
		return new GargoyleDbUtilBaseFxExtractDaoMethod<>(baseResultMapper, returnStatement, 2);
	}
}
