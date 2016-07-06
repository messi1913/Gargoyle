/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.component.console
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

/**
 * 콘솔뷰.
 * 
 * 시스템당 한개만 생성가능.
 * 
 * @author KYJ
 *
 */
public class ReadOnlySingletonConsole {

	private static ReadOnlyConsole console;

	public static ReadOnlyConsole getInstance() {
		if (console == null) {
			console = new ReadOnlyConsole();
		}
		return console;
	}
}
