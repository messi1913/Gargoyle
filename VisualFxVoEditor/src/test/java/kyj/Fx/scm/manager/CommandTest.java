/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.kyj.scm.manager.svn.SVNCommandManager;

/**
 * 커맨드라인베이스 svn 테스트
 *
 * @author KYJ
 *
 */
public class CommandTest {

	SVNCommandManager manager;

	@Before
	public void setting() throws Exception {
		Properties properties = new Properties();
		properties.put(SVNCommandManager.SVN_USER_ID, "callakrsos");
		properties.put(SVNCommandManager.SVN_USER_PASS, "mFn+QPl+TW8=");

		// properties.put(SVNManager.URL,
		// "https://dev.naver.com/svn/javafxvoeditor");
		manager = new SVNCommandManager(properties);
	}

	@Test
	public void catTest() {
		System.out.println(manager.cat("https://dev.naver.com/svn/javafxvoeditor/trunk/ScmManager/pom.xml"));
		System.out.println(manager.cat("r679", "https://dev.naver.com/svn/javafxvoeditor/trunk/ScmManager/pom.xml"));
	}

	@Test
	public void listTest() {
		List<String> list = manager.list("https://dev.naver.com/svn/javafxvoeditor/trunk/");
		System.out.println(list);
	}

	@Test
	public void logTest() {
		List<String> list = manager.log("https://dev.naver.com/svn/javafxvoeditor/trunk/ScmManager/pom.xml");
		System.out.println(list);
	}

	@Test
	public void checkout() {

		List<String> list = manager.checkout("C:\\Users\\KYJ\\Downloads\\ScmManager",
				"https://dev.naver.com/svn/javafxvoeditor/additional/DockFX");
		System.out.println(list);
	}

}
