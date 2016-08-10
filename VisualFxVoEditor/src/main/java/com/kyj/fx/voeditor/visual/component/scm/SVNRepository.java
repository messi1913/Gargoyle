/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * SVN 저장소 정보를 갖는다.
 *
 * @author KYJ
 *
 */
public class SVNRepository extends SVNItem {

	public SVNRepository(String path, String simpleName, JavaSVNManager manager) {
		super(path, simpleName, manager);
	}

	public SVNRepository(JavaSVNManager manager) {
		super("/", manager.getUrl(), manager);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isDir() {
		return true;
	}

	public String getURL() {
		JavaSVNManager manager = super.getManager();
		return manager.getUrl();
	}

	public Object getUserId() {
		JavaSVNManager manager = super.getManager();
		return manager.getUserId();
	}

	@Override
	public String toString() {
		return simpleName;
	}

}
