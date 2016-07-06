/**
 * KYJ
 * 2015. 10. 11.
 */
package kyj.Fx.dao.wizard.core;

import java.util.List;

import kyj.Fx.dao.wizard.core.model.vo.FxDao;

import com.kyj.fx.voeditor.core.IExtractModel;

/**
 * @author KYJ
 *
 */
public interface IExtractDaoMethod<T> extends IExtractModel<FxDao, T> {

	/**
	 * 메소드 항목들을 추출
	 * 
	 * @Date 2015. 10. 20.
	 * @param vo
	 * @param methods
	 * @throws Exception
	 * @User KYJ
	 */
	public void work(FxDao vo, List<T> methods);

}
