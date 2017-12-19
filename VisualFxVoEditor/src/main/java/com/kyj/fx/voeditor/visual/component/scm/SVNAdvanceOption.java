/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2017. 12. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

/**
 * @author KYJ
 *
 */
public class SVNAdvanceOption {

	private int revision = -1;

	private String message;

	/**
	 */
	public SVNAdvanceOption() {

	}

	public final int getRevision() {
		return revision;
	}

	public final void setRevision(int revision) {
		this.revision = revision;
	}

	public final String getMessage() {
		return message;
	}

	public final void setMessage(String message) {
		this.message = message;
	}

}
