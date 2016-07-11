/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.util.SVNURLUtil;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * pure java svnkit test case
 *
 * @author KYJ
 *
 */
public class CommandTest3 {

	JavaSVNManager manager;

	@Before
	public void setting() throws Exception {
		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, "svn://10.40.41.49/");

		properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
		properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

		new ProxyInitializable().initialize();
		// properties.put(SVNManager.URL,
		// "https://dev.naver.com/svn/javafxvoeditor");
		manager = new JavaSVNManager(properties);
	}

	@Test
	public void importTest() throws FileNotFoundException, SVNException {
		manager.doImport("/sos/deprecated_pass-batch-core", SVNURL.fromFile(new File("c:\\logs\\tmp")));
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 조회 테스트
	 *
	 ********************************/
	@Test
	public void catTest() {
		System.out.println(manager.cat("/additional/DockFX/.project"));
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
		List<String> list = manager.list("/sos/pass-batch-core");
		// list = manager.list("//additional/DockFX/src/");
		// list = manager.list("//additional/DockFX/src/main");
		System.out.println(list);
	}

	/**
	 * 메타정보를 포함하는 엔트리 조회
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 13.
	 */
	@Test
	public void listEntryTest() {
		List<SVNDirEntry> list = manager.listEntry("/sos/pass-batch-core");
		list.forEach(System.out::println);
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 이력정보 테스트
	 *
	 ********************************/
	@Test
	public void logTest() {
		// https://dev.naver.com/svn/javafxvoeditor/additional/batch-core/pom.xml
		manager.log("/additional/DockFX/pom.xml");
		manager.log("/additional/batch-core/pom.xml");
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * checkout 테스트
	 *
	 * @throws FileNotFoundException
	 ********************************/
	@Test
	public void checkout() throws FileNotFoundException {

		String property = System.getProperty("user.home");
		File outDir = new File(property + "\\home\\20160504_svn_test\\DockFx");
		outDir.mkdirs();
		Assert.assertTrue(true);
		Long checoutResult = manager.checkout("/branches/batch/", outDir);
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
	 *
	 * @throws FileNotFoundException
	 * @throws SVNException
	 ********************************/
	@Test
	public void diff() throws FileNotFoundException, SVNException {
		String diff = manager.diff("/additional/batch-core/pom.xml", SVNRevision.parse("725"), "/additional/batch-core/pom.xml",
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

		List<SVNDirEntry> list = manager.listEntry("/sos/pass-batch-core");

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
			System.out.println(manager.cat(e));
		}).findFirst();

		// 첫번째 데이터를 찾은후...
		findFirst.ifPresent(url -> {

			try {
				File outDir = new File("c://sampleDir");
				outDir.mkdir();

				System.out.println("checout dir ::: " + outDir.getAbsolutePath());
				System.out.println("checout url ::: " + url);
				Long checkout = manager.checkout(url, outDir);

				System.out.println("result ::: " + checkout);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});

	}

}
