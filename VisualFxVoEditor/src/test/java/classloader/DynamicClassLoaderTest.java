/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2015. 10. 23.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package classloader;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.loder.DynamicClassLoader;
import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.main.model.vo.ClassPath;
import com.kyj.fx.voeditor.visual.main.model.vo.ClassPathEntry;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 동적클래스로더 테스트 케이스
 * 
 * @author KYJ
 *
 */
public class DynamicClassLoaderTest {

	/**
	 * class경로까지 이동후 class파일을 찾는경우 에러가 발생하지않고 정상적인 로딩
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @throws Exception
	 */
	@Test
	public void loadTest() throws Exception {

		// Properties properties = System.getProperties();

		{
			Class<?> load = DynamicClassLoader.load("C:/Users/KYJ/JAVA_FX/webWorkspace/sos-client/target/classes/",
					"com.samsung.sds.sos.client.application.App");

			System.out.println(load);
		}
		{
			Class<?> load = DynamicClassLoader.load("C:/Users/KYJ/JAVA_FX/webWorkspace/meerkat-core/target/classes/",
					"com.samsung.sds.meerkat.core.ano.ColumnName");
		}

		// String classPath = properties.getProperty("java.class.path");
		// System.out.println(classPath);
	}

	/**
	 * 프로젝트 ~ classes 까지 경로가 아니면 에러가 발생함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @throws Exception
	 */
	@Test(expected = ClassNotFoundException.class)
	public void loadTest2() throws Exception {
		Class<?> load = DynamicClassLoader.load("C:/Users/KYJ/JAVA_FX/webWorkspace/sos-client/target/classes/",
				"com.samsung.sds.sos.client.application.App1");
	}

	/**
	 * classpath를 찾고 classpath의 컴파일 폴더를 기준으로 컴파일을 처리한뒤 정상적으로 특정클래스가 로딩되는지를
	 * 테스트한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @throws Exception
	 */
	@Test
	public void loadTest3() throws Exception {
		ClassPath parsingClassPath = DynamicClassLoader.parsingClassPath("C:/Users/KYJ/JAVA_FX/webWorkspace");
		List<ClassPathEntry> entryFilter = parsingClassPath.entryFilter(entry -> ValueUtil.isNotEmpty(entry.getOutput()));
		entryFilter.forEach(entry -> {
			try {
				String filePathName = parsingClassPath.getFilePathName();
				System.out.println(filePathName);
				String replaceFirst = filePathName.replaceFirst(".classPath", "");

				String output = entry.getOutput();
				System.out.println(output);

				System.out.println("output");
				String classesLocation = replaceFirst + output;
				System.out.println(classesLocation);

				List<ProjectInfo> listClases = DynamicClassLoader.listClases(classesLocation);
				if(listClases.isEmpty())
					return;
				ProjectInfo projectInfo = listClases.get(0);
				List<String> classes = projectInfo.getClasses();
				for (String clzz : classes) {
					DynamicClassLoader.load(classesLocation, clzz);
				}

				System.out.println("load ok.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Test
	public void loadTest4() throws Exception {

		try {
			String classDirName = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);

			// List<ProjectInfo> listClases =
			// DynamicClassLoader.listClases(classDirName);
			DynamicClassLoader.load(classDirName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 클래스패스파일을 찾는 API를 테스트.
	 * 
	 * 첫번째는 워크스페이스 기준으로 프로젝트파일을 순회하며 classpath파일을 찾는다.
	 * 
	 * 두번째는 프로젝트 기준으로 classpath 파일을 찾는다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 */
	@Test
	public void findClassPathsTest() {
		{
			String workspaceName = "C:\\Users\\KYJ\\JAVA_FX\\webWorkspace";

			List<File> findClassPaths = DynamicClassLoader.findClassPaths(new File(workspaceName));
			System.out.println("###워크페스페이스 탐색결과 ###");
			for (File f : findClassPaths) {
				System.out.println(f);
			}
		}
		{
			String projectPathName = "C:\\Users\\KYJ\\JAVA_FX\\webWorkspace\\meerkat-core";

			List<File> findClassPaths = DynamicClassLoader.findClassPaths(new File(projectPathName));
			System.out.println("###프로젝트  탐색결과 ###");
			for (File f : findClassPaths) {
				System.out.println(f);
			}
		}

	}

	/**
	 * 
	 * classpath파일을 찾은경우 classpath파일안의 컴파일 폴더를 찾는 테스트코드
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @throws Exception
	 */
	@Test
	public void classPathTest() throws Exception {
		String filePathName = "C:\\G-MES2.0\\workspace\\gmes2-model\\.classPath";
		ClassPath parsingClassPath = DynamicClassLoader.parsingClassPath(filePathName);

		List<ClassPathEntry> entryFilter = parsingClassPath.entryFilter(entry -> ValueUtil.isNotEmpty(entry.getOutput()));
		entryFilter.forEach(System.out::println);
	}

}
