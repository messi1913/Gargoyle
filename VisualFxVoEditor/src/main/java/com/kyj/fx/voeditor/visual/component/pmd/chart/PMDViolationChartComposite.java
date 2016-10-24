/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd.chart
 *	작성일   : 2016. 10. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.component.pmd.PMDCheckedListComposite;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.shape.Rectangle;
import jfxtras.scene.layout.HBox;
import net.sourceforge.pmd.ReportListener;

/**
 * @author KYJ
 *
 */
public class PMDViolationChartComposite extends AbstractPMDViolationChartComposite {

	private ObjectProperty<VIEW_TYPE> selectedViewType = new SimpleObjectProperty<PMDViolationChartComposite.VIEW_TYPE>(
			VIEW_TYPE.GROUP_BY_FILE_LINE);

	private ObjectProperty<AbstractPMDViolationChartComposite> viewComposite = new SimpleObjectProperty<AbstractPMDViolationChartComposite>();

	private ComboBox<VIEW_TYPE> comboViewType;

	private PMDCheckedListComposite master;

	private ObservableList<AbstractPMDViolationChartComposite> avalilableCompositeList;

	/**
	 * 차트를 표현할 유형을 선택
	 * @author KYJ
	 *
	 */
	public enum VIEW_TYPE {
		GROUP_BY_FILE_LINE("[Line] File - Violation"), GROUP_BY_FILE_PIE("[Pie] File - Violation"), GROUP_BY_VIOLATION("[Bar] Vioations");

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

		avalilableCompositeList = FXCollections.observableArrayList(new PMDViolationFilebyLineChartComposite(master),
				new PMDViolationbyPieChartComposite(master), new PMDViolationbyBarChartComposite(master));

		comboViewType = new ComboBox<>(FXCollections.observableArrayList(VIEW_TYPE.values()));
		//default value
		comboViewType.setValue(selectedViewType.get());

		comboViewType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				int intValue = newValue.intValue();

				VIEW_TYPE newType = VIEW_TYPE.values()[intValue];
				selectedViewType.set(newType);

				PMDViolationChartComposite.this.setCenter(createNode());
				build();
			}

		});

		HBox hBox = new HBox(5, comboViewType);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		this.setTop(hBox);
		this.setCenter(createNode());
	}

	/**
	 * 일기 전용 리스트 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 24.
	 * @return
	 */
	public List<ReportListener> getAvalilableReportListenerList() {
		return avalilableCompositeList.stream().map(v -> v.getReportListener()).collect(Collectors.toList());

	}

	public ReportListener[] getAvalilableReportListenerArray() {
		Stream<ReportListener> map = this.avalilableCompositeList.stream().map(v -> v.getReportListener());
		ReportListener[] array = map.toArray(ReportListener[]::new);
		return array;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {

		int ordinal = this.selectedViewType.get().ordinal();
		if (ordinal == -1)
			return new Rectangle();

		viewComposite.set(avalilableCompositeList.get(ordinal));
		return viewComposite.get();

		//		switch (this.selectedViewType.get()) {
		//
		//		case GROUP_BY_FILE_LINE:
		//
		//			viewComposite.set(new PMDViolationFilebyLineChartComposite(master));
		//			break;
		//
		//		case GROUP_BY_FILE_PIE:
		//			viewComposite.set(new PMDViolationbyPieChartComposite(master));
		//			break;
		//
		//		case GROUP_BY_VIOLATION:
		//			viewComposite.set(new PMDViolationbyBarChartComposite(master));
		//			break;
		//		}

		//		return viewComposite.get();
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
