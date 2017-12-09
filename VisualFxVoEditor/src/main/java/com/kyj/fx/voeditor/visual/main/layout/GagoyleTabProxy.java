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
public class GagoyleTabProxy implements GagoyleTabLoadable {

	private static GagoyleTabProxy proxy;

	public static GagoyleTabProxy getInstance() {
		if (proxy == null) {
			proxy = new GagoyleTabProxy();
		}
		return proxy;
	}

	private GagoyleTabLoadable loadableProxy;

	private GargoyleWorkspaceTreeProxy treeProxy;

	/**
	 * 
	 */
	private GagoyleTabProxy() {
		loadableProxy = new GagoyleTabLoadable() {

			@Override
			public void loadNewSystemTab(String tabName, Parent parent) {
				SharedMemory.getSystemLayoutViewController().loadNewSystemTab(tabName, parent);
			}

			@Override
			public void loadNewSystemTab(String tabName, String fxmlName) {
				SharedMemory.getSystemLayoutViewController().loadNewSystemTab(tabName, fxmlName);
			}

			@Override
			public void loadNewSystemTab(String tableName, CloseableParent<?> parent) {
				SharedMemory.getSystemLayoutViewController().loadNewSystemTab(tableName, parent);
			}

		};

		treeProxy = new GargoyleWorkspaceTreeProxy();
	}

	static class GargoyleWorkspaceTreeProxy {

		public void reflesh() {
			SharedMemory.getSystemLayoutViewController().refleshWorkspaceTree();
		}
	}

	@Override
	public void loadNewSystemTab(String tableName, Parent parent) {
		loadableProxy.loadNewSystemTab(tableName, parent);

	}

	@Override
	public void loadNewSystemTab(String tabName, String fxmlName) {
		loadableProxy.loadNewSystemTab(tabName, fxmlName);
	}

	public void refleshWorkspaceTree() {
		treeProxy.reflesh();
	}

	@Override
	public void loadNewSystemTab(String tableName, CloseableParent<?> parent) {
		loadableProxy.loadNewSystemTab(tableName, parent);
	}

}
