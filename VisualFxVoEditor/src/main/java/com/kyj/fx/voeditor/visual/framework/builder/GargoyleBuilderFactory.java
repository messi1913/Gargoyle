/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.builder;

import com.kyj.fx.voeditor.visual.framework.builder.GargoyleButtonBuilder;

import javafx.scene.control.Button;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.util.BuilderFactory#getBuilder(java.lang.Class)
	 */
	@Override
	public Builder<?> getBuilder(Class<?> type) {
		Builder<?> builder = null;
		
		if (type == Button.class) {
			builder = new GargoyleButtonBuilder();
		}
		
		return builder;
	}

}
