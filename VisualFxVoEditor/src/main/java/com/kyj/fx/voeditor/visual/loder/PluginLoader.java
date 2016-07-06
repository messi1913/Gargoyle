/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.util.List;

/**
 * 플러그인 jar 파일 로더
 *
 * @author KYJ
 *
 */
public class PluginLoader implements IPluginLoader {

	private static IPluginLoader loader;

	public static IPluginLoader getInstance() {
		if (loader == null)
			loader = new DefaultPluginLoader();
		return loader;
	}

	/**
	 * 생성금지. getInstance()함수 사용.
	 */
	private PluginLoader() {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.loder.IPluginLoader#load()
	 */
	@Override
	public List<JarWrapper> load() {
		return getInstance().load();
	}

	/**
	 * @return the loader
	 */
	public IPluginLoader getLoader() {
		return loader;
	}

	/**
	 * @param loader
	 *            the loader to set
	 */
	public void setLoader(IPluginLoader newLoader) {
		loader = newLoader;
	}

}
