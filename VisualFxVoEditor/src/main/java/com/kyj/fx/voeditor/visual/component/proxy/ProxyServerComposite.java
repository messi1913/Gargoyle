/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.proxy
 *	작성일   : 2017. 2. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.proxy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.main.Main;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public class ProxyServerComposite extends CloseableParent<BorderPane> implements PrimaryStageCloseable
{

	private static Logger LOGGER = LoggerFactory.getLogger(ProxyServerComposite.class);
	private ProxyServerController controller;

	/**
	 * @param parent
	 */
	public ProxyServerComposite() {
		super(new BorderPane());
		try {
			Main.addPrimaryStageCloseListener(this);
			Node load = FxUtil.loadAndControllerAction(ProxyServerController.class, c -> {
				controller = c;
				c.setComposite(this);
			});
			getParent().setCenter(load);

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.main.layout.CloseableParent#close()
	 */
	@Override
	public void close() throws IOException {
		if (controller != null)
			controller.close();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable#closeRequest()
	 */
	@Override
	public void closeRequest() {
		try {
			close();
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}
}
