/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 16.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author KYJ
 *
 */
class ValueUtil {

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

	public static boolean isEmpty(Object obj) {
		return !isNotEmpty(obj);
	}
	
	public static boolean isNotEmpty(Object obj) {
		boolean flag = true;
		if (obj != null) {
			if (obj instanceof String) {
				String valueOf = obj.toString().trim();
				flag = valueOf.length() > 0 && valueOf != "" && !valueOf.equals("null");
			} else if (obj instanceof Collection) {
				Collection<?> list = (Collection<?>) obj;
				flag = !list.isEmpty();

				// flag = list.size() > 0;
			} else if (obj instanceof Map) {

				Map<?, ?> map = (Map<?, ?>) obj;
				flag = map.size() > 0;
			}
		} else {
			flag = false;
		}
		return flag;

	}

}
