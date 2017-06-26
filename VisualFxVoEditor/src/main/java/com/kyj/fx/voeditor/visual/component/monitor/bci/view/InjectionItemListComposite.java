/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bci.view
 *	작성일   : 2016. 5. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.monitor.bci.view;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.InstanceTypes;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/***************************
 *
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "InjectionItemListView.fxml", isSelfController = true, instanceType = InstanceTypes.RequireNew)
public class InjectionItemListComposite extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(InjectionItemListComposite.class);

	private static final String CODE_DIR = "btrace/items";
	private File itemsDir;

	private final ToggleGroup value = new ToggleGroup();
	@FXML
	private ListView<CodeItem> lvItems;

	private ObjectProperty<CodeItem> selectedItem = new SimpleObjectProperty<>();

	public InjectionItemListComposite() throws Exception {
		FxUtil.loadRoot(this.getClass(), this);
	}

	@FXML
	public void initialize() {
		lvItems.setCellFactory(new Callback<ListView<CodeItem>, ListCell<CodeItem>>() {

			@Override
			public ListCell<CodeItem> call(ListView<CodeItem> param) {
				return new ItemListCell();
			}
		});

	}

	@FxPostInitialize
	public void loaditems() {
		itemsDir = new File(CODE_DIR);
		if (!itemsDir.exists()) {
			itemsDir.mkdirs();
		}

		List<CodeItem> collect = Stream.of(itemsDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".java");
			}
		})).map(f -> {
			CodeItem codeItem = new CodeItem();
			codeItem.setFile(f);
			codeItem.setName(f.getName());
			return codeItem;
		}).collect(Collectors.toList());

		lvItems.getItems().addAll(collect);

	}

	class ItemListCell extends ListCell<CodeItem> {
		HBox hBox;
		Label label;
		RadioButton rd;

		ItemListCell() {
			hBox = new HBox();
			rd = new RadioButton();

			rd.setOnMouseClicked(ev -> {
				getListView().getItems().forEach(v -> v.setSelection(false));
				CodeItem codeItem = getListView().getItems().get(ItemListCell.this.getIndex());
				codeItem.setSelection(true);
				selectedItem.set(codeItem);
				rd.setSelected(true);
			});

			rd.setToggleGroup(value);
			label = new Label();
			hBox.getChildren().addAll(rd, label);
		}

		void updateText(String text) {
			label.setText(text);
		}

		public void updateRdSelection(CodeItem item) {
			rd.setSelected(item.isSelection());
		}

		@Override
		public void updateIndex(int i) {
			super.updateIndex(i);

		}

		void updateRdoSelected(boolean iss) {
			rd.setSelected(iss);
		}

		@Override
		protected void updateItem(CodeItem item, boolean empty) {
			super.updateItem(item, empty);

			setText(null);
			if (empty) {
				setGraphic(null);
			} else {
				setGraphic(hBox);
				updateText(item.getName());
				updateRdSelection(item);
			}

		}
	}

	public final ReadOnlyObjectWrapper<CodeItem> selectedItemProperty() {
		return new ReadOnlyObjectWrapper<CodeItem>(this.selectedItem.get());
	}

	public final CodeItem getSelectedItem() {
		return this.selectedItemProperty().get();
	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	// 
	/***********************************************************************************/
}
