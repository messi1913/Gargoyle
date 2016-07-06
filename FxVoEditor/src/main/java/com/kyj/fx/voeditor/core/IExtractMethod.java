/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * @author KYJ
 *
 */
public interface IExtractMethod<T> extends IExtractModel<FxVo, T> {

	/**
	 * 메소드 항목들을 추출
	 * 
	 * @Date 2015. 10. 20.
	 * @param vo
	 * @param t
	 * @throws Exception
	 * @User KYJ
	 */
	@SuppressWarnings("unchecked")
	public void work(FxVo vo, T... arr);

}
