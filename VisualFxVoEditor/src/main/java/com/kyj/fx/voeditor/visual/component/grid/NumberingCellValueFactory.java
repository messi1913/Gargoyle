/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 10. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

/**
 * 컬럼에 No를 붙이는 기능을 처리하는 ValueFactory
 *
 * @author KYJ
 *
 */
public class NumberingCellValueFactory<T> implements Callback<CellDataFeatures<T, Integer>, ObservableValue<Integer>> {

	private List<T> items;

	public NumberingCellValueFactory(List<T> items) {
		this.items = items;
	}

	@Override
	public ObservableValue<Integer> call(CellDataFeatures<T, Integer> param) {
		return new ReadOnlyObjectWrapper<Integer>(items.indexOf(param.getValue()) + 1);
	}

}
