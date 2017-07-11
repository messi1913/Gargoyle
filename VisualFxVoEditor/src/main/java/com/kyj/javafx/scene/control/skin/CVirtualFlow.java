/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.sun.javafx.scene.control.skin
 *	작성일   : 2017. 7. 6.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.javafx.scene.control.skin;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.javafx.scene.control.skin.VirtualScrollBar;

import javafx.scene.control.IndexedCell;

/**
 * @author KYJ
 *
 */
public class CVirtualFlow<T extends IndexedCell> extends VirtualFlow<T> {

	@SuppressWarnings("restriction")
	public VirtualScrollBar getVerticalbar() {
		return getVbar();
	}

	@SuppressWarnings("restriction")
	public VirtualScrollBar getHorizontalBar() {
		return getHbar();
	}
}
