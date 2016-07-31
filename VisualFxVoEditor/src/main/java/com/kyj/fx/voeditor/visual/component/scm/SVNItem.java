/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * SCMITEM 정의
 *
 * @author KYJ
 *
 */
public class SVNItem implements SCMItem<SVNItem> {


	private static Logger LOGGER = LoggerFactory.getLogger(SVNItem.class);
	public String path;

	public String simpleName;

	public boolean isDir;

	private JavaSVNManager manager;

	/**
	 * @param manager
	 */
	public SVNItem(JavaSVNManager manager) {
		this.manager = manager;
	}

	/**
	 * @param path
	 * @param manager
	 */
	public SVNItem(String path, JavaSVNManager manager) {
		this.manager = manager;
		this.path = path;
	}

	/**
	 * @param path
	 * @param simpleName
	 * @param manager
	 */
	public SVNItem(String path, String simpleName, JavaSVNManager manager) {
		this.manager = manager;
		this.path = path;
		this.simpleName = simpleName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}

	/**
	 * @return the manager
	 */
	public final JavaSVNManager getManager() {
		return manager;
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public List<SVNItem> getChildrens() {
		String svnUrl = manager.getUrl();
		String _path = path;

		/*
		 * fix bug -> SVNInitLoader.java
		 *
		 * 버그 수정 SVN주소를 입력해도 루트디렉토리 밑의 요소들이 트리 요소로 화면에 출력되는 버그 수정
		 *
		 * as-is ]
		 * new SVNRepository("/", url.toString(), manager);
		 *
		 * to-be]
		 * new SVNRepository("", url.toString(), manager);
		 */

//		if(_path.startsWith("/"))
//			_path = _path.substring(1);

		List<SVNDirEntry> list = manager.listEntry(_path, ex -> DialogUtil.showExceptionDailog(ex, "SVN Connection Fail"));
		List<SVNItem> collect = list.stream().map(p -> {
			String svnPath = p.getURL().getPath();
			String url = p.getURL().toString();
			svnPath = url.replaceFirst(svnUrl, "");

			String name = p.getName();
			SVNItem svnItem = null;

			// scm에서 디렉토리인경우 /로 끝남.
			SVNNodeKind kind = p.getKind();
			if (kind == SVNNodeKind.DIR) {
				svnItem = new SVNDirItem(svnPath, name, manager);
				svnItem.setDir(true);
				LOGGER.info("{} .... Dir {}", name, true);
			} else {
				svnItem = new SVNFileItem(svnPath, name, manager);
				svnItem.setDir(false);
				LOGGER.info("{} .... File {}", name, true);
			}

			return svnItem;
		}).collect(Collectors.toList());
		return collect;
	}

	@Override
	public String toString() {
		return simpleName;
	}

}
