/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.proxy
 *	작성일   : 2017. 2. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;

/**
 * @author KYJ
 *
 */
class ProxyServerLogManager {

	private List<Consumer<String>> onPushedItems;

	public ProxyServerLogManager() {
		onPushedItems = new ArrayList<>();
	}

	public ProxyServerLogManager(Consumer<String> onPushed) {
		this();
		this.onPushedItems.add(onPushed);
	}

	public void push(String message) {
		Platform.runLater(() -> {
			String _message = message;
			for (Consumer<String> c : onPushedItems) {
				c.accept(_message);
			}
		});

	}

	public boolean addPushedService(Consumer<String> onPushed) {
		return this.onPushedItems.add(onPushed);
	}

	public void clearPushedService() {
		this.onPushedItems.clear();
	}
}
