/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.chart.service
 *	작성일   : 2016. 11. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.chart.service;

import java.util.List;

import com.sun.javafx.scene.input.InputEventUtils;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;

/**
 * 구글 차트 이벤트
 * @author KYJ
 *
 */
public class GoogleTrendChartEvent extends Event {

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private static final long serialVersionUID = -7214155560305073792L;

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	public static final EventType<GoogleTrendChartEvent> ANY = new EventType<GoogleTrendChartEvent>(Event.ANY, "GOOGLE_CHART");

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	public static final EventType<GoogleTrendChartEvent> GOOGLE_CHART_INTERSECT_NODE_CLICK = new EventType<GoogleTrendChartEvent>(
			GoogleTrendChartEvent.ANY, "GOOGLE_CHART_INTERSECT_NODE_CLICK");

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private transient double x;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getX() {
		return x;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private transient double y;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getY() {
		return y;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private transient double z;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getZ() {
		return z;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private final double screenX;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getScreenX() {
		return screenX;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private final double screenY;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getScreenY() {
		return screenY;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private final double sceneX;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getSceneX() {
		return sceneX;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private final double sceneY;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final double getSceneY() {
		return sceneY;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private PickResult pickResult;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final PickResult getPickResult() {
		return pickResult;
	}

	/**
	 * @최초생성일 2016. 11. 4.
	 */
	private List<Node> contents;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final List<Node> getContents() {
		return contents;
	}

	/**
	 * Mouse로 클릭된 수.
	 * @최초생성일 2016. 11. 4.
	 */
	private int clickCount;

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 4.
	 * @return
	 */
	public final int getClickCount() {
		return clickCount;
	}

	/**
	 * @param eventType
	 * @param x
	 * @param y
	 * @param screenX
	 * @param screenY
	 * @param pickResult
	 */
	public GoogleTrendChartEvent(EventType<? extends GoogleTrendChartEvent> eventType, double x, double y, double screenX, double screenY,
			PickResult pickResult) {
		this(null, null, eventType, x, y, screenX, screenY, pickResult);
	}

	/**
	 * @param source
	 * @param target
	 * @param eventType
	 * @param x
	 * @param y
	 * @param screenX
	 * @param screenY
	 * @param pickResult
	 */
	public GoogleTrendChartEvent(Object source, EventTarget target, EventType<? extends GoogleTrendChartEvent> eventType, double x,
			double y, double screenX, double screenY, PickResult pickResult) {
		this(source, target, eventType, x, y, screenX, screenY, pickResult, 0, null);
	}

	/**
	 * @param source
	 * @param target
	 * @param eventType
	 * @param x
	 * @param y
	 * @param screenX
	 * @param screenY
	 * @param pickResult
	 * @param clickCount
	 * @param contents
	 */
	public GoogleTrendChartEvent(Object source, EventTarget target, EventType<? extends GoogleTrendChartEvent> eventType, double x,
			double y, double screenX, double screenY, PickResult pickResult, int clickCount, List<Node> contents) {
		super(source, target, eventType);
		this.x = x;
		this.y = y;
		this.screenX = screenX;
		this.screenY = screenY;
		this.sceneX = x;
		this.sceneY = y;
		this.pickResult = pickResult != null ? pickResult : new PickResult(target, x, y);
		final Point3D p = InputEventUtils.recomputeCoordinates(this.pickResult, null);
		this.x = p.getX();
		this.y = p.getY();
		this.z = p.getZ();
		this.clickCount = clickCount;
		this.contents = contents;
	}

}
