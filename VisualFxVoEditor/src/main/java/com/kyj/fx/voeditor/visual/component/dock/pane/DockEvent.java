/**
 * @file DockEvent.java
 * @brief Class implementing a generic base for docking events.
 *
 * @section License
 *
 *          This file is a part of the DockFX Library. Copyright (C) 2015 Robert B. Colton
 *
 *          This program is free software: you can redistribute it and/or modify it under the terms
 *          of the GNU Lesser General Public License as published by the Free Software Foundation,
 *          either version 3 of the License, or (at your option) any later version.
 *
 *          This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *          WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *          PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 *          You should have received a copy of the GNU Lesser General Public License along with this
 *          program. If not, see <http://www.gnu.org/licenses/>.
 **/

package com.kyj.fx.voeditor.visual.component.dock.pane;

import com.sun.javafx.scene.input.InputEventUtils;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.PickResult;

/**
 * Base class for DockFX events. Each DockFX event has associated an event source, event target and
 * an event type. The event source specifies for an event handler the object on which that handler
 * has been registered and which sent the event to it. The event target defines the path through
 * which the event will travel when posted. The event type provides additional classification to
 * events of the same {@code DockEvent} class. Like a {@link MouseEvent} the event will be
 * associated with an x and y coordinate local to the node as well as to the screen.
 * 
 * @since DockFX 0.1
 */
public class DockEvent extends Event {
  /**
   * Generated Serial Version UID
   */
  private static final long serialVersionUID = 4413700316447127355L;

  /**
   * Common supertype for all dock event types.
   */
  public static final EventType<DockEvent> ANY = new EventType<DockEvent>(Event.ANY, "DOCK");

  /**
   * This event occurs when a dock window is being dragged by its title bar and the mouse enters a
   * node's bounds. Unlike a {@code DragEvent} the dock over event is handed to all stages that may
   * be interested in receiving the dock pane.
   */
  public static final EventType<DockEvent> DOCK_ENTER =
      new EventType<DockEvent>(DockEvent.ANY, "DOCK_ENTER");

  /**
   * This event occurs when a dock window is being dragged by its title bar and the mouse is
   * contained in a node's bounds. Unlike a {@code DragEvent} the dock over event is handed to all
   * stages that may be interested in receiving the dock pane.
   */
  public static final EventType<DockEvent> DOCK_OVER =
      new EventType<DockEvent>(DockEvent.ANY, "DOCK_OVER");

  /**
   * This event occurs when a dock window is being dragged by its title bar and the mouse exits a
   * node's bounds. Unlike a {@code DragEvent} the dock over event is handed to all stages that may
   * be interested in receiving the dock pane.
   */
  public static final EventType<DockEvent> DOCK_EXIT =
      new EventType<DockEvent>(DockEvent.ANY, "DOCK_EXIT");

  /**
   * This event occurs when a dock window is being dragged by its title bar and the mouse is
   * released over a node's bounds. Unlike a {@code DragEvent} the dock over event is handed to all
   * stages that may be interested in receiving the dock pane.
   */
  public static final EventType<DockEvent> DOCK_RELEASED =
      new EventType<DockEvent>(DockEvent.ANY, "DOCK_RELEASED");

  /**
   * Horizontal x position of the event relative to the origin of the DockEvent's node.
   */
  private transient double x;

  /**
   * Horizontal position of the event relative to the origin of the DockEvent's source.
   * 
   * @return horizontal position of the event relative to the origin of the DockEvent's source.
   */
  public final double getX() {
    return x;
  }

  /**
   * Vertical y position of the event relative to the origin of the DockEvent's node.
   */
  private transient double y;

  /**
   * Vertical position of the event relative to the origin of the DockEvent's source.
   * 
   * @return vertical position of the event relative to the origin of the DockEvent's source.
   */
  public final double getY() {
    return y;
  }

  /**
   * Depth z position of the event relative to the origin of the DockEvent's node.
   */
  private transient double z;

  /**
   * Depth position of the event relative to the origin of the DockEvent's source.
   *
   * @return depth position of the event relative to the origin of the DockEvent's source.
   */
  public final double getZ() {
    return z;
  }

  /**
   * Absolute horizontal x position of the event.
   */
  private final double screenX;

  /**
   * Returns absolute horizontal position of the event.
   * 
   * @return absolute horizontal position of the event
   */
  public final double getScreenX() {
    return screenX;
  }

  /**
   * Absolute vertical y position of the event.
   */
  private final double screenY;

  /**
   * Returns absolute vertical position of the event.
   * 
   * @return absolute vertical position of the event
   */
  public final double getScreenY() {
    return screenY;
  }

  /**
   * Horizontal x position of the event relative to the origin of the {@code Scene} that contains
   * the DockEvent's node. If the node is not in a {@code Scene}, then the value is relative to the
   * boundsInParent of the root-most parent of the DockEvent's node.
   */
  private final double sceneX;

  /**
   * Returns horizontal position of the event relative to the origin of the {@code Scene} that
   * contains the DockEvent's source. If the node is not in a {@code Scene}, then the value is
   * relative to the boundsInParent of the root-most parent of the DockEvent's node. Note that in 3D
   * scene, this represents the flat coordinates after applying the projection transformations.
   * 
   * @return horizontal position of the event relative to the origin of the {@code Scene} that
   *         contains the DockEvent's source
   */
  public final double getSceneX() {
    return sceneX;
  }

  /**
   * Vertical y position of the event relative to the origin of the {@code Scene} that contains the
   * DockEvent's node. If the node is not in a {@code Scene}, then the value is relative to the
   * boundsInParent of the root-most parent of the DockEvent's node.
   */
  private final double sceneY;

  /**
   * Returns vertical position of the event relative to the origin of the {@code Scene} that
   * contains the DockEvent's source. If the node is not in a {@code Scene}, then the value is
   * relative to the boundsInParent of the root-most parent of the DockEvent's node. Note that in 3D
   * scene, this represents the flat coordinates after applying the projection transformations.
   * 
   * @return vertical position of the event relative to the origin of the {@code Scene} that
   *         contains the DockEvent's source
   */
  public final double getSceneY() {
    return sceneY;
  }

  /**
   * Information about the pick if the picked {@code Node} is a {@code Shape3D} node and its
   * pickOnBounds is false.
   */
  private PickResult pickResult;

  /**
   * Returns information about the pick.
   * 
   * @return new PickResult object that contains information about the pick
   */
  public final PickResult getPickResult() {
    return pickResult;
  }

  /**
   * Information about the pick if the picked {@code Node} is a {@code Shape3D} node and its
   * pickOnBounds is false.
   */
  private Node contents;

  /**
   * Returns the contents of the dock event, similar to the dragboard.
   * 
   * @return Node that is currently being dragged during this event
   */
  public final Node getContents() {
    return contents;
  }

  /**
   * Constructs new DockEvent event..
   * 
   * @param eventType The type of the event.
   * @param x The x with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param y The y with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param screenX The x coordinate relative to screen.
   * @param screenY The y coordinate relative to screen.
   * @param pickResult pick result. Can be null, in this case a 2D pick result without any further
   *        values is constructed based on the scene coordinates
   */
  public DockEvent(EventType<? extends DockEvent> eventType, double x, double y, double screenX,
      double screenY, PickResult pickResult) {
    this(null, null, eventType, x, y, screenX, screenY, pickResult);
  }

  /**
   * Constructs new DockEvent event..
   * 
   * @param source the source of the event. Can be null.
   * @param target the target of the event. Can be null.
   * @param eventType The type of the event.
   * @param x The x with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param y The y with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param screenX The x coordinate relative to screen.
   * @param screenY The y coordinate relative to screen.
   * @param pickResult pick result. Can be null, in this case a 2D pick result without any further
   *        values is constructed based on the scene coordinates
   */
  public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType,
      double x, double y, double screenX, double screenY, PickResult pickResult) {
    this(source, target, eventType, x, y, screenX, screenY, pickResult, null);
  }

  /**
   * Constructs new DockEvent event..
   * 
   * @param source the source of the event. Can be null.
   * @param target the target of the event. Can be null.
   * @param eventType The type of the event.
   * @param x The x with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param y The y with respect to the source. Should be in scene coordinates if source == null or
   *        source is not a Node.
   * @param screenX The x coordinate relative to screen.
   * @param screenY The y coordinate relative to screen.
   * @param pickResult pick result. Can be null, in this case a 2D pick result without any further
   *        values is constructed based on the scene coordinates
   * @param contents The contents being dragged during this event.
   */
  public DockEvent(Object source, EventTarget target, EventType<? extends DockEvent> eventType,
      double x, double y, double screenX, double screenY, PickResult pickResult, Node contents) {
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
    this.contents = contents;
  }

}
