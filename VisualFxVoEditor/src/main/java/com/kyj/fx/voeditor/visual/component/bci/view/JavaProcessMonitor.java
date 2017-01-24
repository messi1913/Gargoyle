/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bci.view
 *	작성일   : 2016. 5. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.bci.monitor.Monitors;
import com.kyj.fx.voeditor.visual.component.text.ThreadDumpTextArea;
import com.kyj.fx.voeditor.visual.framework.JavaLauncher;
import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
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
import javafx.stage.Stage;
import javafx.stage.Window;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class JavaProcessMonitor extends CloseableParent<SplitPane> {

	private static Logger LOGGER = LoggerFactory.getLogger(JavaProcessMonitor.class);

	private TableView<ApplicationModel> tbJavaApplication;
	private InjectionItemListComposite injectionItemListComposite;
	private ScheduleTimeLineComposite chart;
	private JavaProcessViewController controller;
	public JavaProcessMonitor() throws Exception {
		super(new SplitPane());


		FXMLLoader loader = new FXMLLoader(JavaProcessMonitor.class.getResource("JavaProcessView.fxml"));
		Node load = loader.load();
		controller = loader.getController();
		chart = new ScheduleTimeLineComposite(controller);

		SplitPane splitPane = new SplitPane(load, chart);

		tbJavaApplication = controller.getTbJavaApplication();
		Menu menu = new Menu("task");
		MenuItem menuInjection = new MenuItem("injection");
		MenuItem menuTaskkill = new MenuItem("task kill");
		MenuItem menuForceTaskkill = new MenuItem("fource task kill");
		MenuItem menuThreadDump = new MenuItem("thread dump");

		menu.getItems().add(menuInjection);
		menu.getItems().add(menuTaskkill);
		menu.getItems().add(menuForceTaskkill);
		menu.getItems().add(menuThreadDump);

		menuInjection.setOnAction(this::menuInjectionOnAction);
		menuTaskkill.setOnAction(this::menuTaskkillOnAction);
		menuForceTaskkill.setOnAction(this::menuForceTaskkillOnAction);
		menuThreadDump.setOnAction(this::menuThreadDumpOnAction);
		tbJavaApplication.setContextMenu(new ContextMenu(menu));

		splitPane.setOrientation(Orientation.VERTICAL);
		injectionItemListComposite = new InjectionItemListComposite();

		getParent().getItems().addAll(Arrays.asList(injectionItemListComposite, splitPane));
		getParent().setDividerPositions(0.2d);

	}

	/**
	 * 선탠된 아이템에 대한 속성중에 process id값을 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 24.
	 * @return
	 */
	private Integer getSelectedProcessId() {
//		ApplicationModel selectedItem = tbJavaApplication.getSelectionModel().getSelectedItem();
//		if (selectedItem != null) {
//			return selectedItem.getProcessId();
//		}
//		return -1;
//
		return getSelectedItem(item ->{
			if(item == null)
				return -1;
			return item.getProcessId();
		});
	}

	/**
	 * 선택된 데이터를 다른 타입으로 바꿈
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 24.
	 * @param convert
	 * @return
	 */
	private <T> T getSelectedItem(Function<ApplicationModel, T> convert) {
		ApplicationModel selectedItem = tbJavaApplication.getSelectionModel().getSelectedItem();
		return convert.apply(selectedItem);
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

	/**
	 * Thread Dump
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 * @param e
	 * @throws Exception
	 */
	public void menuThreadDumpOnAction(ActionEvent e) {


		ApplicationModel selectedItem = getSelectedItem(item -> item);
		if(selectedItem == null)
		{
			Window window = FxUtil.getWindow(this.getParent(), ()->{
				return SharedMemory.getPrimaryStage();
			});

			DialogUtil.showMessageDialog(window, "덤프를 출력할 프로세스를 선택.");
			return;
		}

		Integer selectedProcessId = selectedItem.getProcessId();
		String applicationName = selectedItem.getApplicationName();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Monitors.runStackTool(selectedProcessId, out);

		ThreadDumpTextArea parent = new ThreadDumpTextArea();
		parent.setContent(out.toString());
		FxUtil.createStageAndShow(parent, stage ->{
			stage.initOwner(getParent().getScene().getWindow());
			stage.setWidth(1200d);
			stage.setHeight(800d);
			stage.setTitle("App - "+applicationName);
		});

//		FxUtil.createSimpleTextAreaAndShow(out.toString(), stage -> {
//
//
//		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.main.layout.CloseableParent#close()
	 */
	@Override
	public void close() throws IOException {
		if (chart != null)
			chart.closeRequest();

		if(controller!=null)
			controller.closeRequest();
	}

}
