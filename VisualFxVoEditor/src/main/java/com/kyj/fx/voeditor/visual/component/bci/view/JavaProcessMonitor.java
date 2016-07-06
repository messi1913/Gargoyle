/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bci.view
 *	작성일   : 2016. 5. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.fx.voeditor.visual.framework.JavaLauncher;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class JavaProcessMonitor extends SplitPane {

	private static Logger LOGGER = LoggerFactory.getLogger(JavaProcessMonitor.class);

	private TableView<ApplicationModel> tbJavaApplication;
	private InjectionItemListComposite injectionItemListComposite;

	public JavaProcessMonitor() throws Exception {

		ScheduleTimeLineComposite chart = new ScheduleTimeLineComposite();

		FXMLLoader loader = new FXMLLoader(JavaProcessMonitor.class.getResource("JavaProcessView.fxml"));
		Node load = loader.load();
		JavaProcessViewController controller = loader.getController();
		SplitPane splitPane = new SplitPane(load, chart);

		tbJavaApplication = controller.getTbJavaApplication();
		Menu menu = new Menu("task");
		MenuItem menuInjection = new MenuItem("injection");
		MenuItem menuTaskkill = new MenuItem("task kill");
		MenuItem menuForceTaskkill = new MenuItem("fource task kill");

		menu.getItems().add(menuInjection);
		menu.getItems().add(menuTaskkill);
		menu.getItems().add(menuForceTaskkill);

		menuInjection.setOnAction(this::menuInjectionOnAction);
		menuTaskkill.setOnAction(this::menuTaskkillOnAction);
		menuForceTaskkill.setOnAction(this::menuForceTaskkillOnAction);

		tbJavaApplication.setContextMenu(new ContextMenu(menu));

		splitPane.setOrientation(Orientation.VERTICAL);
		injectionItemListComposite = new InjectionItemListComposite();
		getItems().addAll(Arrays.asList(injectionItemListComposite, splitPane));

	}

	private Integer getSelectedProcessId() {
		ApplicationModel selectedItem = tbJavaApplication.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			return selectedItem.getProcessId();
		}
		return -1;
	}

	public void menuInjectionOnAction(ActionEvent e) {
		CodeItem selectedItem = injectionItemListComposite.getSelectedItem();
		ApplicationModel applicationItem = tbJavaApplication.getSelectionModel().getSelectedItem();

		if (ValueUtil.isNotEmpty(selectedItem) && ValueUtil.isNotEmpty(applicationItem)) {
			LOGGER.debug("application pid : {}  codeitem :{}", applicationItem.getProcessId(), selectedItem.getName());
			BtraceRunner.run(applicationItem, selectedItem.getFile());
		}

	}

	public void menuTaskkillOnAction(ActionEvent e) {
		try {
			Integer selectedProcessId = getSelectedProcessId();
			JavaLauncher.taskkillProcess(selectedProcessId);
		} catch (Exception ex) {
			LOGGER.error(ValueUtil.toString(ex));
		}

	}

	public void menuForceTaskkillOnAction(ActionEvent e) {
		try {
			Integer selectedProcessId = getSelectedProcessId();
			JavaLauncher.taskFourceKillProcess(selectedProcessId);
		} catch (Exception ex) {
			LOGGER.error(ValueUtil.toString(ex));
		}
	}

}
