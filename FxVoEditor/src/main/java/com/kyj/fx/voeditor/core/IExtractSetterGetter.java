/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * @author KYJ
 *
 */
public interface IExtractSetterGetter<T> extends IExtractModel<FxVo, T> {
	/**
	 * VO를 생성하기 위한 필드처리
	 * 
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param t
	 * @User KYJ
	 */
	public void work(FxVo vo, FieldMeta... t);
}
