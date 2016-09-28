/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.capture
 *	작성일   : 2016. 9. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.capture;

import javafx.geometry.Point2D;

/**
 * @author KYJ
 *
 */
public class Location {

	private Point2D dragAnchor;

	private double initX;

	private double initY;

	public Location() {
		super();
	}

	public Location(double initX, double initY) {
		super();
		this.initX = initX;
		this.initY = initY;
	}

	/**
	 * @return the initX
	 */
	public final double getInitX() {
		return initX;
	}

	/**
	 * @return the initY
	 */
	public final double getInitY() {
		return initY;
	}

	/**
	 * @param initX the initX to set
	 */
	public final void setInitX(double initX) {
		this.initX = initX;
	}

	/**
	 * @param initY the initY to set
	 */
	public final void setInitY(double initY) {
		this.initY = initY;
	}

	/**
	 * @return the dragAnchor
	 */
	public final Point2D getDragAnchor() {
		return dragAnchor;
	}

	/**
	 * @param dragAnchor the dragAnchor to set
	 */
	public final void setDragAnchor(Point2D dragAnchor) {
		this.dragAnchor = dragAnchor;
	}

}
