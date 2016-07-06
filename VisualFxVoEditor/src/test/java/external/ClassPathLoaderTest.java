/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 2. 25.
 *	작성자   : KYJ
 *******************************/
package external;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * @author KYJ
 *
 */
public class ClassPathLoaderTest {

	@Test
	public void loaderTest() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();

		Resource resource = context.getResource("classpath:com/kyj/voeditor/visual/component/text/java-keywords.css");

		System.out.println(resource.exists());
	}

	@Test
	public void mapperTest() {
		String[] args = new String[] { "fctCode=C820A", "poNo=010030072647", "injInpQty=400" };

		Map<String, Object> collect = Stream.of(args).collect(Collectors.toMap(new Function<String, String>() {

			@Override
			public String apply(String t) {
				return t.split("=")[0];
			}
		}, new Function<String, Object>() {

			@Override
			public Object apply(String t) {
				return t.split("=")[1];
			}
		}));

		System.out.println(collect);
	}
}
