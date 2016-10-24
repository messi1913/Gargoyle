/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd.chart
 *	작성일   : 2016. 10. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 * @author KYJ
 *
 */
public class PMDViolationChartComposite extends AbstractPMDViolationChartComposite {

	private ObjectProperty<VIEW_TYPE> selectedViewType = new SimpleObjectProperty<PMDViolationChartComposite.VIEW_TYPE>(
			VIEW_TYPE.GROUP_BY_FILE);

	private ObjectProperty<AbstractPMDViolationChartComposite> viewComposite = new SimpleObjectProperty<AbstractPMDViolationChartComposite>();

	private ComboBox<VIEW_TYPE> comboViewType;

	private PMDCheckedListComposite master;

	/**
	 * 차트를 표현할 유형을 선택
	 * @author KYJ
	 *
	 */
	public enum VIEW_TYPE {
		GROUP_BY_FILE("File"), GROUP_BY_VIOLATION("Vioations");

		String name;

		VIEW_TYPE(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}

	/**
	 * 생성자
	 */
	public PMDViolationChartComposite(PMDCheckedListComposite master) {
		comboViewType = new ComboBox<>(FXCollections.observableArrayList(VIEW_TYPE.values()));
		this.setTop(comboViewType);
		this.setCenter(createNode());
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {

		switch (this.selectedViewType.get()) {

		case GROUP_BY_FILE:
			viewComposite.set(new PMDViolationFilebyLineChartComposite(master));
			break;

		case GROUP_BY_VIOLATION:
			viewComposite.set(new PMDViolationbyPieChartComposite(master));
			break;
		}
		return viewComposite.get();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#violationAdapter()
	 */
	@Override
	public PMDVioationAdapter violationAdapter() {
		return viewComposite.get().violationAdapter();
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#build()
	 */
	@Override
	public void build() {
		viewComposite.get().build();

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#clean()
	 */
	@Override
	public void clean() {
		viewComposite.get().clean();
	}

}
