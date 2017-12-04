/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.velocity
 *	작성일   : 2017. 12. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.velocity;

import java.util.Arrays;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import com.kyj.fx.voeditor.visual.framework.KeyNode;

import javafx.scene.Node;
import javafx.scene.web.WebView;

/**
 * @author KYJ
 *
 */

public class DefaultVelocityBinderComposite extends AbstractVelocityBinderComposite {

	@Override
	public void onTextLoad(Node content, String cont) {
		if (content instanceof CodeArea) {
			((CodeArea) content).replaceText(cont);
		} else if (content instanceof WebView) {
			((WebView) content).getEngine().loadContent(cont, "text/html");
		}

	}

	@Override
	public List<KeyNode> tabContents() {
		CodeArea value = new CodeArea();
		value.setParagraphGraphicFactory(LineNumberFactory.get(value));

		WebView webView = new WebView();
		return Arrays.asList(new KeyNode("Text", value), new KeyNode("WebView", webView));
	}
}
