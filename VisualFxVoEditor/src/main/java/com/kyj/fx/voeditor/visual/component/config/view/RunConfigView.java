/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.view;

import java.net.URL;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem;
import com.kyj.fx.voeditor.visual.component.config.model.RunItemCatchModel;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */

@FXMLController(value = "RunConfigView.fxml", isSelfController = true)
public class RunConfigView extends BorderPane {

	@FXML
	private TabPane tabPaneContents;

	@FXML
	private ListView<IRunableItem> lvRunItem;

	@FXML
	private TextField txtRunItemFilter;
	@FXML
	private TextField txtDisplayName;

	private ObservableList<IRunableItem> runnItems = FXCollections.observableArrayList();

	private SimpleObjectProperty<IRunableItem> activedRunItem = new SimpleObjectProperty<>();

	/**
	 * 실행가능한 아이템들에 대한 정보를 리턴해주는 역할처리.
	 * @최초생성일 2016. 10. 5.
	 */
	private RunItemCatchModel runItemCatchModel = new RunItemCatchModel();

	/**
	 * construnct
	 * @throws Exception
	 */
	public RunConfigView(URL sourceURL) throws Exception {
		FxUtil.loadRoot(RunConfigView.class, this);
	}

	@FXML
	public void initialize() {
		ObservableList<IRunableItem> observableArrayList = FXCollections.observableArrayList(runItemCatchModel.getAllItems());
		runnItems.addAll(observableArrayList);
		lvRunItem.getItems().addAll(runnItems);

		txtRunItemFilter.textProperty().addListener(txtRunItemFilterChangeListener);

		activedRunItem.addListener(activeRunListener);

	}

	private ChangeListener<IRunableItem> activeRunListener = (ChangeListener<IRunableItem>) (observable, oldValue, newValue) -> {

		ObservableList<RunConfigTab> tabs = newValue.getTabs();
		tabPaneContents.getTabs().clear();
		tabPaneContents.getTabs().addAll(tabs);
	};

	private ChangeListener<? super String> txtRunItemFilterChangeListener = (oba, o, n) -> {

		ObservableList<IRunableItem> items = lvRunItem.getItems();
		items.clear();
		items.addAll(runnItems);
		if (ValueUtil.isNotEmpty(n)) {
			ObservableList<IRunableItem> currentItems = items;
			int size = currentItems.size();
			for (int i = size - 1; i >= 0; i--) {
				IRunableItem currentItem = currentItems.get(i);
				String displayName = currentItem.getDisplayName();

				if (!ValueUtil.hasTextIgnorecase(displayName, n))
					items.remove(i);
			}
		}

	};

	public void addNewRunItem(IRunableItem item) {
		runnItems.add(item);
		activedRunItem.set(item);
		String displayName = item.getDisplayName();
		txtDisplayName.setText(displayName);
	}

}
