/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2016. 7. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.function.Consumer;

import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class CaptureScreenComposite {

	private BorderPane root;
	private Node targetNode;

	public CaptureScreenComposite(Node targetNode, Consumer<Exception> errorHandler) {
		this.targetNode = targetNode;
		try {
			root = FxUtil.loadAndControllerAction(CaptureScreenController.class, c -> {

				File snapShotDir = FileUtil.getTempSnapShotDir();

				File file = new File(snapShotDir, "tmpImage.png");
				boolean isWriteSuccess = false;
				try (FileOutputStream out = new FileOutputStream(file)) {

					FxUtil.snapShot(targetNode, out, System.err::println);

					isWriteSuccess = true;
				} catch (Exception e) {
					errorHandler.accept(e);
				}

				if (isWriteSuccess) {
					try {
						c.getIvPicture().getTransforms().add(new Scale(1.5, 1.5));
						c.getIvPicture().setImage(new Image(new FileInputStream(file)));
					} catch (FileNotFoundException e) {
						errorHandler.accept(e);
					}
				}

			});
		} catch (Exception e) {
			errorHandler.accept(e);
		}

	}

	public void show() {

		FxUtil.createStageAndShow(root, stage -> {

			//			double width = targetNode.getBoundsInParent().getWidth();
			//			double height = targetNode.getBoundsInParent().getHeight();
			//			stage.setMaxWidth(width);
			//			stage.setMaxHeight(height);
			//			stage.setWidth(width);
			//			stage.setHeight(height);

			stage.show();
		});

	}

}
