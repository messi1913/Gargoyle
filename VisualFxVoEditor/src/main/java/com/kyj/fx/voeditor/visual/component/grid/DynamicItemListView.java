/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.grid
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class DynamicItemListView extends ListView<DynamicItem<? extends Node>> {

	public DynamicItemListView() {
		super();
		init();
	}

	public DynamicItemListView(ObservableList<DynamicItem<?>> items) {
		super(items);
		init();
	}

	public void init() {
		setCellFactory(new Callback<ListView<DynamicItem<?>>, ListCell<DynamicItem<?>>>() {

			@Override
			public ListCell<DynamicItem<?>> call(ListView<DynamicItem<?>> param) {
				return new ListCell<DynamicItem<?>>() {

					/*
					 * (non-Javadoc)
					 *
					 * @see
					 * javafx.scene.control.Cell#updateItem(java.lang.Object,
					 * boolean)
					 */
					@Override
					protected void updateItem(DynamicItem<?> item, boolean empty) {
						super.updateItem(item, empty);

						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(item.getItem());
						}
					}

				};
			}
		});

		setOnMouseClicked(event -> {
			DynamicItem<? extends Node> selectedItem = getSelectionModel().getSelectedItem();
			selectedItem.setOnMouseClicked(event);
		});
	}

}
