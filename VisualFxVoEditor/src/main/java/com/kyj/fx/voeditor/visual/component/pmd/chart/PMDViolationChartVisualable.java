/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import java.util.function.Predicate;

import javafx.scene.Node;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
public interface PMDViolationChartVisualable {

	/**
	 * UI상의 메인 뷰가 되는 Node를 구현
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @return
	 */
	public Node createNode();

	/**
	 * PMD를 실행하는 실제 로직에 인자로 넘겨줄 ReportListener 구현.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @return
	 */
	public ReportListener getReportListener();

	/**
	 * PMD violation 대리자
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @return
	 */
	public PMDVioationAdapter violationAdapter();

	/**
	 * adapter로 넘길 대상인지 여부를 판단하는 역할 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @return
	 */
	public default Predicate<RuleViolation> ruleViolationFilter() {
		return r -> true;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 */
	public void build();

	/**
	 * 데이터 항목들을 청소함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 */
	public void clean();

}
