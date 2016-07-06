/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * VO를 생성하기 위한 클래스처리
 * 
 * @author KYJ
 *
 */
public interface IExtractClass<T> extends IExtractModel<FxVo, T> {

	/**
	 * VO를 생성하기 위한 클래스처리
	 * 
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param t
	 * @User KYJ
	 */
	public void work(FxVo vo, T t);

}
