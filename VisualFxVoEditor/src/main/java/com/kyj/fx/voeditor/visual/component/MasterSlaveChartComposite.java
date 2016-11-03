/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "MasterSlaveChartView.fxml", isSelfController = true)
public abstract class MasterSlaveChartComposite extends BorderPane {

	@FXML
	private BarChart<String, Long> barChartDayOfMonth;

	@FXML
	private LineChart<String, Long> lineChartDayOfWeek;

	@FXML
	private CategoryAxis barChartDayOfMonthCategory, lineChartDayOfWeekCategory;

	@FXML
	private NumberAxis barChartDayOfMonthNumber, lineChartDayOfWeekNumber;

	public MasterSlaveChartComposite() throws Exception {
		FxUtil.loadRoot(MasterSlaveChartComposite.class, this);
	}

	@FXML
	public void initialize() {
		init();
	}

	public abstract void init();

	/**
	 * @return the barChartDayOfMonth
	 */
	public final BarChart<String, Long> getBarChartDayOfMonth() {
		return barChartDayOfMonth;
	}

	/**
	 * @return the lineChartDayOfWeek
	 */
	public final LineChart<String, Long> getLineChartDayOfWeek() {
		return lineChartDayOfWeek;
	}

	/**
	 * @return the barChartDayOfMonthCategory
	 */
	public final CategoryAxis getBarChartDayOfMonthCategory() {
		return barChartDayOfMonthCategory;
	}

	/**
	 * @return the lineChartDayOfWeekCategory
	 */
	public final CategoryAxis getLineChartDayOfWeekCategory() {
		return lineChartDayOfWeekCategory;
	}

	/**
	 * @return the barChartDayOfMonthNumber
	 */
	public final NumberAxis getBarChartDayOfMonthNumber() {
		return barChartDayOfMonthNumber;
	}

	/**
	 * @return the lineChartDayOfWeekNumber
	 */
	public final NumberAxis getLineChartDayOfWeekNumber() {
		return lineChartDayOfWeekNumber;
	}

}
