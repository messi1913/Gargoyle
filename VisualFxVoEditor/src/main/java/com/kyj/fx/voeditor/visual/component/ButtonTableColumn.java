/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.ButtonTableColumn
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Map;
import java.util.function.Function;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

/**
 * @author Hong
 *
 *         2016.4.26 KYJ 버튼에 대한 기능작동에 대한 버그 및 사용안하는 기능 제거. - 버튼 텍스트가 출력이 안되어 코드
 *         수정.
 *
 */
public class ButtonTableColumn<S extends Map<String, Object>> extends TableColumn<S, Boolean> {

	private Function<Integer, Boolean> changeFunc;

	public ButtonTableColumn() {
		initialize("", i -> true);
	}

	public ButtonTableColumn(String buttonText) {
		initialize(buttonText, i -> true);
	}

	private void initialize(String buttonText, Function<Integer, Boolean> changeFunc) {

		this.changeFunc = changeFunc == null ? (i -> true) : changeFunc;
		this.setStyle(" -fx-alignment: CENTER;");
		ButtonCellFactory value = new ButtonCellFactory(buttonText, this);
		this.setCellFactory(value);
		this.setCellValueFactory(p -> new SimpleBooleanProperty(p.getValue() != null));
	}

	/**
	 *
	 * @return the changeFunc
	 */
	public Function<Integer, Boolean> getChangeFunc() {
		return changeFunc;
	}

	/**
	 * @param changeFunc
	 *            the changeFunc to set
	 */
	public void setChangeFunc(Function<Integer, Boolean> changeFunc) {
		this.changeFunc = changeFunc;
	}

	public void clickHandle(int nRow) {
	}

	class ButtonCellFactory implements Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> {
		ButtonTableColumn<?> parent;
		private String buttonText;

		ButtonCellFactory(final String buttonText, ButtonTableColumn<?> parent) {
			this.parent = parent;
			this.buttonText = buttonText;
		}

		@Override
		public TableCell<S, Boolean> call(final TableColumn<S, Boolean> param) {

			final ButtonCell<S> buttonCell = new ButtonCell<>(buttonText, ButtonTableColumn.this);
			Button cellButton = buttonCell.getButtonObject();

			cellButton.textProperty().addListener(new ChangeListener<String>() {

				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if (true) {
						cellButton.setText(newValue);
					}

				}
			});

			cellButton.textFillProperty().addListener(new ChangeListener<Paint>() {
				@Override
				public void changed(ObservableValue<? extends Paint> observable, Paint oldValue, Paint newValue) {

				}
			});
			// cellButton.styleProperty().bind(button.styleProperty());
			// cellButton.visibleProperty().bind(button.visibleProperty());
			// cellButton.disableProperty().bind(button.disableProperty());
			// cellButton.fontProperty().bind(button.fontProperty());
			// cellButton.alignmentProperty().bind(button.alignmentProperty());
			// cellButton.textAlignmentProperty().bind(button.textAlignmentProperty());
			// cellButton.textOverrunProperty().bind(button.textOverrunProperty());
			// cellButton.ellipsisStringProperty().bind(button.ellipsisStringProperty());
			// cellButton.wrapTextProperty().bind(button.wrapTextProperty());
			// cellButton.graphicProperty().bind(button.graphicProperty());
			// cellButton.underlineProperty().bind(button.underlineProperty());
			// cellButton.contentDisplayProperty().bind(button.contentDisplayProperty());
			// cellButton.lineSpacingProperty().bind(button.lineSpacingProperty());
			// cellButton.graphicTextGapProperty().bind(button.graphicTextGapProperty());
			cellButton.setOnAction(e -> parent.clickHandle(buttonCell.getRowIndex()));

			return buttonCell;

		}
	}

	@SuppressWarnings("hiding")
	class ButtonCell<S extends Map<String, Object>> extends TableCell<S, Boolean> {
		private final Button button;

		private ButtonCell(final String buttonText, TableColumn<S, Boolean> column) {
			this.button = new Button(buttonText);
		}

		private Button getButtonObject() {
			return this.button;
		}

		private int getRowIndex() {
			return getIndex();
		}

		@Override
		protected void updateItem(Boolean t, boolean empty) {
			super.updateItem(t, empty);
			if (!empty && getChangeFunc().apply(getIndex())) {
				setGraphic(button);
			} else {
				setGraphic(null);
			}
		}
	}

}
