/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2016. 7. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/***************************
 * 
 * @author KYJ
 *
 ***************************/

public class ErdScreenAdapter extends BorderPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(ErdScreenAdapter.class);
	private BorderPane root;

	private Node targetNode;
	private Consumer<Exception> errorHandler;

	public ErdScreenAdapter() {
		this(null);
	}

	public ErdScreenAdapter(Node targetNode) {
		this(targetNode, err -> LOGGER.error(ValueUtil.toString(err)));
	}

	public ErdScreenAdapter(Node targetNode, Consumer<Exception> errorHandler) {
		this.targetNode = targetNode;
		this.errorHandler = errorHandler;
	}

	public ErdScreenAdapter load() {

		try {

			Parent p = FxUtil.loadAndControllerAction(ErdScreenController.class, c -> {

				if (this.targetNode != null) {
					File snapShotDir = FileUtil.getTempSnapShotDir();

					File file = new File(snapShotDir, "tmpImage.png");
					boolean isWriteSuccess = false;
					try (FileOutputStream out = new FileOutputStream(file)) {

						FxUtil.snapShot(this.targetNode, out, System.err::println);

						isWriteSuccess = true;
					} catch (Exception e) {
						errorHandler.accept(e);
					}

					if (isWriteSuccess) {
						try {
							c.createPicutre("tmpImage.png");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}

			});

			this.setCenter(p);
		} catch (Exception e) {
			if (errorHandler != null)
				errorHandler.accept(e);
			else
				LOGGER.error(ValueUtil.toString(e));
		}

		return this;
	}

	public void show() {

		FxUtil.createStageAndShow(this, stage -> {
			stage.setTitle("ERD Editor[Experiment]");
		});

	}

}
