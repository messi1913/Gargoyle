/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 15.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.memory;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * VO 에디터 Type에서 타입이름에 대해 변환할 표준 클래스타입으로 변환하는 정보를 관리
 *
 * ReadOnlyType
 *
 * @author KYJ
 *
 */
public class DatabaseTypeMappingResourceLoader implements IFileBaseConfiguration {
	private static Properties properties;
	private static DatabaseTypeMappingResourceLoader loader;
	private static final String FILE_NAME = "META-INF/databse.type.mapping.properties";

	public static DatabaseTypeMappingResourceLoader getInstance() {
		if (loader == null) {
			loader = new DatabaseTypeMappingResourceLoader();
			loader.initialize();
		}
		return loader;
	}

	private DatabaseTypeMappingResourceLoader() {
		initialize();
	}

	private void initialize() {
		properties = new Properties();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			properties.load(classLoader.getResourceAsStream(FILE_NAME));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	@Override
	public String getFileName() {
		return FILE_NAME;
	}

	@Override
	public Set<Entry<Object, Object>> getEntry() {
		return properties.entrySet();
	}

}
