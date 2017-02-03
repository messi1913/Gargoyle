/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2017. 1. 16.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * @author KYJ
 *
 */
public class SVNListTest {

	@Test
	public void recently(){

		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, "http://localhost:11121/svn/sample2/");
		properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
		properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

		JavaSVNManager manager = new JavaSVNManager(properties);
		List<SVNDirEntry> listEntry = manager.listEntry("/", "-1", System.err::println);
		listEntry.forEach(System.out::println);
	}
}
