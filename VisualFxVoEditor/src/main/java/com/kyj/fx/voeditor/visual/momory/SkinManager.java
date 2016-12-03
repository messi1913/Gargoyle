/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.PreferencesUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * 스킨정보를 관리하는 매니저
 *
 * @author KYJ
 *
 */
public final class SkinManager {

	private static Logger LOGGER = LoggerFactory.getLogger(SkinManager.class);
	/**
	 * 인스턴스
	 */
	private static SkinManager manager;

	/**
	 * 스킨데이터가 들어가 있는 기본 경로
	 */
	public static final String SKIN_BASE_DIR = "skins/";

	public static final String USER_SKIN_NAME = PreferencesUtil.KEY_USER_SKIN_NAME;

	public static final String DEFAULT_SKIN_PATH_NAME = SKIN_BASE_DIR + "modena.css";

	public static final String KEY_USER_BUTTON_STYLECLASS_NAME = PreferencesUtil.KEY_USER_BUTTON_STYLECLASS_NAME;

	/**
	 * 스킨 템플릿이 존재하는 위치.
	 * 
	 * @최초생성일 2016. 12. 3.
	 */
	private static final String SKIN_TEPLATE_LOCATION = "template/css/skin/skin.css.template";

	/**
	 * 생성방지를 위해 private로선언
	 */
	private SkinManager() {
	}

	/**
	 * 스킨 인스턴스 리턴
	 *
	 * @return
	 */
	public static SkinManager getInstance() {
		if (manager == null)
			manager = new SkinManager();
		return manager;
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
		return PreferencesUtil.getDefault();
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
			File file = new File(skinFullPathName);
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
				userRoot.put(USER_SKIN_NAME, "");
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

		File templGagoyleCss = FileUtil.getTemplGagoyleCss();
		File file = new File(templGagoyleCss, "UserCustom.css");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileUtil.writeFile(file, style, Charset.forName("UTF-8"));

		if (isRegist) {
			registSkin(file.getAbsolutePath());
		}

		return file;
	}

	/**
	 * 스킨을 적용해본다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 2.
	 * @param createUserCustomSkin
	 * @throws MalformedURLException
	 */
	public void applySkin(File createUserCustomSkin) throws MalformedURLException {
		ObservableList<String> stylesheets = SharedMemory.getPrimaryStage().getScene().getStylesheets();
		stylesheets.clear();
		stylesheets.add(createUserCustomSkin.toURI().toURL().toExternalForm());
	}

	/**
	 * 스킨을 초기화 시킨다. 디폴트 스킨이 적용된다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 */
	public void resetSkin() {
		Stage primaryStage = SharedMemory.getPrimaryStage();
		Scene scene = primaryStage.getScene();
		scene.getStylesheets().clear();
		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
	}
	
	public void resetSkin(Scene scene) {
		scene.getStylesheets().clear();
		scene.getStylesheets().add(SkinManager.getInstance().getSkin());
	}
	

	/**
	 * 스킨템플릿을 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public String getSkinTemplate() {
		return FileUtil.readFile(new File(SKIN_TEPLATE_LOCATION), new LoadFileOptionHandler() {

			@Override
			public Function<File, String> getFileNotFoundThan() {
				LOGGER.warn("template file not found.");
				return f -> "";
			}
		});
	}

	/**
	 * 버튼 스타일클래스를 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @return
	 */
	public String getButtonStyleClass() {
		return getPreference().get(KEY_USER_BUTTON_STYLECLASS_NAME, "");
	}

	/**
	 * 사용자가 선택한 버튼 스타일 클래스를 등록.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 3.
	 * @param styleClassName
	 */
	public void registButtonSyleClass(String styleClassName) {
		Preferences userRoot = getPreference();
		userRoot.put(KEY_USER_BUTTON_STYLECLASS_NAME, styleClassName);
	}
}
