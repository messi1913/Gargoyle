/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 2. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import javafx.scene.Node;

/**
 * @author KYJ
 *
 */
public interface SupplySkin<N extends Node> {

	public N supplyNode();
}
