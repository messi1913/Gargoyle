/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.controlsfx.control.table.TableFilter;
import org.controlsfx.control.table.TableFilter.Builder;

import com.kyj.fx.voeditor.visual.component.grid.AnnotateBizOptions;
import com.kyj.fx.voeditor.visual.framework.logview.helper.core.EMRLogViewHelper;
import com.kyj.fx.voeditor.visual.framework.logview.helper.core.EMRLogViewHelper.DefaultFxDataInfo;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableView;

/**
 * @author KYJ
 *
 */
public class EMRServiceLogTableViewHelper extends Thread {

	private TableView<DefaultFxDataInfo> view;
	private String text;
	private EMRLogViewHelper h;

	/**
	 */
	public EMRServiceLogTableViewHelper(String text) {
		this.text = text;
		view = new TableView<DefaultFxDataInfo>();
		view.getSelectionModel().setCellSelectionEnabled(true);
		FxUtil.installCommonsTableView(DefaultFxDataInfo.class, view, new AnnotateBizOptions<DefaultFxDataInfo>(DefaultFxDataInfo.class) {

			@Override
			public int columnSize(String columnName) {
				switch (columnName) {
				case "dateString":
					return 200;
				case "xml":
					return 600;
				}
				return super.columnSize(columnName);
			}

			@Override
			public boolean visible(String columnName) {
				if ("commonsClicked".equals(columnName))
					return false;
				return super.visible(columnName);
			}

		});

		FxUtil.installClipboardKeyEvent(view);

		view.setContextMenu(new ContextMenu(FxUtil.createMenuItemExcelExport(view)));

		// this.view = new CommonsBaseGridView<>(DefaultFxDataInfo.class);
		this.h = new EMRLogViewHelper();
	}

	@Override
	public void run() {

		List<DefaultFxDataInfo> items = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new StringReader(this.text))) {

			String tmp = null;
			while ((tmp = r.readLine()) != null) {
				h.setText(tmp);
				h.read();
				DefaultFxDataInfo fxValue = h.getFxValue();
				if (fxValue != null)
					items.add(fxValue);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		onCompleted(items);
	}

	private Consumer<List<DefaultFxDataInfo>> action = items -> {
		Platform.runLater(() -> {
			this.view.getItems().addAll(items);
			
			// Install Table Filter.
			Builder<DefaultFxDataInfo> forTableView = TableFilter.forTableView(view);
			forTableView.lazy(true);
			forTableView.apply();
		});

	};

	public void setOnCompleted(Consumer<List<DefaultFxDataInfo>> action) {
		this.action = action;
	}

	private void onCompleted(List<DefaultFxDataInfo> items) {
		action.accept(items);
	}

	public TableView<DefaultFxDataInfo> getView() {
		return this.view;
	}

}
