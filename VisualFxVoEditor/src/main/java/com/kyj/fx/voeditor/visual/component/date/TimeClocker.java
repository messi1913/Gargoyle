/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.date
 *	작성일   : 2016. 4. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.date;

import javafx.scene.control.Control;

/**
 * Note that this clock does not keep perfect time, but is close. It's main
 * purpose is to demonstrate various features of JavaFX.
 */
public class TimeClocker extends Control {

	/**
	 * @inheritDoc
	 */
	@Override
	protected ClockNaSkin createDefaultSkin() {
		return new ClockNaSkin(this);
	}

	public String getTimeString() {
		return getClockSkin().getText();
	}

	public ClockNaSkin getClockSkin() {
		return (ClockNaSkin) getSkin();
	}

}
