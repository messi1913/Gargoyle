/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2015. 12. 18.
 *	작성자   : KYJ
 *******************************/
package classloader;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.loder.JarWrapper;
import com.kyj.fx.voeditor.visual.loder.PluginLoader;

/**
 * @author KYJF
 *
 */
public class PluginLoaderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoaderTest.class);

	/**
	 * 프로젝트 plugins 디렉토리안에 jar파일이 존재하는경우 로드된 jar에 대한 정보를 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 22.
	 */
	@Test
	public void loadTest() {
		List<JarWrapper> load = PluginLoader.getInstance().load();
		load.forEach(jar -> {
			String menuPath = jar.getMenuPath();
			String displayMenuName = jar.getDisplayMenuName();
			Properties prop = jar.getProp();

			LOGGER.debug(String.format("menu name \"%s\" \n menu path %s \n menu property %s", displayMenuName, menuPath, prop.toString()));

		});

		Assert.assertNotNull(load);
	}
}
