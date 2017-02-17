/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.ISVNEditor;

import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.NetworkUtil;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.scm.manager.core.commons.ISCMCommit;
import com.sun.star.uno.RuntimeException;

/**
 *  SVN Commit Operation.
 *
 * @author KYJ
 *
 */
class DimCommit extends AbstractDimmension implements ISCMCommit {

	private static final Logger LOGGER = LoggerFactory.getLogger(DimCommit.class);
	private static final String DEFAULT_COMMIT_FORMAT = "Commit Date : [%s] \nIp Addr:[%s]\n";

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimCommit(DimmensionManager javaSVNManager, Properties properties) {

		super(javaSVNManager, properties);

		//		getSvnManager().getCommitClient().setCommitHandler(new ISVNCommitHandler() {
		//
		//			@Override
		//			public SVNProperties getRevisionProperties(String message, SVNCommitItem[] commitables, SVNProperties revisionProperties)
		//					throws SVNException {
		//				return revisionProperties == null ? new SVNProperties() : revisionProperties;
		//			}
		//
		//			@Override
		//			public String getCommitMessage(String message, SVNCommitItem[] commitables) throws SVNException {
		//				return SVNCommit.getCommitMessage(message);
		//			}
		//		});
		//
		//		getSvnManager().getCommitClient().setEventHandler(new ISVNEventHandler() {
		//
		//			@Override
		//			public void checkCancelled() throws SVNCancelException {
		//				// TODO Auto-generated method stub
		//
		//			}
		//
		//			@Override
		//			public void handleEvent(SVNEvent event, double progress) throws SVNException {
		//				if (SVNEventAction.COMMIT_ADDED == event.getExpectedAction()) {
		//					System.out.println("commit addred");
		//				}
		//
		//			}
		//		});
	}

	/**
	 *
	 *  디폴트 커밋 메세지 등록.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param message
	 * @param commitables
	 * @return
	 * @throws SVNException
	 */
	private static String getCommitMessage(String message) throws Exception {
		throw new RuntimeException("Not yet support");
	}

	/**
	 *  PC 시간 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @return
	 */
	private static String getCurrentDateTime() {
		return DateUtil.getCurrentDateString();
	}

	/**
	 *
	 *  PC IP ADDR 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @return
	 */
	private static String getIpAddr() {
		try {
			return NetworkUtil.getRealAddress();
		} catch (SocketException e) {
			return "ip error";
		}
	}

	/**
	 *  신규파일이 추가되는 경우 사용.
	 *  SVN Commit Operator
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 6.
	 * @param commitLocalFile
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo addFileCommit(String dirPath, String filePath, byte[] data, String commitMessage) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo addDirCommit(String dirPath, String commitMessage) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * 신규파일이 추가되는 경우 사용.
	 *  SVN Commit Operator
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param dirPath
	 *    remote server relative path.
	 * @param fileName
	 *    only SimpleFileName
	 * @param uploadStream
	 *   코드 Stream
	 * @param commitMessage
	 *   Commit Message
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo addFileCommit(String dirPath, String fileName, InputStream uploadStream, String commitMessage)
			throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * DefaultSVNEditor
	 *
	 * Commit행위 처리를 위한 SVN Low Level API.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param commitMessage
	 *   Commit Message
	 * @return
	 * @throws SVNException
	 */
	private ISVNEditor getDefaultSVNEditor(String commitMessage) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 *
	 * 수정된 코드를 서버 반영시 사용.
	 *
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 *    remote server relative path.
	 * @param fileName
	 *     only SimpleFileName
	 * @param oldData
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 * @throws IOException
	 */
	public SVNCommitInfo modifyFileCommit(String dirPath, String fileName, InputStream oldData, InputStream newData, String commitMessage)
			throws SVNException, IOException {

		throw new RuntimeException("Not yet support");

	}

	/**
	 *
	 * FileSystem Base Commit Operation.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param paths
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo commitClient(File[] paths, String commitMessage) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * 수정.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param editor
	 * @param dirPath
	 *   remote server relative path.
	 * @param fileName
	 *   only SimpleFileName
	 * @param oldData
	 * @param newData
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath, String fileName, InputStream oldData, InputStream newData)
			throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	private static SVNCommitInfo addDir(ISVNEditor editor, String dirPath) throws SVNException {
		throw new RuntimeException("Not yet support");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param editor
	 * @param dirPath
	 *    remote server relative path.
	 * @param fileName
	 *     only SimpleFileName
	 * @param dataStream
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo addFile(ISVNEditor editor, String dirPath, String fileName, InputStream dataStream) throws SVNException {
		throw new RuntimeException("Not yet support");

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param editor
	 * @param dirPath
	 * @param filePath
	 * @param data
	 * @return
	 * @throws SVNException
	 */
	//	private static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, byte[] data) throws SVNException {
	//		return addDir(editor, dirPath, filePath, new ByteArrayInputStream(data));
	//	}

	/**
	 * Resource clear.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 13.
	 * @param is
	 */
	static void close(InputStream is) {
		NullExpresion.ifNotNullDo(is, r -> {
			try {
				r.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
