/********************************
 *	프로젝트 : fxloader
 *	패키지   : com.kyj.fx.fxloader
 *	작성일   : 2017. 11. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public abstract class FxSkinManager {

	private static Logger LOGGER = LoggerFactory.getLogger(FxSkinManager.class);

	/**
	 * 스킨데이터가 들어가 있는 기본 경로
	 */
	public static final String SKIN_BASE_DIR = "skins/";

	public static final String USER_SKIN_NAME = FxPreferencesUtil.KEY_USER_SKIN_NAME;

	public static final String DEFAULT_SKIN_PATH_NAME = SKIN_BASE_DIR + "modena.css";

	public static final String KEY_USER_BUTTON_STYLECLASS_FILE_NAME = FxPreferencesUtil.KEY_USER_BUTTON_STYLECLASS_FILE_NAME;

	/**
	 * 스킨 템플릿이 존재하는 위치.
	 *
	 * @최초생성일 2016. 12. 3.
	 */
	private static final String SKIN_TEPLATE_LOCATION = "template/css/skin/skin.css.template";

	/**
	 * 스킨 템플릿이 존재하는 위치.
	 *
	 * @최초생성일 2016. 12. 3.
	 */
	private static final String ROOT_SKIN_TEPLATE_LOCATION = "template/css/skin/rootSkin.css.template";

	/**
	 * @최초생성일 2016. 12. 5.
	 */
	private static final String SKIN_BUTTON_TEPLATE_LOCATION = "template/css/button";

	/**
	 * 버튼 스타일 클래스명
	 * 
	 * @최초생성일 2016. 12. 5.
	 */
	public static final String BUTTON_STYLE_CLASS_NAME = "button-gargoyle";

	/**
	 * 풀스킨명을 통한 스킨 등록
	 *
	 * @param skinFullPath
	 * @return
	 */
	public boolean registSkin(String skinFullPath) {

		if (skinFullPath == null || skinFullPath.trim().isEmpty()) {
			return false;
		}
		Preferences userRoot = getPreference();

		userRoot.put(USER_SKIN_NAME, skinFullPath);

		return true;
	}

	Preferences getPreference() {
		return FxPreferencesUtil.getDefault();
	}

	/**
	 * 단순스킨명만 존재하는 스킨정보등록
	 *
	 * @param simpleSkinPath
	 * @return
	 */
	public boolean registSkinFullPathn(String skinFullPath) {
		if (skinFullPath == null || skinFullPath.trim().isEmpty()) {
			return false;
		}
		Preferences userRoot = getPreference();
		userRoot.put(USER_SKIN_NAME, skinFullPath);
		return true;
	}

	/**
	 * 스킨이 존재하는지 확인한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @param simpleSkinPath
	 * @return
	 */
	public boolean existSkin(String simpleSkinPath) {
		boolean exists = false;
		try {
			File file = new File(SKIN_BASE_DIR + simpleSkinPath);
			exists = file.exists();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return exists;
	}

	/**
	 * 스킨이 존재하는지 확인한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @param skinFullPathName
	 * @return
	 */
	public boolean existSkinFullPath(String skinFullPathName) {
		boolean exists = false;
		try {
			String property = System.getProperty("user.dir");
			File file = new File(property, skinFullPathName);
			exists = file.exists();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return exists;
	}

	/**
	 * 스킨명만 있는 텍스트를 풀패스명으로 변환
	 *
	 * @param simpleSkinPath
	 * @return
	 */
	public String toFullPath(String simpleSkinPath) {
		return SKIN_BASE_DIR.concat(simpleSkinPath);
	}

	/**
	 * 스킨명만 있는 텍스트를 풀패스명으로 변환
	 *
	 * @param simpleSkinPath
	 * @return
	 */
	public URL toURL(String skinFullPath) {
		if (existSkinFullPath(skinFullPath)) {
			File file = new File(skinFullPath);
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}
		return null;
	}

	/**
	 * return Button Skin URL
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 7.
	 * @param btnStyleClass
	 * @return
	 */
	public URL toButtonURL(String btnStyleClass) {
		File file = new File(SKIN_BUTTON_TEPLATE_LOCATION, btnStyleClass + ".css");
		if (file.exists()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}
		return null;
	}

	/**
	 * 디폴트 스킨위치를 알려주는 URL을 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public URL getDefaultSkin() {
		return toURL(DEFAULT_SKIN_PATH_NAME);
	}

	/**
	 * 현재 적용된 스킨을 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public String getSkin() {
		Preferences userRoot = getPreference();
		try {
			String skinPath = userRoot.get(USER_SKIN_NAME, DEFAULT_SKIN_PATH_NAME);
			if (skinPath.trim().isEmpty())
				skinPath = DEFAULT_SKIN_PATH_NAME;

			if (!existSkinFullPath(skinPath)) {

				/*
				 * 2017.05.11 값을 제거하는 처리는 하지않는다. 값이 사라지면서 기존에 적용한 스킨이 지워지는 현상이 발생하기 때문이다.
				 */
				// userRoot.put(USER_SKIN_NAME, "");

				return "";
			}
			return toURL(skinPath).toExternalForm();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		return "";
	}

	/**
	 * return caspian.css
	 *
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 30.
	 */
	public String getJavafxDefaultSkin() {
		return toURL(toFullPath("caspian.css")).toExternalForm();
	}

	/**
	 * 사용자 정의 스킨 파일을 생성
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 * @param style
	 * @param isRegist
	 *            사용스킨으로 등록할지 유무
	 * @return 생성된 스킨 파일.
	 * @throws IOException
	 */
	public File createUserCustomSkin(String style, boolean isRegist) throws IOException {

		File templGagoyleCss = getTemplGagoyleCss();
		File file = new File(templGagoyleCss, "UserCustom.css");
		if (!file.exists()) {
			file.createNewFile();
		}
		writeFile(file, style, Charset.forName("UTF-8"));

		if (isRegist) {
			registSkin(file.getAbsolutePath());
		}

		return file;
	}

	/**
	 * str 내용을 file로 write처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param file
	 * @param str
	 * @param charset
	 * @throws IOException
	 */
	static void writeFile(File file, String str, Charset charset) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file, false)) {
			try (OutputStreamWriter writer = new OutputStreamWriter(out, charset)) {
				writer.write(str);
				writer.flush();
			}
		}

	}

	/**
	 * Gagoyle에서 사용중인 css Temp 파일 위치리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @return
	 */
	public static File getTemplGagoyleCss() {
		File file = new File(getTempGagoyle(), "css");
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * Gagoyle에서 사용중인 Temp 파일 디렉토리 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 */
	public static File getTempGagoyle() {
		File file = new File(getTempFileSystem(), "Gagoyle");
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * 시스템 Temp 파일 위치 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @return
	 */
	public static File getTempFileSystem() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * 스킨을 적용해본다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 * @param createUserCustomSkin
	 * @throws MalformedURLException
	 */
	public void applySkin(File createUserCustomSkin) {
		applySkin(createUserCustomSkin, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 * @param createUserCustomSkin
	 * @param btnStyleClass
	 * @throws MalformedURLException
	 */
	public void applySkin(File createUserCustomSkin, String btnStyleClass) {
		ObservableList<Stage> stages = StageHelper.getStages();
		if (stages.isEmpty())
			return;
		Stage stage = stages.get(0);
		ObservableList<String> stylesheets = stage.getScene().getStylesheets();
		stylesheets.clear();

		try {
			stylesheets.add(createUserCustomSkin.toURI().toURL().toExternalForm());
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		if (ValueUtil.isNotEmpty(btnStyleClass)) {
			applyBtnSyleClass(btnStyleClass);
		}
	}

	/**
	 * 버튼 스타일 클래스를 적용.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 * @param btnStyleClass
	 */
	public void applyBtnSyleClass(String btnStyleClass) {
		ObservableList<Stage> stages = StageHelper.getStages();
		if (stages.isEmpty())
			return;
		Stage stage = stages.get(0);
		ObservableList<String> stylesheets = stage.getScene().getStylesheets();

		URL buttonURL = toButtonURL(btnStyleClass);
		if (buttonURL != null) {
			stylesheets.add(buttonURL.toExternalForm());
		}

	}

	/**
	 * 스킨을 초기화 시킨다. 디폴트 스킨이 적용된다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 */
	public void resetSkin() {
		ObservableList<Stage> stages = StageHelper.getStages();
		if (stages.isEmpty())
			return;
		Stage primaryStage = stages.get(0);

		Scene scene = primaryStage.getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add(getSkin());
		scene.getStylesheets().add(getButtonSkin());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 * @return
	 */
	public String getButtonSkin() {
		try {
			return getButtonStyle().toURI().toURL().toExternalForm();
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return "";

	}

	public void resetSkin(Scene scene) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(getSkin());
	}

	/**
	 * 스킨템플릿을 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public String getSkinTemplate() {
		return readFile(new File(SKIN_TEPLATE_LOCATION));
	}

	/**
	 * 폰트나 기본 색상들을 지정하기위한 루트 스킨 템플릿
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 15.
	 * @return
	 */
	public String getRootSkinTemplate() {
		return readFile(new File(ROOT_SKIN_TEPLATE_LOCATION));

	}

	/**
	 * 파일 읽기 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 26.
	 * @param file
	 * @param useCache
	 *            메모리에 읽어온 파일의 컨텐츠를 임시저장함.
	 * @param options
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file) {
		String content = "";

		if (file.exists()) {
			try {
				return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			} catch (IOException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}

		return content;

	}

	/**
	 * 버튼 스타일클래스를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public File getButtonStyle() {
		String string = getPreference().get(KEY_USER_BUTTON_STYLECLASS_FILE_NAME, "");
		return new File(SKIN_BUTTON_TEPLATE_LOCATION, string);
	}

	/**
	 * 사용자가 선택한 버튼 스타일 클래스를 등록.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @param styleClassName
	 */
	public void registButtonSyleClass(String styleClassFileName) {
		Preferences userRoot = getPreference();
		userRoot.put(KEY_USER_BUTTON_STYLECLASS_FILE_NAME, (styleClassFileName + ".css"));
	}

	/**
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 */
	public List<File> getButtonSkinFiles() {
		File file = new File(SKIN_BUTTON_TEPLATE_LOCATION);
		if (file.exists()) {
			File[] listFiles = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".css");
				}
			});

			return Stream.of(listFiles).collect(Collectors.toList());
		}
		return Collections.emptyList();

	}

	/**
	 * @return
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 5.
	 */
	public boolean isUsedCustomButton() {
		return getButtonStyle().exists();
	}

	/**
	 * 스킨정보 리턴
	 *
	 * @return
	 */
	public List<String> getSkinList() {
		List<String> value = new ArrayList<String>();
		try {
			File file = new File(SKIN_BASE_DIR);
			String[] list = file.list();
			LOGGER.debug("##########Skin Info###############");
			if (list != null) {
				value = Stream.of(list).map(str -> {
					String skinFullPath = SKIN_BASE_DIR.concat(str);
					LOGGER.debug(skinFullPath);
					return skinFullPath;
				}).collect(Collectors.toList());
			}
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return value;

	}
}
