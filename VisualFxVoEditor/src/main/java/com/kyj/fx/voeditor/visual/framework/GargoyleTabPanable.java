/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2017. 9. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import com.kyj.fx.voeditor.visual.component.dock.tab.DockTab;
import com.kyj.fx.voeditor.visual.component.dock.tab.DockTabPane;

/**
 * Gargoyle 메인Frame에서 호출된 TabPane이면 구현할 수 있도록 한다.
 * 
 * @author KYJ
 *
 */
public interface GargoyleTabPanable {

	public void setTabPane(DockTabPane tabpane);

	public void setTab(DockTab tab);
}
