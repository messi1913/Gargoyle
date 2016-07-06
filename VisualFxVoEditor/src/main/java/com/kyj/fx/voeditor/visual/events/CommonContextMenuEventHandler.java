/**
 * KYJ
 * 2015. 10. 14.
 */
package com.kyj.fx.voeditor.visual.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

/**
 * @author KYJ
 *
 */
public class CommonContextMenuEventHandler implements EventHandler<CommonContextMenuEvent> {

	public CommonContextMenuEventHandler() {
		super();
	}

	/**
	 * The only valid EventType for the ActionEvent.
	 */
	public static final EventType<CommonContextMenuEvent> CommonContextMenuEvent = new EventType<CommonContextMenuEvent>(Event.ANY,
			"CommonContextMenuEvent");

	@Override
	public void handle(CommonContextMenuEvent event) {
		Event.fireEvent(event.getNode(), event);
	}

}
