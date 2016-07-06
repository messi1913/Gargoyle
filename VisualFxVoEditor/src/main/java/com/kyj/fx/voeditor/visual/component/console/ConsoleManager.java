/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.console
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

/**
 * @author KYJ
 *
 */
public class ConsoleManager {

	private BaseConsole baseConsole;

	protected ConsoleManager(BaseConsole baseConsole) {
		this.baseConsole = baseConsole;

	}

	/**
	 * @return the baseConsole
	 */
	public BaseConsole getBaseConsole() {
		return baseConsole;
	}

	/**
	 * @param baseConsole
	 *            the baseConsole to set
	 */
	public void setBaseConsole(BaseConsole baseConsole) {
		this.baseConsole = baseConsole;
	}

	public String appendText(String text, boolean appendLine) {
		if (appendLine) {
			return text.concat(System.lineSeparator());
		}
		return text;
	}

	public String appendText(String text) {
		return appendText(text, true);
	}

}
