/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 4. 5.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.monitor.bci.view;

import java.util.Collection;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.bci.monitor.MonitorListener;
import com.kyj.bci.monitor.Monitors;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.main.Main;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author KYJ
 *
 */

public class JavaProcessViewController implements MonitorListener, PrimaryStageCloseable {

	private static Logger LOGGER = LoggerFactory.getLogger(JavaProcessViewController.class);

	@FXML
	private TableView<ApplicationModel> tbJavaApplication;

	private Predicate<ApplicationModel> filter = new DefaultJavaMonitorFilter();

	@FXML
	public void initialize() {

		Monitors.addListener(this);
		Main.addPrimaryStageCloseListener(this);

	}

	@Override
	public void onApplicationLoaded(ApplicationModel model) {

		if (filter.test(model)) {
			tbJavaApplication.getItems().add(model);
		}

	}

	@Override
	public void onApplicationTerminated(ApplicationModel model) {
		tbJavaApplication.getItems().remove(model);
	}

	public TableView<ApplicationModel> getTbJavaApplication() {
		return tbJavaApplication;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 */
	public void closeRequest() {
		LOGGER.debug("Close Request Listener ");
		Monitors.removeListener(this);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 */
	public void onTickTock() {
		try {

			Collection<ApplicationModel> activedApplicationModel = Monitors.getActivedJavaProceses();
			tbJavaApplication.getItems().setAll(activedApplicationModel);

		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
