package simple;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 10. 15.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */

public class ClassTypeResourceLoaderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassTypeResourceLoaderTest.class);
	/**
	 * 매핑처리한대로 처리되는지 테스트
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void getTest() throws FileNotFoundException, IOException, ClassNotFoundException {
		FieldMeta str1 = ClassTypeResourceLoader.getInstance().get("String");
		LOGGER.debug(str1.toString());

		FieldMeta str2 = ClassTypeResourceLoader.getInstance().get("Date");
		LOGGER.debug(str2.toString());

		FieldMeta str3 = ClassTypeResourceLoader.getInstance().get("BigDecimal");
		LOGGER.debug(str3.toString());

		FieldMeta str5 = ClassTypeResourceLoader.getInstance().get("StringProperty");
		LOGGER.debug(str5.toString());

		FieldMeta str6 = ClassTypeResourceLoader.getInstance().get("IntegerProperty");
		LOGGER.debug(str6.toString());

		FieldMeta str7 = ClassTypeResourceLoader.getInstance().get("Boolean");
		LOGGER.debug(str7.toString());
	}

	/**
	 * 설정파일의 키리스트를 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 */
	@Test
	public void getKeyListTest() {
		List<String> keyList = ClassTypeResourceLoader.getInstance().getKeyList();
		LOGGER.debug(keyList.toString());
	}
}
