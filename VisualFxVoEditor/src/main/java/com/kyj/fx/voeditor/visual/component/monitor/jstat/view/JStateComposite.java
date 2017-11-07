/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.monitor.jstat.view
 *	작성일   : 2017. 7. 17.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.monitor.jstat.view;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.Monitors;
import com.kyj.bci.monitor.Monitors.SnapShotType;
import com.kyj.bci.monitor.SnapShotListener;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.BaseOptions;
import com.kyj.fx.voeditor.visual.framework.excel.IExcelScreenHandler;
import com.kyj.fx.voeditor.visual.framework.thread.DemonTimerFactory;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil.POPUP_STYLE;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "JStateView.fxml", isSelfController = true)
public class JStateComposite extends BorderPane implements Closeable, IExcelScreenHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(JStateComposite.class);

	@FXML
	private ComboBox<SnapShotType> comSnapShotType;
	@FXML
	private TableView<JstatModel> tbPrevStat;
	@FXML
	private TextArea txtCurrentStat;
	@FXML
	private LineChart<String, Number> lcStat;

	private int pid = -1;

	/**
	 * 
	 */
	public JStateComposite(int pid) {
		this.pid = pid;
		FxUtil.loadRoot(JStateComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {

		comSnapShotType.getItems().addAll(SnapShotType.values());
		tbPrevStat.getSelectionModel().setCellSelectionEnabled(true);
		tbPrevStat.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		FxUtil.installCommonsTableView(JstatModel.class, tbPrevStat, new BaseOptions() {

			@Override
			public int columnSize(String columnName) {
				if ("content".equals(columnName)) {
					return 800;
				}
				return super.columnSize(columnName);
			}

			@Override
			public StringConverter<Object> stringConverter(String columnName) {
				if ("dateTime".equals(columnName)) {
					return new StringConverter<Object>() {

						@Override
						public String toString(Object object) {
							if (object instanceof Long) {
								return DateUtil.getDateString((Long) object);
							}
							return "";
						}

						@Override
						public Object fromString(String string) {

							return null;
						}
					};
				}
				return super.stringConverter(columnName);
			}

		});

		FxUtil.installDoubleClickPopup(POPUP_STYLE.POPUP.POP_OVER, tbPrevStat);
		FxUtil.installClipboardKeyEvent(tbPrevStat);
		//tableView ContextMenu
		{
			ContextMenu contextMenu = new ContextMenu();//tbPrevStat.getContextMenu();
			//export 
			{
				Menu meExport = new Menu("Export");
				contextMenu.getItems().add(meExport);

				//excel export
				{
					MenuItem miExportExcel = FxUtil.createMenuItemExcelExport(tbPrevStat);
					meExport.getItems().add(miExportExcel);
				}

			}

			tbPrevStat.setContextMenu(contextMenu);
		}

		comSnapShotType.setCellFactory(TextFieldListCell.forListView(new StringConverter<SnapShotType>() {

			@Override
			public String toString(SnapShotType object) {
				if (object == null)
					return "";
				return object.name();
			}

			@Override
			public SnapShotType fromString(String string) {
				return SnapShotType.valueOf(string);
			}
		}));

		NumberAxis yAxis = (NumberAxis) lcStat.getYAxis();
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0d);
		yAxis.setUpperBound(100d);

	}

	@FxPostInitialize
	public void postInit() {
		//default value
		comSnapShotType.getSelectionModel().select(0);
		run();
	}

	private Map<String, Series<String, Number>> cacheChartData = new HashMap<>();

	public void run() {

		if (pid == -1) {
			DialogUtil.showMessageDialog("Invalide Process Id. ");
			return;
		}

		SnapShotType type = comSnapShotType.getSelectionModel().getSelectedItem();
		if (type != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			SnapShotListener listener = new SnapShotListener() {

				@Override
				public void onListen(JSONObject object) {

					if (type != SnapShotType.gcutil) {
						return;
					}

					Set<Map.Entry<String, Object>> entrySet = object.entrySet();

					String date = object.get("date").toString();
					for (Map.Entry<String, Object> ent : entrySet) {
						String key = ent.getKey();

						if (key.endsWith("CT") || key.endsWith("GC"))
							continue;
						if ("date".equals(key))
							continue;

						Object value = ent.getValue();
						double parseDouble = Double.parseDouble(value.toString());

						//데이터갯수가 많아지면 비워줌.
						if (!lcStat.getData().isEmpty() && lcStat.getData().get(0).getData().size() > 60) {
							ObservableList<Series<String, Number>> data = lcStat.getData();
							for (Series<String, Number> s : data) {
								s.getData().remove(0);
							}
						}

						if (cacheChartData.containsKey(key)) {

							Series<String, Number> series = cacheChartData.get(key);
							series.getData().add(new Data<>(date, parseDouble));
						} else {
							Series<String, Number> series = new Series<>(key,
									FXCollections.observableList(new LinkedList<Data<String, Number>>()));
							series.getData().add(new Data<>(date, parseDouble));
							lcStat.getData().add(series);

							cacheChartData.put(key, series);
						}
					}

				}
			};

			Monitors.logSnapShot(type, this.pid, 1, out, listener);
			String content = out.toString();
			txtCurrentStat.setText(content);

			JstatModel newItem = new JstatModel();
			long currentTimeMillis = System.currentTimeMillis();
			newItem.setDateTime(currentTimeMillis);
			newItem.setContent(content);
			ObservableList<JstatModel> items = this.tbPrevStat.getItems();
			items.add(newItem);
			this.tbPrevStat.scrollTo(items.size());

		}

	}

	//	Series<String, Number> series = new Series<>("", FXCollections.observableList(new LinkedList<Data<String, Number>>()));

	private Timer newInsance;
	private AtomicBoolean wasScheduled = new AtomicBoolean();

	@FXML
	public void btnRunOnAction() {
		cancel();

		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				if (wasScheduled.get()) {
					Platform.runLater(() -> {
						JStateComposite.this.run();
					});
				}

			}
		};
		newInsance = DemonTimerFactory.newInsance("Jstat - Monitor");
		wasScheduled.set(true);
		newInsance.scheduleAtFixedRate(task, 0, 1000);

	}

	private boolean wasSchedule() {
		return wasScheduled.get();
	}

	private void cancel() {
		if (wasSchedule()) {
			newInsance.cancel();
			wasScheduled.set(false);
			newInsance = null;
		}
	}

	@FXML
	public void btnStopOnAction() {
		cancel();
	}

	public static class JstatModel extends AbstractDVO {
		private LongProperty dateTime = new SimpleLongProperty();
		private StringProperty content = new SimpleStringProperty();

		public final StringProperty contentProperty() {
			return this.content;
		}

		public final java.lang.String getContent() {
			return this.contentProperty().get();
		}

		public final void setContent(final java.lang.String content) {
			this.contentProperty().set(content);
		}

		public final LongProperty dateTimeProperty() {
			return this.dateTime;
		}

		public final long getDateTime() {
			return this.dateTimeProperty().get();
		}

		public final void setDateTime(final long dateTime) {
			this.dateTimeProperty().set(dateTime);
		}

	}

	@Override
	public void close() throws IOException {
		cancel();
	}

	private static final String FILE_OVERWIRTE_MESSAGE = "파일이 이미 존재합니다. 덮어씌우시겠습니까? ";

}
