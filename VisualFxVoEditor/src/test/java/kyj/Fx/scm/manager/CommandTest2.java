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
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
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
public class CommandTest2 {

	JavaSVNManager manager;

	@Before
	public void setting() throws Exception {
		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, "https://dev.naver.com/svn/javafxvoeditor");
		properties.put(JavaSVNManager.SVN_USER_ID, "");
		properties.put(JavaSVNManager.SVN_USER_PASS, "");

		new ProxyInitializable().initialize();
		// properties.put(SVNManager.URL,
		// "https://dev.naver.com/svn/javafxvoeditor");
		manager = new JavaSVNManager(properties);
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

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 목록 테스트
	 *
	 ********************************/
	@Test
	public void listTest() {
		List<String> list = manager.list("/additional/DockFX/");
		list = manager.list("//additional/DockFX/src/");
		list = manager.list("//additional/DockFX/src/main");
		System.out.println(list);
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
	 * @throws Exception
	 ********************************/
	@Test
	public void diff() throws Exception {
		String diff = manager.diff("/additional/batch-core/pom.xml", SVNRevision.parse("725"), "/additional/batch-core/pom.xml",
				SVNRevision.parse("784"));
		System.out.println(diff);
	}

}
