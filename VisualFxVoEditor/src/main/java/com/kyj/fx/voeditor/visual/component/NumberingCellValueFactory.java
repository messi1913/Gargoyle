/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 10. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class NumberingCellValueFactory<T> implements Callback<CellDataFeatures<T, Integer>, ObservableValue<Integer>> {

	private List<T> items;

	public NumberingCellValueFactory(List<T> items) {
		this.items = items;
	}

	public NumberingCellValueFactory(TableView<T> tb) {
		this.items = tb.getItems();
	}

	public NumberingCellValueFactory(TableColumn<T, Integer> col) {

		this.items = col.getTableView().itemsProperty().get();
	}

	@Override
	public ObservableValue<Integer> call(CellDataFeatures<T, Integer> param) {
		return new ReadOnlyObjectWrapper<Integer>(items.indexOf(param.getValue()) + 1);
	}

}
