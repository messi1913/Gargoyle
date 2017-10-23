/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.util.function.Function;

import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.util.DateUtil;

/**
 * @author KYJ
 *
 */
public interface SVNFormatter {

	/**
	 *
	 * 출력용 포멧.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @return
	 */
	default public Function<? super SVNLogEntry, ? extends String> fromPrettySVNLogConverter() {
		return logEntry -> {

			return String.format("rivision :: %d date :: %s author :: %s message :: %s \n", logEntry.getRevision(),
					DateUtil.getDateString(logEntry.getDate()), logEntry.getAuthor(), logEntry.getMessage());

		};
	}
}
