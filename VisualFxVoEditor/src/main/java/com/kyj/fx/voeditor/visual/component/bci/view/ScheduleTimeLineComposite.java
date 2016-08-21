/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 4. 5.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.bci.monitor.MonitorListener;
import com.kyj.bci.monitor.Monitors;
import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.main.Main;
import com.kyj.fx.voeditor.visual.momory.MonitorProperties;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ScheduleTimeLineView.fxml", isSelfController = true)
public class ScheduleTimeLineComposite extends BorderPane implements MonitorListener, PrimaryStageCloseable {

	private static final int CHART_SHOWING_ITEM_COUNT = MonitorProperties.getInstance()
			.getInteger(MonitorProperties.CHART_SHOWING_ITEM_COUNT);

	private static Logger LOGGER = LoggerFactory.getLogger(ScheduleTimeLineComposite.class);

	private Predicate<ApplicationModel> filter = new DefaultJavaMonitorFilter();

	@FXML
	private LineChart<String, Number> chartTimeLine;
	private Series<String, Number> passBatchProcessSeries;
	// private Series<String, Number> allJvmProcessSeries;

	private AtomicBoolean running = new AtomicBoolean(true);
	private SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
	private AtomicInteger batchProcessCounting = new AtomicInteger(0);

	// private AtomicInteger allProcessCounting = new AtomicInteger(0);
	Service<Void> monitorService;

	public ScheduleTimeLineComposite() throws Exception {
		FxUtil.loadRoot(ScheduleTimeLineComposite.class, this);
	}

	@FXML
	public void initialize() {

		Monitors.addListener(this);
		Main.addPrimaryStageCloseListener(this);

		chartTimeLine.setVerticalGridLinesVisible(false);
		ObservableList<Series<String, Number>> data = chartTimeLine.getData();

		// allJvmProcessSeries = new Series<String, Number>();
		// allJvmProcessSeries.setName("All JVM Process");
		// data.add(allJvmProcessSeries);

		passBatchProcessSeries = new Series<String, Number>();
		passBatchProcessSeries.setName("Java Process");
		data.add(passBatchProcessSeries);
		// monitorService.setOnRunning(ev ->{
		// System.err.println("running~~~~~~~~~~~~~~~~~~~");
		// });
		// monitorService.setOnScheduled(ev ->{
		// System.err.println("service schedule.....");
		// });

		monitorService = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						Thread.sleep(1000);
						String mmss = format.format(new Date());
						updateMessage(mmss);
						return null;
					}
				};

			}
		};

		monitorService.messageProperty().addListener((oba, oldval, newval) -> {
			if (newval == null || newval.isEmpty())
				return;

			ObservableList<Data<String, Number>> seriesData = passBatchProcessSeries.getData();

			Data<String, Number> e = new Data<String, Number>(newval, batchProcessCounting.get());
			e.setNode(new Rectangle(1, 1));
			seriesData.add(e);
			if (seriesData.size() > CHART_SHOWING_ITEM_COUNT) {
				seriesData.remove(0);
			}
		});

		monitorService.setOnSucceeded(ev -> {
			if (running.get())
				monitorService.restart();
		});

		monitorService.start();

	}

	@Override
	public void onApplicationLoaded(ApplicationModel model) {
		if (filter.test(model)) {
			batchProcessCounting.addAndGet(1);
		}
		// allProcessCounting.addAndGet(1);
	}

	@Override
	public void onApplicationTerminated(ApplicationModel model) {
		if (filter.test(model))
			batchProcessCounting.addAndGet(-1);
		// allProcessCounting.addAndGet(-1);
	}

	@Override
	public void closeRequest() {
		LOGGER.debug("close request...");

		running.set(false);
		if (monitorService.isRunning())
			monitorService.cancel();
	}

}
