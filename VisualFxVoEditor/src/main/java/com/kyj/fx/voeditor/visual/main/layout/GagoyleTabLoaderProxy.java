/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2016. 2. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.scene.Parent;

/**
 * @author KYJ
 *
 */
public class GagoyleTabLoaderProxy implements GagoyleTabLoadable {

	private GagoyleTabLoadable proxy;

	/**
	 * 
	 */
	public GagoyleTabLoaderProxy() {
		proxy = new GagoyleTabLoadable() {

			@Override
			public void loadNewSystemTab(String tabName, Parent parent) {
				SharedMemory.getSystemLayoutViewController().loadNewSystemTab(tabName, parent);
			}

			@Override
			public void loadNewSystemTab(String tabName, String fxmlName) {
				SharedMemory.getSystemLayoutViewController().loadNewSystemTab(tabName, fxmlName);
			}

		};
	}

	@Override
	public void loadNewSystemTab(String tableName, Parent parent) {
		proxy.loadNewSystemTab(tableName, parent);

	}

	@Override
	public void loadNewSystemTab(String tabName, String fxmlName) {
		proxy.loadNewSystemTab(tabName, fxmlName);
	}

}
