import java.io.File;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.model.proj.ProjectDescription;
import com.kyj.fx.voeditor.visual.util.SAXPasrerUtil;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2015. 12. 7.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class XmlProjectLoadTest {

	@Test
	public void test() throws Exception {
		String property = System.getProperty("user.dir");
		System.out.println(property);
		String location = property + File.separator + ".project";
		File file = new File(location);
		Assert.assertEquals(file.exists(), true);
		ProjectDescription loadXml = SAXPasrerUtil.loadXml(file, ProjectDescription.class);
		Assert.assertNotNull(loadXml);
		System.out.println(loadXml);
		// savePersonDataToFile(new File("sample"), asList);
	}

	@XmlRootElement
	class ProjectWrapper {

	}
}