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
import java.util.Map;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.util.FileUtil;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.transform.Scale;

/***************************
 *
 * 캡쳐후 미리보기 기능 지원.
 *
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "CaptureScreenView.fxml")
public class ErdScreenController {

	private File snapShotDir = FileUtil.getTempSnapShotDir();

	// @FXML
	// private ImageView ivPicture;
	@FXML
	private ScrollPane spPic;
	@FXML
	private AnchorPane anchorBoard;

	private IntegerProperty itemCount = new SimpleIntegerProperty();

	@FXML
	private Label lblStatus;
	@FXML
	private FlowPane flowItems;

	private Scale scale = new Scale(1, 1);
	private DoubleProperty scaleDeltaX = new SimpleDoubleProperty(1);
	private DoubleProperty scaleDeltaY = new SimpleDoubleProperty(1);

	// private double initX;
	// private double initY;

	// private Point2D dragAnchor;

	private static final String STATUS_FORMAT = "scale : %f  x : %f  y : %f items : %d";

	private ErdItemHandler itemHandler;

	@FXML
	public void initialize() {

		itemHandler = new ErdItemHandler(this);

		flowItems.getChildren().addAll(itemHandler.getItems());

		// 아이템 카운트 수를 핸들링하는 이벤트 리스너
		anchorBoard.getChildren().addListener((ListChangeListener) c -> {
			if (c.next()) {
				if (c.wasAdded()) {
					int addedSize = c.getAddedSize();
					itemCount.add(addedSize);

				}

				else if (c.wasRemoved()) {
					itemCount.subtract(c.getRemovedSize());
				}
			}
		});

		// spPic.setContent(anchorBoard);
		// this.anchorBoard.setPrefSize(spPic.getPrefWidth(), spPic.getPrefHeight());
		// spPic.prefWidthProperty().bind(this.anchorBoard.prefWidthProperty());
		// spPic.prefHeightProperty().bind(this.anchorBoard.prefHeightProperty());

		// spPic.prefHeightProperty().bind(this.anchorBoard.prefHeightProperty());

		anchorBoard.setOnScroll(ev -> {

			if (ev.isConsumed())
				return;

			if (ev.isControlDown()) {
				// ObservableList<Transform> transforms = ivPicture.getTransforms();

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

				lblStatus.setText(String.format(STATUS_FORMAT, scaleDeltaX.get(), ev.getX(), ev.getY(), itemCount.get()));

				ev.consume();
			}
		});

		anchorBoard.setOnMouseMoved(ev -> {
			lblStatus.setText(String.format(STATUS_FORMAT, scaleDeltaX.get(), ev.getX(), ev.getY(), itemCount.get()));
		});

	}

	/**
	 * @return the anchorBoard
	 */
	public final AnchorPane getAnchorBoard() {
		return anchorBoard;
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
		addItemEvent(ivPicture);

		// ivPicture.getTransforms().add(scale);
		//
		// ivPicture.setOnMouseDragged(ev -> {
		// double dragX = ev.getSceneX() - dragAnchor.getX();
		// double dragY = ev.getSceneY() - dragAnchor.getY();
		// //calculate new position of the circle
		//
		// double newXPosition = initX + dragX;
		// double newYPosition = initY + dragY;
		//
		// //if new position do not exceeds borders of the rectangle, translate to this position
		// ivPicture.setTranslateX(newXPosition);
		// ivPicture.setTranslateY(newYPosition);
		//
		// });
		//
		// ivPicture.setOnMousePressed(ev -> {
		// initX = ivPicture.getTranslateX();
		// initY = ivPicture.getTranslateY();
		// dragAnchor = new Point2D(ev.getSceneX(), ev.getSceneY());
		// });

		anchorBoard.getChildren().add(ivPicture);

		// spPic.getcon
	}

	private Map<Node, Location> initLocation = FXCollections.observableHashMap();

	protected void addItemEvent(Node newNode) {

		// newNode.getTransforms().add(new Scale(1.3, 1.3));
		initLocation.put(newNode, new Location());

		newNode.getTransforms().add(scale);

		newNode.setOnMouseDragged(ev -> {

			// if(ev.isConsumed())
			// return;

			Location location = initLocation.get(newNode);

			Point2D dragAnchor = location.getDragAnchor();
			double dragX = ev.getSceneX() - dragAnchor.getX();
			double dragY = ev.getSceneY() - dragAnchor.getY();
			// calculate new position of the circle

			double newXPosition = location.getInitX() + dragX;
			double newYPosition = location.getInitY() + dragY;

			// System.out.println(newXPosition + " : " + location.getInitY());
			// if new position do not exceeds borders of the rectangle, translate to this position

			newNode.setTranslateX(newXPosition);
			newNode.setTranslateY(newYPosition);

			// newNode.requestFocus();
			ev.consume();
		});

		newNode.setOnMousePressed(ev -> {

			if (ev.isConsumed())
				return;

//			if (newNode instanceof DrawItem) {
//				((DrawItem)newNode).setSelected(true);
//			}
			
			Location location = initLocation.get(newNode);
			location.setInitX(newNode.getTranslateX());
			location.setInitY(newNode.getTranslateY());
			location.setDragAnchor(new Point2D(ev.getSceneX(), ev.getSceneY()));
			newNode.requestFocus();	
			
			ev.consume();

		});

		newNode.setOnKeyPressed(ev -> {

			if (!(newNode instanceof DrawItem)) {
				return;
			}

			switch (ev.getCode()) {
			case DELETE:
				remove((DrawItem) newNode);
				break;

			case A:

				if (ev.isControlDown() && ev.isShiftDown()) {
					anchorBoard.getChildren().forEach(n -> {
						n.requestFocus();
					});
				}

				break;

			default:
				break;
			}

			// if (ev.getCode() == KeyCode.DELETE) {
			// if (newNode instanceof DrawItem)
			// remove((DrawItem) newNode);
			// }

		});

	}

	protected void addChildren(Node n) {
		anchorBoard.getChildren().add(n);
	}

	public void remove(DrawItem drawItemSkin) {
		anchorBoard.getChildren().remove(drawItemSkin);
	}
}
