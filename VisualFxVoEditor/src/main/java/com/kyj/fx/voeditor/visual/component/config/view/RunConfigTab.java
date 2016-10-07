/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.view
 *	작성일   : 2016. 10. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.view;

import com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem;

import javafx.scene.control.Tab;

/**
 * @author KYJ
 *
 */
public class RunConfigTab extends Tab {

	private IRunableItem item;

	public RunConfigTab(IRunableItem item) {
		this.item = item;
	}
}
