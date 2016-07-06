/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2015. 12. 17.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.memory;

import java.util.Set;
import java.util.Map.Entry;

/**
 * 파일 기반 설정 클래스
 *
 * @author KYJ
 *
 */
public interface IFileBaseConfiguration {
	public String getFileName();

	public Set<Entry<Object, Object>> getEntry();
}
