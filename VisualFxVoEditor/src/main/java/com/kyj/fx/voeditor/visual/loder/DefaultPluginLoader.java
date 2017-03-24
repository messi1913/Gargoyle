/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.GagoyleParentBeforeLoad;
import com.kyj.fx.voeditor.visual.framework.GagoyleParentOnLoaded;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.Parent;

/**
 * 플러그인 로더 초기작
 *
 *
 * PluginLoader클래스를 이용하여 접근할것
 *
 * @author KYJ
 *
 */
class DefaultPluginLoader implements IPluginLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

	public DefaultPluginLoader() {

	}

	/**
	 * 디렉토리안의 플러그인파일들을 불러들인다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 18.
	 */
	protected File[] searchPlugins() {
		File file = new File(PLUGIN_DIR);
		LOGGER.debug(file.getAbsolutePath());
		if (!(file.exists() && file.isDirectory()))
			return null;
		return file.listFiles((FilenameFilter) (dir, name) -> name.endsWith(PLUGIN_EXTENSION));
	}

	/**
	 * 불러들인 플러그인에서 노드위치정보를 로드해온다.
	 *
	 * @return
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 18.
	 */
	@Override
	public List<JarWrapper> load() {
		File[] pluginFiles = searchPlugins();
		if (pluginFiles == null)
			return Collections.emptyList();

		return Stream.of(pluginFiles).map(file -> {
			try {
				@SuppressWarnings("resource")
				JarFile jarFile = new JarFile(file);
				ZipEntry entry = jarFile.getEntry(PLUGIN_INI);
				Properties properties = new Properties();
				properties.load(jarFile.getInputStream(entry));

				JarWrapper zipWrapper = new JarWrapper();
				zipWrapper.location = file;
				zipWrapper.jarFile = jarFile;
				zipWrapper.prop = properties;
				return zipWrapper;
			} catch (Exception e) {
				LOGGER.error(String.format("is not jar file \n%s", ValueUtil.toString(e)));
			}
			return null;
		}).filter(/* jar파일이라면 로드됨. */zipWrapper -> zipWrapper != null)
				.map(/* 클래스정보 노출 */zipWrapper -> {
					String clazz = zipWrapper.prop.getProperty(KEY_DISPLAY_JAVAFX_NODE_CLASS);
					String diplayMenuName = zipWrapper.prop.getProperty(KEY_DISPLAY_MENU_NAME);
					String menuPath = zipWrapper.prop.getProperty(KEY_DISPLAY_MENU_PATH);
					String configNodeClass = zipWrapper.prop.getProperty(KEY_DISPLAY_CONFIG_JAVAFX_NODE_CLASS);
					String configNodeName = zipWrapper.prop.getProperty(KEY_DISPLAY_CONFIG_NAME);
					String addOnParentLoadedListener = zipWrapper.prop.getProperty(ADD_ON_PARENT_LOADED_LISTENER);
					String setOnParentBeforeLoadedListener = zipWrapper.prop.getProperty(SET_ON_PARENT_BEFORE_LOADED_LISTENER);
					String openType = zipWrapper.prop.getProperty(OPEN_TYPE);

					if (clazz != null && !clazz.isEmpty()) {
						zipWrapper.clazz = clazz;
						zipWrapper.displayMenuName = diplayMenuName;
						zipWrapper.menuPath = menuPath;
						zipWrapper.configNodeClass = configNodeClass;
						zipWrapper.configNodeName = configNodeName;
						zipWrapper.addOnParentLoadedListener = addOnParentLoadedListener;
						zipWrapper.setOnParentBeforeLoadedListener = setOnParentBeforeLoadedListener;
						zipWrapper.openType = ValueUtil.isEmpty(openType) ? "INNER" : openType;
						return zipWrapper;
					}
					return null;
				}).filter(/* 유효한 정보만 다시 필터링하고 클래스 로딩 */ zipWrapper -> {

					try {
						Class<?> loadFromJarFile = DynamicClassLoader.loadFromJarFile(zipWrapper.location, zipWrapper.clazz);

						/* 2017-03-23
						 * JAVAFX Parent 노드 타입과 더블어 CloseableParent 타입도 허용한다.
						 * JAVAFX Parent 노드 타입이어야 유효하다.
						 */
						if (Parent.class.isAssignableFrom(loadFromJarFile) ||
								CloseableParent.class.isAssignableFrom(loadFromJarFile)
								) {
							zipWrapper.nodeClass = loadFromJarFile;
							LOGGER.debug(String.format("valide plugin class info : %s ", loadFromJarFile.getName()));
						} else {
							return false;
						}

						if (ValueUtil.isNotEmpty(zipWrapper.addOnParentLoadedListener)) {
							/* 리스너 클래스 등록 과정에서 성공여부 중요치않음. */
							try {
								Class<?> addOnParentLoadedListenerClass = DynamicClassLoader.loadFromJarFile(zipWrapper.location,
										zipWrapper.addOnParentLoadedListener);
								// JAVAFX Parent 노드 타입이어야 유효하다.
								if (GagoyleParentOnLoaded.class.isAssignableFrom(addOnParentLoadedListenerClass)) {
									zipWrapper.addOnParentLoadedListenerClass = (Class<GagoyleParentOnLoaded>) addOnParentLoadedListenerClass;
									LOGGER.debug(String.format("added plugin listener ::  class info : %s ",
											zipWrapper.addOnParentLoadedListener));
								}
							} catch (Exception e) {
								LOGGER.error(ValueUtil.toString(e));
							}
						}

						if (ValueUtil.isNotEmpty(zipWrapper.setOnParentBeforeLoadedListener)) {
							/* 리스너 클래스 등록 과정에서 성공여부 중요치않음. */
							try {
								Class<?> setOnParentBeforeLoadedListenerClass = DynamicClassLoader.loadFromJarFile(zipWrapper.location,
										zipWrapper.setOnParentBeforeLoadedListener);
								// JAVAFX Parent 노드 타입이어야 유효하다.
								if (GagoyleParentBeforeLoad.class.isAssignableFrom(setOnParentBeforeLoadedListenerClass)) {
									zipWrapper.setOnParentBeforeLoadedListenerClass = (Class<GagoyleParentBeforeLoad>) setOnParentBeforeLoadedListenerClass;
									LOGGER.debug(String.format("added plugin listener ::  class info : %s ",
											zipWrapper.setOnParentBeforeLoadedListener));
								}
							} catch (Exception e) {
								LOGGER.error(ValueUtil.toString(e));
							}
						}

					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
						return false;
					}

					return true;

				}).collect(Collectors.toList());
	}

}
