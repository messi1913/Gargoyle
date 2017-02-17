/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNCopySource;

import com.kyj.scm.manager.core.commons.ISCMCommit;
import com.sun.star.uno.RuntimeException;

/**
 *
 * Project 복사.
 *
 * Copy의 경우 Revision 정보까지 복사하고자하는 경우 사용.
 *
 * @author KYJ
 *
 */
public class DimCopy extends AbstractDimmension implements ISCMCommit {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimCopy(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

	public void copy(SVNURL targetURL, SVNURL[] paths) throws Exception {throw new RuntimeException("Not yet support");}

	private SVNCopySource[] convert(SVNURL[] urls) {throw new RuntimeException("Not yet support");}

	private SVNCopySource convert(SVNURL url) {throw new RuntimeException("Not yet support");}
}
