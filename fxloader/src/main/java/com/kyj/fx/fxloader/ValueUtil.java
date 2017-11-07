/********************************
 *	프로젝트 : fxloader
 *	패키지   : com.kyj.fx.fxloader
 *	작성일   : 2017. 11. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.output.StringBuilderWriter;

/**
 * @author KYJ
 *
 */
class ValueUtil {

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

	public static String toString(Throwable e) {
		return toString(null, e);
	}

	/**
	 * 에러 메세지 상세화
	 *
	 * @param title
	 *            메세지 타이틀
	 * @param e
	 * @return
	 */
	public static String toString(String title, Throwable e) {
		if (e == null)
			return "[warnning] Exception is null";

		String errMsg = "";
		try (StringBuilderWriter sbw = new StringBuilderWriter()) {
			try (PrintWriter printWriter = new PrintWriter(sbw, true)) {
				if (title != null)
					printWriter.write("#############  ".concat(title).concat("  ##############\n"));
				e.printStackTrace(printWriter);
			}
			errMsg = sbw.toString();
		}
		return errMsg;
	}
}
