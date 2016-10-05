/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 9. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MergedRowSkin<T, S> implements Skin<MergedRow<T, S>> {

	private MergedRow<T, S> row;

	public MergedRowSkin(MergedRow<T, S> row) {
		this.row = row;

	}

	@Override
	public MergedRow<T, S> getSkinnable() {
		return this.row;
	}

	@Override
	public Node getNode() {
		TableView<T> tableView = this.row.getTableView();
		TableColumn<T, S> tableColumn = this.row.getTableColumn();

		ObservableValue<S> cellObservableValue = tableColumn.getCellObservableValue(0);
		Label label = new Label();
		label.setText(cellObservableValue.getValue().toString());
		return label;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
