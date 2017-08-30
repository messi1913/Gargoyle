/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2017. 7. 28.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author KYJ
 *
 */

public abstract class DrawItemSkin<T extends DrawItem> extends SkinBase<DrawItem>
		implements ColorChange, MouseAction, KeyAction, ContainerAction {

	private VBox container;
	private T control;

	public DrawItemSkin(T control) {
		super(control);

		this.control = control;
		container = new VBox();
		container.setPickOnBounds(true);

		draw(container);

		getSkinnable().focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					container.requestFocus();
				}
			}
		});

		container.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println(newValue);

				if (newValue) {
					BorderStroke bs = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN);
					Border border = new Border(bs);// Border.EMPTY;
					container.setPadding(new Insets(5));
					container.setBorder(border);
				} else {
					container.setBorder(Border.EMPTY);
				}
			}
		});

		container.addEventHandler(MouseEvent.ANY, this::containerMouseAction);
		container.addEventHandler(KeyEvent.ANY, this::containerKeyAction);

		control.setContextMenu(createContextMenu());
		getChildren().add(container);

	}

	private ContextMenu contextMenu;

	/**
	 * Context Menu 생성
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 8. 31.
	 * @return
	 */
	protected ContextMenu createContextMenu() {
		contextMenu = new ContextMenu();
		{
			MenuItem e = new MenuItem("To - Front");
			e.setOnAction(ev -> this.toFront());
			contextMenu.getItems().add(e);
		}
		{
			MenuItem e = new MenuItem("To - Back");
			e.setOnAction(ev -> this.toBack());
			contextMenu.getItems().add(e);
		}
		return contextMenu;
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}

	public final T getControl() {
		return control;
	}

	/**
	 * 컨테이너 안의 디자인요소를 구현하여 container에 input할 코드를 기술.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 8. 31.
	 * @param container
	 */
	public abstract void draw(VBox container);

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
	}

	@Override
	public void requestBackgroundColorChange(Color newColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestTextFillChange(Color newColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestBorderColorChange(Color newColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void containerMouseAction(MouseEvent e) {

	}

	@Override
	public void containerKeyAction(KeyEvent e) {

		switch (e.getCode()) {
		case DOWN: {
			DrawItem editor = getControl();
			editor.setLayoutY(editor.getLayoutY() + 1);
			e.consume();
		}
			break;
		case UP: {
			DrawItem editor = getControl();
			editor.setLayoutY(editor.getLayoutY() - 1);
			e.consume();
		}
			break;
		case LEFT: {
			DrawItem editor = getControl();
			editor.setLayoutX(editor.getLayoutX() - 1);
			e.consume();
		}
			break;
		case RIGHT: {
			DrawItem editor = getControl();
			editor.setLayoutX(editor.getLayoutX() + 1);
			e.consume();
		}
			break;
		default:
			// Nothing....
		}

	}

	@Override
	public void toFront() {
		getControl().toFront();
	}

	@Override
	public void toBack() {
		getControl().toBack();
	}

}
