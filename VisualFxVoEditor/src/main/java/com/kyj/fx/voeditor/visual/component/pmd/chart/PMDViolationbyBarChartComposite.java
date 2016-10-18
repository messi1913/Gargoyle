/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import javafx.scene.Node;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
public class PMDViolationbyBarChartComposite extends AbstractPMDViolationBarChartComposite {

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#createNode()
	 */
	@Override
	public Node createNode() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#violationAdapter()
	 */
	@Override
	public PMDVioationAdapter violationAdapter() {
		return new PMDVioationAdapter() {

			@Override
			public void ruleViolationAdded(RuleViolation ruleViolation) {

			}

		};
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#build()
	 */
	@Override
	public void build() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.pmd.chart.PMDViolationChartVisualable#clean()
	 */
	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}

}
