/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

/**
 * @author KYJ
 *
 */
public class GargoyleBuilderFactory implements BuilderFactory {

	private GargoyleBuilderFactory() {
	}

	private static GargoyleBuilderFactory factory;

	public static BuilderFactory getInstance() {

		if (factory == null) {
			factory = new GargoyleBuilderFactory();
		}
		return factory;
	}

	private BuilderFactory deligator;

	public void setBuilderFactory(BuilderFactory factory) {
		this.deligator = factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.BuilderFactory#getBuilder(java.lang.Class)
	 */
	@Override
	public Builder<?> getBuilder(Class<?> type) {
		Builder<?> builder = null;

		if (deligator != null) {
			Builder<?> userBuilder = deligator.getBuilder(type);
			if (userBuilder != null) {
				return userBuilder;
			}
		}

		if (type == Button.class) {
			builder = new GargoyleButtonBuilder();
		} else if (type == TableView.class) {
			builder = new GargoyleTableViewBuilder();
		}

		return builder;
	}

}
