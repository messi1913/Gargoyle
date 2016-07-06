/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * SCMITEM 정의
 *
 * @author KYJ
 *
 */
public class SVNFileItem extends SVNItem {

	public SVNFileItem(JavaSVNManager manager) {
		super(manager);
	}

	public SVNFileItem(String path, String simpleName, JavaSVNManager manager) {
		super(path, simpleName, manager);
	}

	public SVNFileItem(String path, JavaSVNManager manager) {
		super(path, manager);
	}

	@Override
	public boolean isDir() {
		return false;
	}

}
