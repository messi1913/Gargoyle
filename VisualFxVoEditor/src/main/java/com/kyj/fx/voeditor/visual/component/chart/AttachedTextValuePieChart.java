/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart
 *	작성일   : 2016. 10. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart;

import java.util.Optional;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * javafx에서 제공되지않는 기능에 대한 처리를 도와주는 PieChart
 * @author KYJ
 *
 */
public class AttachedTextValuePieChart extends PieChart {

	/**
	 * textlabel 텍스쳐 처리
	 * @최초생성일 2016. 10. 4.
	 */
	private StringConverter<Data> labelConverter;
	/**
	 * 그래프에서 툴팁 처리
	 * @최초생성일 2016. 10. 4.
	 */
	private StringConverter<Data> tooltipConverter;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param labelConverter
	 */
	public void setLabelConverter(StringConverter<Data> labelConverter) {
		this.labelConverter = labelConverter;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param tooltipConverter
	 */
	public void setTooltipConverter(StringConverter<Data> tooltipConverter) {
		this.tooltipConverter = tooltipConverter;
	}

	/* (non-Javadoc)
	 * @see javafx.scene.chart.PieChart#layoutChartChildren(double, double, double, double)
	 */
	@Override
	protected void layoutChartChildren(double top, double left, double contentWidth, double contentHeight) {
		if (getLabelsVisible()) {
			getData().forEach(d -> {

				if (tooltipConverter != null) {
					d.getNode().lookupAll(".chart-pie").stream().forEach(v -> {
						FxUtil.installTooltip(v, tooltipConverter.toString(d));
					});
				}

				if (labelConverter != null) {
					Optional<Node> opTextNode = this.lookupAll(".chart-pie-label").stream()
							.filter(n -> n instanceof Text && ((Text) n).getText().contains(d.getName())).findAny();
					if (opTextNode.isPresent()) {
						Text text = (Text) opTextNode.get();
						text.setText(labelConverter.toString(d));
					}
				}
			});

		}
		super.layoutChartChildren(top, left, contentWidth, contentHeight);

	}

}
