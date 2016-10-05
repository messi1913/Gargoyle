/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 9. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MergedTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {

	/***************************************************************************
	 * * Static cell factories * *
	 **************************************************************************/

	/**
	 * Provides a {@link TextField} that allows editing of the cell content when the cell is double-clicked, or when {@link TableView#edit(int, javafx.scene.control.TableColumn)} is called. This
	 * method will only work on {@link TableColumn} instances which are of type String.
	 *
	 * @return A {@link Callback} that can be inserted into the {@link TableColumn#cellFactoryProperty() cell factory property} of a TableColumn, that enables textual editing of the content.
	 */
	public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
		return forTableColumn(new DefaultStringConverter());
	}

	/**
	 * Provides a {@link TextField} that allows editing of the cell content when the cell is double-clicked, or when {@link TableView#edit(int, javafx.scene.control.TableColumn) } is called. This
	 * method will work on any {@link TableColumn} instance, regardless of its generic type. However, to enable this, a {@link StringConverter} must be provided that will convert the given String
	 * (from what the user typed in) into an instance of type T. This item will then be passed along to the {@link TableColumn#onEditCommitProperty()} callback.
	 *
	 * @param converter
	 *            A {@link StringConverter} that can convert the given String (from what the user typed in) into an instance of type T.
	 * @return A {@link Callback} that can be inserted into the {@link TableColumn#cellFactoryProperty() cell factory property} of a TableColumn, that enables textual editing of the content.
	 */
	public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
		return list -> new MergedTextFieldTableCell<S, T>(converter);
	}

	/***************************************************************************
	 * * Constructors * *
	 **************************************************************************/

	/**
	 * Creates a default TextFieldTableCell with a null converter. Without a {@link StringConverter} specified, this cell will not be able to accept input from the TextField (as it will not know how
	 * to convert this back to the domain object). It is therefore strongly encouraged to not use this constructor unless you intend to set the converter separately.
	 */
	public MergedTextFieldTableCell() {
		this(null);
	}

	/**
	 * Creates a TextFieldTableCell that provides a {@link TextField} when put into editing mode that allows editing of the cell content. This method will work on any TableColumn instance, regardless
	 * of its generic type. However, to enable this, a {@link StringConverter} must be provided that will convert the given String (from what the user typed in) into an instance of type T. This item
	 * will then be passed along to the {@link TableColumn#onEditCommitProperty()} callback.
	 *
	 * @param converter
	 *            A {@link StringConverter converter} that can convert the given String (from what the user typed in) into an instance of type T.
	 */
	public MergedTextFieldTableCell(StringConverter<T> converter) {
		this.getStylesheets().add(MergedTextFieldTableCell.class.getResource("mergedTableCellStyle.css").toExternalForm());
		this.getStyleClass().add("merged-text-field-table-cell");
		setConverter(converter);
	}

	/***************************************************************************
	 * * Properties * *
	 **************************************************************************/

	/** {@inheritDoc} */
	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (!empty) {
			int index = getIndex();
			
			if (index >= 1  /*&& index < size*/) {
				T beforeData = getTableColumn().getCellData(index - 1);
//				T afterData = getTableColumn().getCellData(index + 1);
				if (item.equals(beforeData) /*&& item.equals(afterData)*/) {
					setStyle("-fx-border-color:transparent transparent transparent transparent");
					setText("");
				}
			}


		} else {
			setStyle(null);
		}
	}

}
