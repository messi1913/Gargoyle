/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.exceptions.GargoyleConnectionFailException;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * @author KYJ
 *
 */
public abstract class AbstracrtSVNHistoryDataSupplier {

	private JavaSVNManager manager;

	private int weekSize;

	private int rankSize = 5;

	public AbstracrtSVNHistoryDataSupplier(JavaSVNManager manager, int weekSize, int rankSize) throws Exception {
		this.manager = manager;
		this.weekSize = weekSize;
		this.rankSize = rankSize;
		ping(manager);
		reload(weekSize);
	}

	/********************************
	 * 작성일 :  2016. 7. 21. 작성자 : KYJ
	 *
	 * 접속가능여부를 체크한다.
	 * 
	 * @param manager
	 * @throws Exception
	 ********************************/
	protected void ping(JavaSVNManager manager) throws Exception {
		try {
			manager.ping();

			if (!manager.isExistsPath("")) {
				throw new GargoyleConnectionFailException("SVN Connect Fail...");
			}
		} catch (SVNException e) {
			throw new GargoyleConnectionFailException("SVN Connect Fail...");
		}

	}

	protected abstract void reload(int weekSize) throws SVNException;

	/**
	 * @return the manager
	 */
	public final JavaSVNManager getManager() {
		return manager;
	}

	/**
	 * @param manager
	 *            the manager to set
	 */
	public final void setManager(JavaSVNManager manager) {
		this.manager = manager;
	}

	/**
	 * @return the allLogs
	 */
	public abstract Collection<SVNLogEntry> getAllLogs();

	/**
	 * @return the weekSize
	 */
	public final int getWeekSize() {
		return weekSize;
	}

	/**
	 * @return the rankSize
	 */
	public final int getRankSize() {
		return rankSize;
	}

}
