/**
 * KYJ
 * 2015. 10. 11.
 */
package kyj.Fx.dao.wizard.core;

import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.IExtractModel;

/**
 * VO를 생성하기 위한 필드처리
 * 
 * @author KYJ
 *
 */
public interface IExtractDaoField<F> extends IExtractModel<FxDao, F> {

	/**
	 * VO를 생성하기 위한 필드처리
	 * 
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param t
	 * @User KYJ
	 */
	public void work(FxDao vo, F... t);
}
