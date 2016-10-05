/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.view;

import java.net.URL;

import com.kyj.fx.voeditor.visual.component.config.item.node.AbstractRunItem;
import com.kyj.fx.voeditor.visual.component.config.model.RunItemCatchModel;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */

@FXMLController(value = "RunConfigView.fxml", isSelfController = true)
public class RunConfigView extends BorderPane {

	@FXML
	private ListView<AbstractRunItem> lvRunItem;

	@FXML
	private TextField txtRunItemFilter;

	private ObservableList<AbstractRunItem> runnItems = FXCollections.observableArrayList();

	private SimpleObjectProperty<AbstractRunItem> activedRunItem = new SimpleObjectProperty<>();

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
		ObservableList<AbstractRunItem> observableArrayList = FXCollections.observableArrayList(runItemCatchModel.getAllItems());
		runnItems.addAll(observableArrayList);
		lvRunItem.getItems().addAll(runnItems);

		txtRunItemFilter.textProperty().addListener(txtRunItemFilterChangeListener);
	}

	private ChangeListener<? super String> txtRunItemFilterChangeListener = (oba, o, n) -> {

		ObservableList<AbstractRunItem> items = lvRunItem.getItems();
		items.clear();
		items.addAll(runnItems);
		if (ValueUtil.isNotEmpty(n)) {
			ObservableList<AbstractRunItem> currentItems = items;
			int size = currentItems.size();
			for (int i = size - 1; i >= 0; i--) {
				AbstractRunItem currentItem = currentItems.get(i);
				String displayName = currentItem.getDisplayName();

				if (!ValueUtil.hasTextIgnorecase(displayName, n))
					items.remove(i);
			}
		}

	};

}
