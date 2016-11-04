/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart.service
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import org.json.simple.JSONObject;

import com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

/**
 * @author KYJ
 *
 */
public class BaseGoogleTrendChart extends AbstractGoogleTrendChart<JSONObject, String> {

	public BaseGoogleTrendChart() {
		super();
	}
	/**
	 * @param source
	 */
	public BaseGoogleTrendChart(String source) {
		super(source);
	}

	public BaseGoogleTrendChart(CategoryAxis x, NumberAxis y) {
		super("", x, y);
	}

	public BaseGoogleTrendChart(String source, CategoryAxis x, NumberAxis y) {
		super(source, x, y);
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.chart.service.AbstractGoogleTrendChart#convert(java.lang.String)
	 */
	@Override
	public JSONObject convert(String source) {
		return ValueUtil.toJSONObject(source);
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.chart.service.AbstractGoogleTrendChart#adapter()
	 */
	@Override
	public IGargoyleChartAdapter<JSONObject, String> adapter() {
		return new BaseGoogleTrendAdapter(jsonProperty());
	}

}
