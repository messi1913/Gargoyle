/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 7. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.ISVNCommitHandler;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNCommitItem;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.NetworkUtil;
import com.kyj.scm.manager.core.commons.ISCMCommit;

/**
 *  SVN Commit Operation.
 *
 * @author KYJ
 *
 */
class SVNCommit extends AbstractSVN implements ISCMCommit {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVNCommit.class);
	private static final String DEFAULT_COMMIT_FORMAT = "Commit Date : [%s] \nIp Addr:[%s]\n";

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public SVNCommit(JavaSVNManager javaSVNManager, Properties properties) {

		super(javaSVNManager, properties);

		//		getSvnManager().getCommitClient().setCommitHandler(new ISVNCommitHandler() {
		//

		//
		//			@Override
		//			public SVNProperties getRevisionProperties(String message, SVNCommitItem[] commitables, SVNProperties revisionProperties)
		//					throws SVNException {
		//
		//				return revisionProperties == null ? new SVNProperties() : revisionProperties;
		//			}
		//
		//			@Override
		//			public String getCommitMessage(String message, SVNCommitItem[] commitables) throws SVNException {
		//				int commitCount = commitables == null ? 0 : commitables.length;
		//				String systemMessage = String.format(DEFAULT_COMMIT_FORMAT, getCurrentTime(), getIpAddr(), commitCount);
		//				return message == null ? systemMessage : systemMessage.concat("\n").concat(message);
		//			}
		//
		//			String getCurrentTime() {
		//				return DateUtil.getCurrentDateString();
		//			}
		//
		//			String getIpAddr() {
		//				try {
		//					return NetworkUtil.getRealAddress();
		//				} catch (SocketException e) {
		//					return "ip error";
		//				}
		//			}
		//
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
	private static String getCommitMessage(String message) throws SVNException {
		String systemMessage = String.format(DEFAULT_COMMIT_FORMAT, getCurrentTime(), getIpAddr());
		return message == null ? systemMessage : systemMessage.concat("\n").concat(message);
	}

	/**
	 *  PC 시간 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @return
	 */
	static String getCurrentTime() {
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
	static String getIpAddr() {
		try {
			return NetworkUtil.getRealAddress();
		} catch (SocketException e) {
			return "ip error";
		}
	}

	/**
	 *  SVN Commit Operator
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 6.
	 * @param commitLocalFile
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo addDirCommit(String dirPath, String filePath, byte[] data, String commitMessage) throws SVNException {
		return addDirCommit(dirPath, filePath, new ByteArrayInputStream(data), commitMessage);
	}

	public SVNCommitInfo addDirCommit(String dirPath, String fileName, InputStream uploadStream, String commitMessage) throws SVNException {

		ISVNEditor editor = getDefaultSVNEditor(commitMessage);
		/*
		 * 일반적인 API로는 여러 상황에대한 대처가 불가능함.
		 * 예를들면 디렉토리 생성후 파일추가. 파일삭제후 다른파일추가등에 대한 처리가 안됨.
		 *특히 신규파일생성같은경우 doCommit명령어 에러.
		 *doCommit명령어는 리비젼이 관리되는 상황에서 기존의 파일을 수정하는 처리를 일컬는것같음.
		 */
		//		return commitClient.doCommit(commitLocalFile, false, commitMessage, new SVNProperties(), null, false, true, SVNDepth.INFINITY);

		//		String fullPath = getSvnURL().appendPath(dirPath, false).toString();
		return addDir(editor, dirPath /*fullPath*/ , dirPath.concat("/").concat(fileName), uploadStream);
	}

	/**
	 * DefaultSVNEditor
	 *
	 * Commit행위 처리를 위한 SVN Low Level API.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	private ISVNEditor getDefaultSVNEditor(String commitMessage) throws SVNException {
		return getRepository().getCommitEditor(getCommitMessage(commitMessage), null /*locks*/ , true /*keepLocks*/ , null /*mediator*/ );
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param dirPath
	 * @param fileName
	 * @param oldData
	 * @param newData
	 * @param commitMessage
	 * @return
	 * @throws SVNException
	 */
	public SVNCommitInfo modifyFileCommit(String dirPath, String fileName, InputStream oldData, InputStream newData, String commitMessage)
			throws SVNException {
		ISVNEditor editor = getDefaultSVNEditor(commitMessage);
		return modifyFile(editor, dirPath, dirPath.concat("/").concat(fileName), oldData, newData);

	}

	/**
	 * 수정.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param editor
	 * @param dirPath
	 * @param filePath
	 * @param oldData
	 * @param newData
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo modifyFile(ISVNEditor editor, String dirPath, String filePath, InputStream oldData, InputStream newData)
			throws SVNException {
		editor.openRoot(-1);
		editor.openDir(dirPath, -1);
		editor.openFile(filePath, -1);
		editor.applyTextDelta(filePath, null);
		
		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(filePath, oldData, 0, newData, editor, true);

		//Closes filePath.
		editor.closeFile(filePath, checksum);
		// Closes dirPath.
		editor.closeDir();
		//Closes the root directory.
		editor.closeDir();
		return editor.closeEdit();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param editor
	 * @param dirPath
	 * @param filePath
	 * @param dataStream
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, InputStream dataStream) throws SVNException {
		editor.openRoot(-1);
		editor.openDir(dirPath, -1);
		//		editor.addDir("", null, -1);;
		editor.addFile(filePath, null, -1);

		editor.applyTextDelta(filePath, null);

		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(filePath, dataStream, editor, true);

		editor.closeFile(filePath, checksum);

		//Closes dirPath.
		editor.closeDir();

		//Closes the root directory.
		editor.closeDir();

		return editor.closeEdit();

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
	private static SVNCommitInfo addDir(ISVNEditor editor, String dirPath, String filePath, byte[] data) throws SVNException {
		return addDir(editor, dirPath, filePath, new ByteArrayInputStream(data));
	}

}
