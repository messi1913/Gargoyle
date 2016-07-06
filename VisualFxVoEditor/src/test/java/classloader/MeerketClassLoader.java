/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2015. 10. 23.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package classloader;

import java.util.Map;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.loder.ClassLoaderInfo;

/**
 * @author KYJ
 *
 */
public class MeerketClassLoader {

	@Test
	public void loaderTest() {
		Map loadingClassInfo = ClassLoaderInfo.getInstance().getLoadingClassInfo("java.lang.String");
		System.out.println(loadingClassInfo);

	}
}
