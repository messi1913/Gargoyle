/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.pmd.chart
 *	작성일   : 2016. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.pmd.chart;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import net.sourceforge.pmd.RuleViolation;

/**
 * RuleViolation클래스에서 fileName 기준으로
 * hashcode를 오버라이드하여 사용하기 위해 선언
 * @author KYJ
 *
 */
public class ReuleViolationWrapper {

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
