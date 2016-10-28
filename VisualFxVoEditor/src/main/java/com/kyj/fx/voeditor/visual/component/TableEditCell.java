package com.kyj.fx.voeditor.visual.component;

import java.util.function.Predicate;

import javafx.event.Event;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class TableEditCell<S, T> extends TableCell<S, T> {

	private final TextField textField = new TextField();

	private final StringConverter<T> converter;

	private final Predicate<S> editPredicate;

	public TableEditCell(StringConverter<T> converter) {
		this(converter, t -> true);
	}

	public TableEditCell(StringConverter<T> converter, Predicate<S> editPredicate) {
		this.converter = converter;
		this.editPredicate = editPredicate;
		itemProperty().addListener((obx, oldItem, newItem) -> {
			if (newItem == null) {
				setText(null);
			} else {
				setText(converter.toString(newItem));
			}
		});
		setGraphic(textField);
		setContentDisplay(ContentDisplay.TEXT_ONLY);

		textField.setOnAction(evt -> {
			commitEdit(this.converter.fromString(textField.getText()));
		});
		textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (!isNowFocused) {
				commitEdit(this.converter.fromString(textField.getText()));
			}
		});
		textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				textField.setText(converter.toString(getItem()));
				cancelEdit();
				event.consume();
			} else if (event.getCode() == KeyCode.RIGHT) {
				getTableView().getSelectionModel().selectRightCell();
				event.consume();
			} else if (event.getCode() == KeyCode.LEFT) {
				getTableView().getSelectionModel().selectLeftCell();
				event.consume();
			} else if (event.getCode() == KeyCode.UP) {
				getTableView().getSelectionModel().selectAboveCell();
				event.consume();
			} else if (event.getCode() == KeyCode.DOWN) {
				getTableView().getSelectionModel().selectBelowCell();
				event.consume();
			}
		});
	}

	// set the text of the text field and display the graphic
	@SuppressWarnings("unchecked")
	@Override
	public void startEdit() {
		if (!editPredicate.test(((S) getTableRow().getItem()))) {
			return;
		}
		super.startEdit();
		textField.setText(converter.toString(getItem()));
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		textField.requestFocus();
	}

	// revert to text display
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setContentDisplay(ContentDisplay.TEXT_ONLY);
	}

	// commits the edit. Update property if possible and revert to text display
	@Override
	public void commitEdit(T item) {

		if (!isEditing() && !item.equals(getItem())) {
			TableView<S> table = getTableView();
			if (table != null) {
				TableColumn<S, T> column = getTableColumn();

//				S dvo = getTableView().getItems().get(getIndex());
//				if (!CommonConst._STATUS_CREATE.equals(dvo.get_status())) {
//					dvo.set_status(CommonConst._STATUS_UPDATE);
//				}
				// ObservableValue<T> ov = column.getCellObservableValue(dvo);
				// if (ov instanceof WritableValue) {
				// ((WritableValue) ov).setValue(item);
				// }
				CellEditEvent<S, T> event = new CellEditEvent<>(table, new TablePosition<S, T>(table, getIndex(), column),
						TableColumn.editCommitEvent(), item);
				Event.fireEvent(column, event);
			}
		}
		super.commitEdit(item);
		getTableView().refresh();
	}

}
