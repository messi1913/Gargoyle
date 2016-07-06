/**
 * KYJ
 * 2015. 10. 11.
 */
package kyj.Fx.dao.wizard.core;

import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.IExtractModel;

/**
 * VO를 생성하기 위한 클래스처리
 * 
 * @author KYJ
 *
 */
public interface IExtractDaoClass<T> extends IExtractModel<FxDao, T> {

	/**
	 * VO를 생성하기 위한 클래스처리
	 * 
	 * @Date 2015. 10. 11.
	 * @param vo
	 * @param t
	 * @User KYJ
	 */
	public int work(FxDao vo, T t);

}
