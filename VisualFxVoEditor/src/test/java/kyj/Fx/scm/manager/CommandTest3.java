/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * pure java svnkit test case
 *
 * @author KYJ
 *
 */
public class CommandTest3 {

	JavaSVNManager testServerManager;
	JavaSVNManager localServerManager;
	JavaSVNManager localServerManager2;

	@Before
	public void setting() throws Exception {
		new ProxyInitializable().initialize();

		{
			Properties properties = new Properties();
			properties.put(JavaSVNManager.SVN_URL, "svn://10.40.41.49/");
			properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
			properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");
			testServerManager = new JavaSVNManager(properties);
		}

		{
			Properties properties = new Properties();
			properties.put(JavaSVNManager.SVN_URL, "svn://localhost/svn/AnimationRecorder");
			properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
			properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

			localServerManager = new JavaSVNManager(properties);
		}

		{
			Properties properties = new Properties();
			properties.put(JavaSVNManager.SVN_URL, "svn://localhost/svn/test/");
			properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
			properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

			localServerManager2 = new JavaSVNManager(properties);
		}

	}

	@Test
	public void importTest() throws Exception {
		testServerManager.doImport("/sos/deprecated_pass-batch-core", SVNURL.parseURIEncoded("svn://localhost/svn/A/trunk/"));
	}

	@Test
	public void addFileTest() throws Exception {
		if (!localServerManager.isExistsPath("test")) {
			System.out.println("존재하지않음.. 새로 생성함.");
		}

		String newFile = "test_" + System.currentTimeMillis() + ".txt";
		String content = "New File Add Test FileName ::: " + newFile;
		SVNCommitInfo commit_new = localServerManager.commit_new("test", newFile, content.getBytes(), content);

		System.out.printf("New File Revision : %d author : %s \n", commit_new.getNewRevision(), commit_new.getAuthor());
	}

	@Test
	public void addDirTest() throws Exception {

		if (!localServerManager.isExistsPath("test")) {
			System.out.println("존재하지않음.. 새로 생성함.");
			localServerManager.commit_new("test", "Dir Commit Test");
		} else {
			System.out.println("이미 존재하므로 디렉토리 생성하지않음.");
		}

	}

	/**
	 * SVN Commit Test.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @throws SVNException
	 * @throws IOException
	 */
	@Test
	public void commitTest() throws SVNException, IOException {

		File[] commitTestFiles = getCommitTestFiles();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(commitTestFiles[0]);
			//			thirdPartManager.commit_new("/sql", commitTestFiles[0].getName(), inputStream, "test commit.");

			//			commitTestFiles = new File[] { new File("C:\\logs\\test\\deprecated_pass-batch-core\\sql\\text.txt") };
			SVNCommitInfo commit_modify = localServerManager.commit_modify("sql", "text.txt", inputStream, "test commit.");

			System.out.println(commit_modify.getAuthor());
			System.out.println(commit_modify.getNewRevision());

		} finally {
			if (inputStream != null)
				inputStream.close();
		}

	}

	@Test
	public void commitReverseTest() throws SVNException, IOException {

		File[] commitTestFiles = getCommitTestFiles();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(commitTestFiles[0]);
			//			thirdPartManager.commit_new("/sql", commitTestFiles[0].getName(), inputStream, "test commit.");

			//			commitTestFiles = new File[] { new File("C:\\logs\\test\\deprecated_pass-batch-core\\sql\\text.txt") };
			SVNCommitInfo commit_modify = localServerManager.commit_modify_reverse("sql", "text.txt", 42);

			System.out.println(commit_modify.getAuthor());
			System.out.println(commit_modify.getNewRevision());

		} finally {
			if (inputStream != null)
				inputStream.close();
		}

	}

	/**
	 * Test File writtend Date
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param files
	 * @throws IOException
	 */
	private void modifyFileContent(File[] files) throws IOException {
		for (File f : files) {
			try (FileWriter fileWriter = new FileWriter(f, true)) {
				fileWriter.append(DateUtil.getCurrentDateString());
				fileWriter.append(System.lineSeparator());
				fileWriter.flush();
			}
		}
	}

	/**
	 * Create New Test Files.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param files
	 * @throws IOException
	 */
	private void createTestFiles(File[] files) throws IOException {
		for (File f : files) {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
		}
	}

	/**
	 * get Default Test Files.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @return
	 * @throws IOException
	 */
	private File[] getCommitTestFiles() throws IOException {

		File[] files = new File[] { new File("C:\\logs\\test\\deprecated_pass-batch-core\\sql\\text.txt"),
				new File("C:\\logs\\test\\deprecated_pass-batch-core\\sql\\text2.txt"),
				new File("C:\\logs\\test\\deprecated_pass-batch-core\\sql\\text3.txt") };

		createTestFiles(files);

		modifyFileContent(files);

		return files;
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 조회 테스트
	 *
	 ********************************/
	@Test
	public void catTest() {
		System.out.println(localServerManager.cat("/pom.xml"));
		// System.out.println(manager.cat("r679",
		// "https://dev.naver.com/svn/javafxvoeditor/trunk/ScmManager/pom.xml"));

	}

	private static String getJSONString(String[] paths) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");

		for (String str : paths) {

			// HashMap<String, String> hashMap = new HashMap<String, String>();
			// hashMap.put("author", svnInfo.lastChangedAuthor);
			// hashMap.put("date", String.valueOf(svnInfo.lastChangedDate));
			// hashMap.put("repository", "O-pera");
			// hashMap.put("revision", String.valueOf(svnInfo.revision));
			// hashMap.put("path", svnInfo.path);

			sb.append("{");
			sb.append("\"author\":").append("\"").append(str).append("\",");
			sb.append("\"date\":").append("\"").append(str).append("\",");
			sb.append("\"system\":").append("\"").append(str).append("\",");
			sb.append("\"revision\":").append("\"").append(str).append("\",");
			sb.append("\"path\":").append("\"").append(str).append("\"");
			sb.append("},");
		}
		sb.setLength(sb.length() - 1);

		sb.append("]");

		return sb.toString();
	}

	@Test
	public void catByRivisionTest() {
		String jsonString = getJSONString(new String[] { "1", "2" });
		System.out.println(jsonString);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 목록 테스트
	 *
	 ********************************/
	@Test
	public void listTest() {
		List<String> list = testServerManager.list("/sos/pass-batch-core");
		// list = manager.list("//additional/DockFX/src/");
		// list = manager.list("//additional/DockFX/src/main");
		System.out.println(list);
	}

	/**
	 * 메타정보를 포함하는 엔트리 조회
	 * @throws ParseException
	 * @throws SVNException
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 13.
	 */
	@Test
	public void listEntryTest() throws SVNException, ParseException {

		long revision = testServerManager.getRevision(DateUtil.toDate("2016-11-02", DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD));

		List<SVNDirEntry> listEntry = testServerManager.listEntry("sos/sos-client/", String.valueOf(revision), true, null);
		List<SVNDirEntry> listEntry2 = testServerManager.listEntry("sos/sos-client/", "-1", true, null);

		System.out.println(revision + "  ->  " + listEntry.size());
		System.out.println("-1  -> " + listEntry2.size());


		listEntry2.forEach(System.out::println);

		testServerManager.listEntry("", "-1", false, null).forEach(System.err::println);;
		//			listEntry.forEach(v -> {
		//
		//				System.out.println("#######################################hello");
		//				System.out.println(v);
		//			});
		//
		//		listEntry.forEach(v ->{
		//
		//			System.out.printf("%s message : %s \n ", v.toString() , v.getCommitMessage());
		//
		//		});

		//		SVNDirEntry svnDirEntry = listEntry.get(0);

	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 이력정보 테스트
	 *
	 * @throws ParseException
	 *
	 ********************************/
	@Test
	public void logTest() throws ParseException {

		/*Server log*/
		localServerManager.log("/sql");
		localServerManager.log("/sql/text.txt", DateUtil.toDate("20160606", "YYYYMMDD"), System.err::println);

		/*File System log*/
		System.out.println("################# FileSystem Log ###################");
		File path = new File("C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\fileexplorer");

		localServerManager.logFileSystem(path, new Date(), System.err::println);

	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * checkout 테스트
	 * @throws Exception 
	 ********************************/
	@Test
	public void checkout() throws Exception {

		String property = System.getProperty("user.home");
		File outDir = new File(property + "\\home\\20160504_svn_test\\DockFx");
		outDir.mkdirs();
		Assert.assertTrue(true);
		Long checoutResult = testServerManager.checkout("/branches/batch/", outDir);
		System.out.println(checoutResult);

		if (outDir.exists()) {
			boolean deleteDir = FileUtil.deleteDir(outDir);
			Assert.assertTrue(deleteDir);
			System.out.println("디펠토리 삭제 성공 ::: " + outDir);
		}

		Assert.assertNotEquals(0, -1);

		Assert.assertTrue(true);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 차이점 비교 테스트
	 * @throws Exception
	 ********************************/
	@Test
	public void diff() throws Exception {
		String diff = testServerManager.diff("/additional/batch-core/pom.xml", SVNRevision.parse("725"), "/additional/batch-core/pom.xml",
				SVNRevision.parse("784"));
		System.out.println(diff);
	}

	/**
	 *
	 * ################# 시나리오 테스트 ###################
	 *
	 * 1. svn 디렉토리 파일목록만 추출.
	 *
	 * 2. 추출된 파일목록에서 컨텐츠 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 13.
	 */
	@Test
	public void scenarioTest() {

		List<SVNDirEntry> list = testServerManager.listEntry("/sos/pass-batch-core");

		Optional<String> findFirst = list.stream().filter(e -> {
			SVNNodeKind kind = e.getKind();
			if (SVNNodeKind.FILE == kind)
				return true;
			return false;
		}).map(e -> {
			try {
				SVNURL url = e.getURL();
				SVNURL repositoryRoot = e.getRepositoryRoot();
				String relativeURL = SVNURLUtil.getRelativeURL(repositoryRoot, url, true);
				return relativeURL;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return "error";
		}).peek(e -> {
			System.out.println(testServerManager.cat(e));
		}).findFirst();

		// 첫번째 데이터를 찾은후...
		findFirst.ifPresent(url -> {

			try {
				File outDir = new File("c://sampleDir");
				outDir.mkdir();

				System.out.println("checout dir ::: " + outDir.getAbsolutePath());
				System.out.println("checout url ::: " + url);
				Long checkout = testServerManager.checkout(url, outDir);

				System.out.println("result ::: " + checkout);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});

	}

	@Test
	public void getRepositoryUUID() {

		try {
			localServerManager.ping();

			String repositoryUUID = localServerManager.getRepositoryUUID();
			System.out.println(repositoryUUID);

		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getSvnUrlByFileSystemTest() throws Exception {
		SVNURL svnUrlByFileSystem = localServerManager2
				.getSvnUrlByFileSystem(new File("C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\test\\trunk\\trunk\\test\\Test"));
		System.out.println(svnUrlByFileSystem);
	}

}
