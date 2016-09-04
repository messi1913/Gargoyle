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

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;

/***************************
 * 
 * 캡쳐후 미리보기 기능 지원.
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "CaptureScreenView.fxml")
public class CaptureScreenController {

	private File snapShotDir = FileUtil.getTempSnapShotDir();

	//	@FXML
	//	private ImageView ivPicture;
	@FXML
	private ScrollPane spPic;
	@FXML
	private Label lblStatus;

	Scale scale = new Scale(1, 1);
	DoubleProperty scaleDeltaX = new SimpleDoubleProperty(1);
	DoubleProperty scaleDeltaY = new SimpleDoubleProperty(1);
	private double initX;
	private double initY;
	private Point2D dragAnchor;

	private static final String STATUS_FORMAT = "scale : %f  x : %f  y : %f";

	@FXML
	public void initialize() {

		spPic.setOnScroll(ev -> {

			if (ev.isControlDown()) {
				//				ObservableList<Transform> transforms = ivPicture.getTransforms();

				if (ev.getDeltaY() > 0) {
					scaleDeltaX.set(scaleDeltaX.get() + 0.1);
					scaleDeltaY.set(scaleDeltaY.get() + 0.1);
					scale.setX(scaleDeltaX.get());
					scale.setY(scaleDeltaY.get());
					scale.setPivotX(ev.getX());
					scale.setPivotY(ev.getY());
				} else {
					double value = scaleDeltaX.get() - 0.1;
					double value2 = scaleDeltaY.get() - 0.1;
					if (value < 0)
						return;
					if (value2 < 0)
						return;
					scaleDeltaX.set(value);
					scaleDeltaY.set(value2);
					scale.setX(scaleDeltaX.get());
					scale.setY(scaleDeltaY.get());
					scale.setPivotX(ev.getX());
					scale.setPivotY(ev.getY());
				}

				lblStatus.setText(String.format(STATUS_FORMAT, scaleDeltaX.get(), ev.getX(), scaleDeltaY.get()));
			}
		});

		spPic.setOnMouseMoved(ev -> {
			lblStatus.setText(String.format(STATUS_FORMAT, scaleDeltaX.get(), ev.getX(), scaleDeltaY.get()));
		});

	}

	public void createPicutre(String image) throws FileNotFoundException {

		ImageView ivPicture = new ImageView();

		File file = new File(snapShotDir, "tmpImage.png");

		try {
			ivPicture.getTransforms().add(new Scale(1.3, 1.3));
			ivPicture.setImage(new Image(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			throw e;
		}

		ivPicture.getTransforms().add(scale);

		ivPicture.setOnMouseDragged(ev -> {
			double dragX = ev.getSceneX() - dragAnchor.getX();
			double dragY = ev.getSceneY() - dragAnchor.getY();
			//calculate new position of the circle

			double newXPosition = initX + dragX;
			double newYPosition = initY + dragY;

			//if new position do not exceeds borders of the rectangle, translate to this position
			ivPicture.setTranslateX(newXPosition);
			ivPicture.setTranslateY(newYPosition);

		});

		ivPicture.setOnMousePressed(ev -> {
			initX = ivPicture.getTranslateX();
			initY = ivPicture.getTranslateY();
			dragAnchor = new Point2D(ev.getSceneX(), ev.getSceneY());
		});

		spPic.setContent(ivPicture);
		//		spPic.getcon
	}

}
