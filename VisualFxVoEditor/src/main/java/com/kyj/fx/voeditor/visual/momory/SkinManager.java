/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2016. 2. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.PreferencesUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 스킨정보를 관리하는 매니저
 *
 * @author KYJ
 *
 */
public class SkinManager {

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

	public URL getDefaultSkin() {
		return toURL(DEFAULT_SKIN_PATH_NAME);
	}

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
}
