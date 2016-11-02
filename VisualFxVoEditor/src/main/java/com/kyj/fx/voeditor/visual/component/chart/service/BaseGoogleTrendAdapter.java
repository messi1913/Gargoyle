/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart.service
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;

import com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter;

import javafx.scene.chart.XYChart.Data;

/**
 * @author KYJ
 *
 */
public class BaseGoogleTrendAdapter implements IGargoyleChartAdapter<JSONObject, String> {

	private JSONObject json;
	private String version;
	private Map<String, Object> table;
	private List<Map<String, String>> cols;
	private List<Map<String, List<Map<String, String>>>> rows;

	public BaseGoogleTrendAdapter(JSONObject json) {
		this.json = json;
		this.version = this.json.get("version").toString();
		table = (Map<String, Object>) this.json.get("table");
		cols = (List<Map<String, String>>) this.table.get("cols");
		rows = (List<Map<String, List<Map<String, String>>>>) this.table.get("rows");
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getTitle(java.lang.Object)
	 */
	@Override
	public String getTitle(JSONObject t) {
		return "Google Trend Chart";
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getColumnName(java.lang.Object, int)
	 */
	@Override
	public String getColumnName(JSONObject t, int seq) {
		String findKey = "query" + seq;
		Optional<String> findFirst = cols.stream().map(v -> {
			if (findKey.equals(v.get("id")))
				return v.get("label");
			return null;
		}).filter(v -> v != null).findFirst();
		if (findFirst.isPresent())
			return findFirst.get();
		return "";
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getColumnCount(java.lang.Object)
	 */
	@Override
	public int getColumnCount(JSONObject t) {
		return (int) cols.stream().filter(v -> {
			String string = v.get("id");
			return string.startsWith("query0");
		}).count();

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getValue(java.lang.Object, java.lang.String, int)
	 */
	@Override
	public Data<String, Number> getValue(JSONObject t, int columnIndex, String columnName, int row) {
		Map<String, List<Map<String, String>>> map = this.rows.get(row);
		List<Map<String, String>> list = map.get("c");
		Map<String, String> dateInfo = list.get(0);


		Map<String, String> query = list.get(columnIndex + 1);
		if(query == null)
			return null;
		
		String colName = dateInfo.get("f");
		String value = query.get("f");
		if(colName == null || value == null )
			return null;
		Integer valueOf = Integer.valueOf(value,10);
		return new Data<>(colName, valueOf);
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getValueCount(java.lang.Object, java.lang.String)
	 */
	@Override
	public int getValueCount(JSONObject t, String columnName) {
		return this.rows.size();
	}

}
