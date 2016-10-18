/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd.chart
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import net.sourceforge.pmd.RuleViolation;

/**
 * @author KYJ
 *
 */
@FunctionalInterface
public interface PMDVioationAdapter {

	/**
	 * 리스너를 실제 PMD에 구현하고 리스너에서 호출되는 RuleVioation을 넘겨주는 처리를 하기 위한 메소드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @param ruleViolation
	 */
	public abstract void ruleViolationAdded(RuleViolation ruleViolation);

}
