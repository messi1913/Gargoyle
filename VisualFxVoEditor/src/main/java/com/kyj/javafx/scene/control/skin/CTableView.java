/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.javafx.scene.control.skin
 *	작성일   : 2017. 7. 6.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.javafx.scene.control.skin;

import java.util.BitSet;

import org.controlsfx.control.spreadsheet.SpreadsheetColumn;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * @author KYJ
 *
 */
public class CTableView<T> extends TableView<T> {

	private final BooleanProperty fixingColumnsAllowedProperty = new SimpleBooleanProperty(true);

	/**
	* Return whether change to Fixed columns are allowed.
	*
	* @return whether change to Fixed columns are allowed.
	*/
	public boolean isFixingColumnsAllowed() {
		return fixingColumnsAllowedProperty.get();
	}

	public BooleanProperty fixingColumnsAllowedProperty() {
		return fixingColumnsAllowedProperty;
	}

	private ObservableList<TableColumn<T, ?>> fixedColumns = FXCollections.observableArrayList();

	public ObservableList<TableColumn<T, ?>> getFixedColumns() {
		return fixedColumns;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new CTableViewSkin<>(this);
	}



}
