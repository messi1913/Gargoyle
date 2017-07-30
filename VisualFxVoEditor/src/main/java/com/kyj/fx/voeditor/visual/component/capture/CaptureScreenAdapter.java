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
import javafx.scene.layout.BorderPane;

/***************************
 * 
 * @author KYJ
 *
 ***************************/

public class CaptureScreenAdapter extends BorderPane{
	private static final Logger LOGGER = LoggerFactory.getLogger(CaptureScreenAdapter.class);
	private BorderPane root;
	private Node targetNode;

	public CaptureScreenAdapter() {
		this(null);
	}

	public CaptureScreenAdapter(Node targetNode) {
		this(targetNode, err -> LOGGER.error(ValueUtil.toString(err)));
	}

	public CaptureScreenAdapter(Node targetNode, Consumer<Exception> errorHandler) {
		this.targetNode = targetNode;
		try {
			root = FxUtil.loadAndControllerAction(CaptureScreenController.class, c -> {

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
			
			this.setCenter(this.root);
		} catch (Exception e) {
			errorHandler.accept(e);
		}

	}

	public void show() {

		FxUtil.createStageAndShow(root, stage -> {
			stage.setTitle("Captured Image Editor[Experiment]");
			//			double width = targetNode.getBoundsInParent().getWidth();
			//			double height = targetNode.getBoundsInParent().getHeight();
			//			stage.setMaxWidth(width);
			//			stage.setMaxHeight(height);
			//			stage.setWidth(width);
			//			stage.setHeight(height);
		});

	}

}
