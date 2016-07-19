/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleException;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "ScmChagnedCodeView.fxml", isSelfController = true)
public class SvnChagnedCodeComposite extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(SvnChagnedCodeComposite.class);

	private FxSVNHistoryDataSupplier supplier;
	@FXML
	private PieChart piChartChagendCode;

	private List<Data> dataList;

	public SvnChagnedCodeComposite(FxSVNHistoryDataSupplier supplier)
			throws SVNException, NullPointerException, GagoyleException, IOException {
		this.supplier = supplier;
		FxUtil.loadRoot(SvnChagnedCodeComposite.class, this);

	}

	@FXML
	public void initialize() {

		Collection<SVNLogEntry> allLogs = supplier.getAllLogs();
		LOGGER.debug("Log Count : {}", allLogs.size());
		String dateString = supplier.getStart().toDateString();
		String dateString2 = supplier.getEnd().toDateString();

		piChartChagendCode
				.setTitle(String.format("Chaned Code Count ( Rank %d )\n%s ~ %s", supplier.getRankSize(), dateString, dateString2));

		//		collectedTable = supplier.createStream(allLogs).collect(Collectors.groupingBy(v -> v.getPath()));
		Map<String, Long> collect = supplier.createStream(allLogs)
				.collect(Collectors.groupingBy(v -> v.getPath(), LinkedHashMap::new, Collectors.counting()));

		dataList = collect.entrySet().stream().sorted((o1, o2) -> {
			return -Long.compare(o1.getValue(), o2.getValue());
		}).map(v -> new Data(v.getKey(), v.getValue())).limit(supplier.getRankSize()).collect(Collectors.toList());

		piChartChagendCode.getData().addAll(dataList);

		Set<Node> lookupAll = piChartChagendCode.lookupAll(".chart-pie-label");

		for (Data d : piChartChagendCode.getData()) {
			Tooltip.install(d.getNode(),
					new Tooltip(String.format("Source Code : %s\nModify Count:%d", d.getName(), (int) d.getPieValue())));

			d.getNode().setOnMouseClicked(e -> {
				dataOnMouseClick(e, d);
			});

		}

		lookupAll.stream().map(v -> (Text) v).forEach(v -> {

			String text = v.getText();
			int count = supplier.getCollectedTable().get(text).size();
			int textLength = text.length();
			if (textLength > 15) {
				int lastIndexOf = text.lastIndexOf("/");
				text = text.substring(lastIndexOf, textLength);
			}

			text = text.concat("   [").concat(String.valueOf(count)).concat("]");
			v.setText(text);
		});
	}

	public void dataOnMouseClick(MouseEvent e, Data d) {

		if (e.getClickCount() == 1 && e.getButton() == MouseButton.PRIMARY) {

			//			Long long1 = collectedTable.get(d.getName());

			ObservableList<GargoyleSVNLogEntryPath> list = FXCollections.observableArrayList(supplier.getCollectedTable().get(d.getName()));
			if (!list.isEmpty()) {
				GargoyleSVNLogEntryPath gargoyleSVNLogEntryPath = list.get(0);

				BorderPane borderPane = new BorderPane();
				borderPane.setTop(new Label(gargoyleSVNLogEntryPath.getPath()));
				borderPane.setCenter(supplier.createHistoryListView(list));
				FxUtil.showPopOver(d.getNode(), borderPane);
			}

		}
	}

}
