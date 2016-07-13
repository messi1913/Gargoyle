/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 7. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.internal.wc.SVNCommitUtil;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.NetworkUtil;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
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
	private static String getCommitMessage(String message) throws SVNException {
		String systemMessage = String.format(DEFAULT_COMMIT_FORMAT, getCurrentDateTime(), getIpAddr());
		return message == null ? systemMessage : systemMessage.concat("\n").concat(message);
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
		return addFileCommit(dirPath, filePath, new ByteArrayInputStream(data), commitMessage);
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
		ISVNEditor editor = getDefaultSVNEditor(commitMessage);
		return addFile(editor, dirPath /*fullPath*/ , dirPath.concat("/").concat(fileName), uploadStream);
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
		String convertCommitMessage = getCommitMessage(commitMessage);

		convertCommitMessage = SVNCommitUtil.validateCommitMessage(convertCommitMessage);
		SVNProperties revisionProperties = new SVNProperties();
		revisionProperties.put("exec.ip.addr", getIpAddr());
		revisionProperties.put("exec.client.date", getCurrentDateTime());
		revisionProperties.put("exec.user", getUserId());
		return getRepository().getCommitEditor(convertCommitMessage, null /*locks*/ , true /*keepLocks*/ , revisionProperties, null /*mediator*/ );
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

		ISVNEditor editor = getDefaultSVNEditor(commitMessage);
		SVNCommitInfo modifyFile = SVNCommitInfo.NULL;

		try {
			modifyFile = modifyFile(editor, dirPath, dirPath.concat("/").concat(fileName), oldData, newData);
		} finally {
			close(oldData);
			close(newData);
		}

		return modifyFile;

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

		/*
		 * Parameters:
		 * paths 				:: paths to commit
		 * keepLocks 			:: whether to unlock or not files in the repository
		 * commitMessage 	:: commit log message
		 * revisionProperties :: custom revision properties
		 * changelists 			:: changelist names array
		 * keepChangelist 	:: whether to remove changelists or not
		 * force 					:: true to force a non-recursive commit; if depth is SVNDepth.INFINITY the force flag is ignored
		 * depth 				:: tree depth to processReturns: information about the new committed revision
		 */

		return getSvnManager().getCommitClient().doCommit(paths, false, commitMessage, null, null, false, true, SVNDepth.INFINITY);

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
		editor.openRoot(-1);
		editor.openDir(dirPath, -1);
		editor.openFile(fileName, -1);
		editor.applyTextDelta(fileName, null);

		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(fileName, oldData, 0, newData, editor, true);

		//Closes filePath.
		editor.closeFile(fileName, checksum);
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
	 *    remote server relative path.
	 * @param fileName
	 *     only SimpleFileName
	 * @param dataStream
	 * @return
	 * @throws SVNException
	 */
	private static SVNCommitInfo addFile(ISVNEditor editor, String dirPath, String fileName, InputStream dataStream) throws SVNException {
		editor.openRoot(-1);
		editor.openDir(dirPath, -1);
		//		editor.addDir("", null, -1);;
		editor.addFile(fileName, null, -1);

		editor.applyTextDelta(fileName, null);

		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(fileName, dataStream, editor, true);

		editor.closeFile(fileName, checksum);

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
