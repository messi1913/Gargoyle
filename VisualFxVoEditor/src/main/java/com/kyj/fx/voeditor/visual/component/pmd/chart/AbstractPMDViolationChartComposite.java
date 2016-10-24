/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.stat.Metric;

/**
 * Bar차트기반 PMD Violation을 차트로 표현하기 위한 추상 클래스
 * @author KYJ
 *
 */
public abstract class AbstractPMDViolationChartComposite extends BorderPane implements PMDViolationChartVisualable {

	private Predicate<RuleViolation> ruleViolationFilter;

	/**
	 * @param layout
	 */
	public AbstractPMDViolationChartComposite() {
		this(null);
	}

	/**
	 * @param initLayout
	 * 		초기 BorderPane 레이아웃에 배치할 영역이 따로 존재하는경우 구현처리함.
	 */
	public AbstractPMDViolationChartComposite(BiConsumer<BorderPane, Node> initLayout) {
		ruleViolationFilter = ruleViolationFilter();

		Platform.runLater(() -> {
			if (initLayout != null) {
				initLayout.accept(this, createNode());
			} else
				setCenter(createNode());
		});

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#getReportListener()
	 */
	@Override
	public final ReportListener getReportListener() {
		return new ReportListener() {

			@Override
			public void ruleViolationAdded(RuleViolation arg0) {
				if (ruleViolationFilter.test(arg0))
					violationAdapter().ruleViolationAdded(arg0);
			}

			@Override
			public void metricAdded(Metric arg0) {
				//Nothing.
			}
		};
	}

}
