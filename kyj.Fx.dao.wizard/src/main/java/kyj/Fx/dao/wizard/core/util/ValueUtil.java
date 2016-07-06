/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 16.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.util;

/**
 * @author KYJ
 *
 */
public class ValueUtil {

	/**
	 * 에러 메세지 상세화
	 *
	 * @param e
	 * @return
	 */
	public static String toString(Throwable e) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(e.getMessage()).append("\n");
		for (StackTraceElement s : stackTrace) {
			sb.append(s.getClassName()).append(".").append(s.getMethodName()).append("[").append(s.getLineNumber()).append("]\n");
		}
		return sb.toString();
	}

}
