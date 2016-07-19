/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Date;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

/**
 * @author KYJ
 *
 */
public class GargoyleSVNLogEntryPath extends SVNLogEntryPath {

	/**
	 * @최초생성일 2016. 7. 19.
	 */
	private static final long serialVersionUID = 3379664478987623088L;

	/**
	 * Commit Date.
	 * @최초생성일 2016. 7. 19.
	 */
	private Date date;

	public GargoyleSVNLogEntryPath(String path, char type, String copyPath, long copyRevision, SVNNodeKind kind) {
		super(path, type, copyPath, copyRevision, kind);
	}

	public GargoyleSVNLogEntryPath(String path, char type, String copyPath, long copyRevision) {
		super(path, type, copyPath, copyRevision);
	}

	/**
	 * @return the date
	 */
	public final Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public final void setDate(Date date) {
		this.date = date;
	}

	/**
	* Calculates and returns a hash code for this object.
	*
	* @return a hash code
	*/
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((getPath() == null) ? 0 : getPath().hashCode());
		result = PRIME * result + (int) (getCopyRevision() ^ (getCopyRevision() >>> 32));
		return result;
	}

	/**
	 * Compares this object with another one.
	 *
	 * @param  obj  an object to compare with
	 * @return      <span class="javakeyword">true</span>
	 *              if this object is the same as the <code>obj</code>
	 *              argument
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof SVNLogEntryPath)) {
			return false;
		}
		final SVNLogEntryPath other = (SVNLogEntryPath) obj;
		return getCopyRevision() == other.getCopyRevision() && getPath().equals(other.getPath());
	}

}
