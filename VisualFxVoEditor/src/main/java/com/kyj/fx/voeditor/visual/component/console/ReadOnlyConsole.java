/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

/**
 *
 * use ReadOnlySingletonConsole
 *
 * @author KYJ
 *
 */
public class ReadOnlyConsole extends BaseConsole {

	/**
	 * use ReadOnlySingletonConsole
	 */
	ReadOnlyConsole() {
		super();
		setEditable(false);
	}

}
