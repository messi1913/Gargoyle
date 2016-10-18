/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
public class PMDViolationFilebyBarChartComposite extends AbstractPMDViolationBarChartComposite {

	/**
	 *  ObservableMap에 집계된 데이터를 기반으로 차트로 표현하기 위한 dataList
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableList<Data<String, Number>> dataList;

	/**
	 * 중복되는 데이터를 제거하기위한 데이터셋
	 * @최초생성일 2016. 10. 18.
	 */
	private ObservableMap<ReuleViolationWrapper, Integer> observableHashMap;

	/**
	 * RangeChart를 표현해주는 뷰
	 * @최초생성일 2016. 10. 18.
	 */
	private RangeSliderLineChartComposite<Data<String, Number>> sliderLineChartComposite = null;

	public PMDViolationFilebyBarChartComposite() {
		observableHashMap = FXCollections.observableHashMap();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {

		try {
			sliderLineChartComposite = new RangeSliderLineChartComposite<Data<String, Number>>() {
				@Override
				public Data<String, Number> converter(Data<String, Number> t) {
					return t;
				}

				/* (non-Javadoc)
				 * @see com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite#createNewSeries(javafx.collections.ObservableList)
				 */
				@Override
				public Series<String, Number> createNewSeries(String seriesName, ObservableList<Data<String, Number>> subList) {
					return super.createNewSeries("File Names", subList);
				}

				/* (non-Javadoc)
				 * @see com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite#createNumberAxis()
				 */
				@Override
				protected NumberAxis createNumberAxis() {

					NumberAxis createNumberAxis = super.createNumberAxis();
					createNumberAxis.setLabel("Violation Count");
					return createNumberAxis;
				}

			};
			dataList = sliderLineChartComposite.getItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sliderLineChartComposite;
	}

	PMDVioationAdapter pmdVioationAdapter = ruleViolation -> {
		ReuleViolationWrapper wrapper = new ReuleViolationWrapper(ruleViolation);

		if (observableHashMap.containsKey(wrapper)) {
			Integer integer = observableHashMap.get(wrapper);
			observableHashMap.put(wrapper, Integer.sum(integer.intValue(), 1));
		} else {
			observableHashMap.put(wrapper, Integer.valueOf(1));
		}
	};

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#violationAdapter()
	 */
	@Override
	public PMDVioationAdapter violationAdapter() {
		return pmdVioationAdapter;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#build()
	 */
	@Override
	public void build() {

		observableHashMap.entrySet().forEach(v -> {
			String name = v.getKey().toString();

			Integer value = v.getValue();
			dataList.add(new Data<String, Number>(name, value));
		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#clean()
	 */
	@Override
	public void clean() {
		observableHashMap.clear();
		dataList.clear();
	}

	/**
	 * RuleViolation클래스에서 fileName 기준으로
	 * hashcode를 오버라이드하여 사용하기 위해 선언
	 * @author KYJ
	 *
	 */
	class ReuleViolationWrapper {

		private RuleViolation ruleViolation;

		public ReuleViolationWrapper(RuleViolation ruleViolation) {
			this.ruleViolation = ruleViolation;
		}

		/**
		 * @return the ruleViolation
		 */
		public final RuleViolation getRuleViolation() {
			return ruleViolation;
		}

		/**
		 * @param ruleViolation the ruleViolation to set
		 */
		public final void setRuleViolation(RuleViolation ruleViolation) {
			this.ruleViolation = ruleViolation;
		}

		public String getFileName() {
			String filename = this.ruleViolation.getFilename();
			String simpleName = ValueUtil.getSimpleFileName(filename);
			return simpleName;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReuleViolationWrapper other = (ReuleViolationWrapper) obj;

			if (ruleViolation == null) {
				if (other.ruleViolation != null)
					return false;
			} else if (!getFileName().equals(getFileName()))
				return false;
			return true;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return getFileName();
		}

	}

}
