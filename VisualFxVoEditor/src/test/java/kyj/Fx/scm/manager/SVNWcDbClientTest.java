/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.scm.manager.svn.java.SVNWcDbClient;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class SVNWcDbClientTest {

	/********************************
	 * 작성일 : 2016. 7. 18. 작성자 : KYJ
	 *
	 * wc.db파일로부터 SVN URL정보를 가져옴.
	 * 
	 * @throws Exception
	 ********************************/
	@Test
	public void simple() throws Exception {

		SVNWcDbClient client = new SVNWcDbClient(new File("C:\\Users\\KYJ\\eclipse-jee-neon\\workspace\\AnimationRecorder\\.svn\\wc.db"));
		System.out.println(client.getUrl());
		Assert.assertNotNull(client.getUrl());
	}
}
