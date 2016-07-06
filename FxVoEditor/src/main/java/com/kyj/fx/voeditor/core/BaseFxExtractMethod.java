/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core
 *	작성일   : 2015. 10. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.core;

/**
 * KYJ
 * 2015. 10. 11.
 */

import com.kyj.fx.voeditor.core.model.meta.MethodMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * @author KYJ
 *
 */
public class BaseFxExtractMethod extends FxVoCommons implements IExtractMethod<MethodMeta> {

	/*
	 * 항목추출 (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractModel#extract(com.kyj.fx.voeditor.core
	 * .model.vo.FxVo, java.lang.Object) KYJ
	 */
	@Override
	public void extract(FxVo vo, MethodMeta t) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.core.IExtractMethod#work(com.kyj.fx.voeditor.core
	 * .model.vo.FxVo, java.lang.Object[]) KYJ
	 */
	@Override
	public void work(FxVo vo, MethodMeta... methodMetas) {
		for (MethodMeta meta : methodMetas) {
			try {
				this.extract(vo, meta);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
