/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart.service
 *	작성일   : 2016. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;

import com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.XYChart.Data;

/**
 * @author KYJ
 *
 */
public class BaseGoogleTrendAdapter implements IGargoyleChartAdapter<JSONObject, String> {

	private ObjectProperty<JSONObject> json = new SimpleObjectProperty<>();
	private StringProperty version = new SimpleStringProperty();;
	private ObjectProperty<Map<String, Object>> table = new SimpleObjectProperty<>();
	private ObjectProperty<List<Map<String, String>>> cols = new SimpleObjectProperty<>();
	private ObjectProperty<List<Map<String, List<Map<String, String>>>>> rows = new SimpleObjectProperty<>();

	public BaseGoogleTrendAdapter(ObjectProperty<JSONObject> jsonProperty) {
		JSONObject json = jsonProperty.get();
		if (ValueUtil.isNotEmpty(json)) {
			this.version.set(json.get("version").toString());
			table.set((Map<String, Object>) json.get("table"));
			cols.set((List<Map<String, String>>) json.get("cols"));
			rows.set((List<Map<String, List<Map<String, String>>>>) json.get("rows"));
		}

		this.json.bind(jsonProperty);//set(json);
		this.json.addListener((oba, o, n) -> {

			if (ValueUtil.isNotEmpty(n)) {
				this.version.set(n.get("version").toString());
				Map<String, Object> _table = (Map<String, Object>) n.get("table");
				table.set(_table);

				if (_table.isEmpty()) {
					cols.set(Collections.emptyList());
					rows.set(Collections.emptyList());
				} else {
					cols.set((List<Map<String, String>>) _table.get("cols"));
					rows.set((List<Map<String, List<Map<String, String>>>>) _table.get("rows"));
				}

			}

		});

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
	public String getColumnName(int seq) {
		String findKey = "query" + seq;
		Optional<String> findFirst = cols.get().stream().map(v -> {
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
	public int getColumnCount() {
		return (int) cols.get().stream().filter(v -> {
			String string = v.get("id");
			return string.startsWith("query");
		}).count();

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getValue(java.lang.Object, java.lang.String, int)
	 */
	@Override
	public Data<String, Number> getValue(int columnIndex, String columnName, int row) {
		Map<String, List<Map<String, String>>> map = this.rows.get().get(row);
		List<Map<String, String>> list = map.get("c");
		Map<String, String> dateInfo = list.get(0);

		Map<String, String> query = list.get(columnIndex + 1);
		if (query == null)
			return null;

		String colName = dateInfo.get("f");
		String value = query.get("f");
		if (colName == null || value == null)
			return null;
		Integer valueOf = Integer.valueOf(value, 10);
		return new Data<>(colName, valueOf);
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.adapter.IGargoyleChartAdapter#getValueCount(java.lang.Object, java.lang.String)
	 */
	@Override
	public int getValueCount(String columnName) {
		return this.rows.get().size();
	}

}
