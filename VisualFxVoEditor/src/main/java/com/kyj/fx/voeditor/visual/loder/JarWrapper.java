/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.io.File;
import java.util.Properties;
import java.util.jar.JarFile;

import com.kyj.fx.voeditor.visual.framework.GagoyleParentBeforeLoad;
import com.kyj.fx.voeditor.visual.framework.GagoyleParentOnLoaded;

/**
 * Jar파일로더를 구현하기위한 변수정보를 담는 클래스
 *
 * @author KYJ
 *
 */
public class JarWrapper {

	// 위치정보
	File location;
	// Jar파일정보
	JarFile jarFile;
	// 변수정보
	Properties prop;
	// 변수에 담긴 위치정보
	String clazz;
	// UI에 보여줄 메뉴 이름
	String displayMenuName;

	/**
	 * 메뉴 경로
	 */
	String menuPath;

	// 로드된 javafx 노드정보
	Class<?> nodeClass;
	String configNodeClass;
	String configNodeName;

	// 메인탭에 Parent가 로드될때 처리할 리스너를 등록할 클래스를 정의.
	String addOnParentLoadedListener;
	Class<GagoyleParentOnLoaded> addOnParentLoadedListenerClass;

	String pluginDesc;

	// 메인탭에 Parent가 로드될때 처리할 리스너를 등록할 클래스를 정의.
	String setOnParentBeforeLoadedListener;
	Class<GagoyleParentBeforeLoad> setOnParentBeforeLoadedListenerClass;

	String openType = "INNER";
	
	/**
	 * 클래스 패스 추가
	 * @최초생성일 2017. 5. 17.
	 */
	String classpath;

	/**
	 * @return the setOnParentBeforeLoadedListenerClass
	 */
	public final Class<GagoyleParentBeforeLoad> getSetOnParentBeforeLoadedListenerClass() {
		return setOnParentBeforeLoadedListenerClass;
	}

	/**
	 * @return the addOnParentLoadedListenerClass
	 */
	public final Class<GagoyleParentOnLoaded> getAddOnParentLoadedListenerClass() {
		return addOnParentLoadedListenerClass;
	}

	/**
	 * @return the pluginDesc
	 */
	public final String getPluginDesc() {
		return pluginDesc;
	}

	/**
	 * @param pluginDesc
	 *            the pluginDesc to set
	 */
	public final void setPluginDesc(String pluginDesc) {
		this.pluginDesc = pluginDesc;
	}

	/**
	 * @return the location
	 */
	public File getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(File location) {
		this.location = location;
	}

	/**
	 * @return the jarFile
	 */
	public JarFile getJarFile() {
		return jarFile;
	}

	/**
	 * @param jarFile
	 *            the jarFile to set
	 */
	public void setJarFile(JarFile jarFile) {
		this.jarFile = jarFile;
	}

	/**
	 * @return the prop
	 */
	public Properties getProp() {
		return prop;
	}

	/**
	 * @param prop
	 *            the prop to set
	 */
	public void setProp(Properties prop) {
		this.prop = prop;
	}

	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz
	 *            the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the nodeClass
	 */
	public Class<?> getNodeClass() {
		return nodeClass;
	}

	/**
	 * @param nodeClass
	 *            the nodeClass to set
	 */
	public void setNodeClass(Class<?> nodeClass) {
		this.nodeClass = nodeClass;
	}

	/**
	 * @return the displayMenuName
	 */
	public String getDisplayMenuName() {
		return displayMenuName;
	}

	/**
	 * @param displayMenuName
	 *            the displayMenuName to set
	 */
	public void setDisplayMenuName(String displayMenuName) {
		this.displayMenuName = displayMenuName;
	}

	/**
	 * @return the menuPath
	 */
	public String getMenuPath() {
		return menuPath;
	}

	/**
	 * @param menuPath
	 *            the menuPath to set
	 */
	public void setMenuPath(String menuPath) {
		this.menuPath = menuPath;
	}

	/**
	 * @return the configNodeClass
	 */
	public String getConfigNodeClass() {
		return configNodeClass;
	}

	/**
	 * @param configNodeClass
	 *            the configNodeClass to set
	 */
	public void setConfigNodeClass(String configNodeClass) {
		this.configNodeClass = configNodeClass;
	}

	/**
	 * @return the configNodeName
	 */
	public String getConfigNodeName() {
		return configNodeName;
	}

	/**
	 * @param configNodeName
	 *            the configNodeName to set
	 */
	public void setConfigNodeName(String configNodeName) {
		this.configNodeName = configNodeName;
	}


	/**
	 * @return the openType
	 */
	public final String getOpenType() {
		return openType;
	}

	/**
	 * @param openType the openType to set
	 */
	public final void setOpenType(String openType) {
		this.openType = openType;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ZipWrapper [nodeClass=" + nodeClass + "]";
	}

}
