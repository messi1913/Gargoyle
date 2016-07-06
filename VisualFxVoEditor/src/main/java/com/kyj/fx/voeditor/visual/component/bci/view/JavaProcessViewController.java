/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 4. 5.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.util.function.Predicate;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.bci.monitor.MonitorListener;
import com.kyj.bci.monitor.Monitors;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

/**
 * @author KYJ
 *
 */

public class JavaProcessViewController implements MonitorListener {

	@FXML
	private TableView<ApplicationModel> tbJavaApplication;

	private Predicate<ApplicationModel> filter = new DefaultJavaMonitorFilter();

	@FXML
	public void initialize() {
		Monitors.addListener(this);
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
}
