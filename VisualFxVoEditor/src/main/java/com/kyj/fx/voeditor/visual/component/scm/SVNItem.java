/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;

import com.kyj.fx.voeditor.visual.util.DateUtil;
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

	private static final Comparator<SVNDirEntry> BASIC_COMPAREABLE = (e1, e2) -> {
		SVNNodeKind kind1 = e1.getKind();
		SVNNodeKind kind2 = e2.getKind();
		if (SVNNodeKind.DIR == kind1 && SVNNodeKind.DIR != kind2)
			return -1;
		else if (SVNNodeKind.DIR != kind1 && SVNNodeKind.DIR == kind2)
			return 1;


		return e1.getName().compareTo(e2.getName());
	};

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
//		String svnUrl = manager.getUrl();
		String _path = path;

		/*
		 * 17.12.20 상대주소기준으로 처리될 수 있도록 코드 수정
		 * 
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
//		_path = SVNURL.parseURIEncoded(svnUrl).appendPath(path, true);
//		_path = JavaSVNManager.relativePath(svnUrl, _path, true);
		
		//		if(_path.startsWith("/"))
		//			_path = _path.substring(1);

		List<SVNDirEntry> list = manager.listEntry(_path, "-1", false, BASIC_COMPAREABLE,
				ex -> DialogUtil.showExceptionDailog(ex, "SVN Connection Fail"));
		
		// For Loop
		List<SVNItem> collect = list.stream().map(p -> {
			String rootUrl = manager.getRootUrl();
			
			String svnPath = p.getURL().getPath();
//			String url = p.getURL().toString();
			
			
			try {
				svnPath = SVNURLUtil.getRelativeURL(SVNURL.parseURIEncoded(rootUrl) , p.getURL() , true);
			} catch (SVNException e) {
				e.printStackTrace();
			}
//			svnPath = JavaSVNManager.relativePath( rootUrl, svnPath, true);
//			svnPath = url.replaceFirst(svnUrl, "");
			
			long revision = p.getRevision();
			String name = p.getName();
			String author = p.getAuthor();
			Date date = p.getDate();
			SVNItem svnItem = null;

			// scm에서 디렉토리인경우 /로 끝남.
			SVNNodeKind kind = p.getKind();
			if (kind == SVNNodeKind.DIR) {
				svnItem = new SVNDirItem(svnPath, name, manager);
				svnItem.setDir(true);
				LOGGER.debug("{} .... Dir {}", name, true);
			} else {
				svnItem = new SVNFileItem(svnPath, name, manager);
				svnItem.setDir(false);
				LOGGER.debug("{} .... File {}", name, true);
			}
			svnItem.setAuthor(author);
			svnItem.setRevision(revision);
			svnItem.setDate(date);
			return svnItem;
		}).collect(Collectors.toList());
		return collect;
	}

	private Date date;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 17.
	 * @param date
	 */
	private void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public final Date getDate() {
		return date;
	}

	private String author;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 16.
	 * @param author
	 */
	private void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the author
	 */
	public final String getAuthor() {
		return author;
	}

	private long revision;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 13.
	 * @param revision
	 */
	private void setRevision(long revision) {
		this.revision = revision;
	}

	public final long getRevision() {
		return this.revision;
	}

	public final long getLatestRevision() throws SVNException {

		if (this.manager == null)
			return -1;

		return this.manager.getLatestRevision();
	}

	@Override
	public String toString() {

		String dateAsStr = DateUtil.getDateAsStr(this.date, DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM);
		return String.format("%s   [%s] [%d] [%s]", simpleName, this.author, this.revision, dateAsStr);
	}

}
