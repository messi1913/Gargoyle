/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2015. 10. 23.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package classloader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class JarFileTest {

	/**
	 * DataTest.jar파일에 포함된 클래스파일을 리스틍하는 테스트케이스
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 23.
	 * @throws IOException
	 */
	@Test
	public void jarFileListing() throws IOException {
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);
		JarFile jarFile = new JarFile(userDir + "\\lib\\db\\oracle\\ojdbc14.jar");
		Enumeration<JarEntry> entries = jarFile.entries();
		int count = 0;
		while (entries.hasMoreElements()) {
			JarEntry nextElement = entries.nextElement();
			String name = nextElement.getName();
			boolean directory = nextElement.isDirectory();
			// 디렉토리폴더 제외
			if (!directory) {
				// 이너클래스인경우 제외
				if (name.indexOf('$') < 0) {
					// 클래스확장자 파일만...
					if (name.endsWith(".class")) {
						System.out.println(name);
						count++;
					}

				}

			}

		}
		System.out.println("총 개수 : " + count);
	}

	@Test
	public void classLoaderJarFileListing() {
		Properties properties = System.getProperties();
		String classPath = properties.getProperty("java.class.path");

		String[] split = classPath.split(";");
		for (String classPathEntry : split) {
			System.out.println(classPathEntry);
		}

	}
}
