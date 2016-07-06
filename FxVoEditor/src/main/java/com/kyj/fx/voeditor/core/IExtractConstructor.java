/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.core.model.vo.FxVo;

/**
 * @author KYJ
 *
 */
public interface IExtractConstructor<T> extends IExtractModel<FxVo, T> {
	/**
	 * 생성자를 생성하기 위한 처리
	 * 
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param t
	 * @User KYJ
	 */
	public void work(FxVo vo, ClassMeta classMeta, FieldMeta... t);
}
