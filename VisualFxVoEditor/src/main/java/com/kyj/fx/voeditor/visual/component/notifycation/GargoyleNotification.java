/**
 * Copyright (c) 2014, 2016, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.notifycation
 *	작성일   : 2017. 6. 8.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.notifycation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.controlsfx.tools.Utils;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import impl.org.controlsfx.skin.NotificationBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * 
 * copy base code org.controlsfx.control.Notifications
 * 
 * @author KYJ
 *
 */
public class GargoyleNotification {

	/***************************************************************************
	 * * Static fields * *
	 **************************************************************************/

	private static final String STYLE_CLASS_DARK = "dark"; //$NON-NLS-1$

	/***************************************************************************
	 * * Private fields * *
	 **************************************************************************/

	private String title;
	private String text;
	private Node graphic;
	private ObservableList<Action> actions = FXCollections.observableArrayList();
	private Pos position = Pos.BOTTOM_RIGHT;
	private Duration hideAfterDuration = Duration.seconds(5);
	private boolean hideCloseButton;
	private EventHandler<ActionEvent> onAction;
	private Window owner;

	private DoubleProperty prefWidth = new SimpleDoubleProperty(-1d);
	private DoubleProperty prefHeight = new SimpleDoubleProperty(-1d);

	private Screen screen = Screen.getPrimary();

	private List<String> styleClass = new ArrayList<>();

	/***************************************************************************
	 * * Constructors * *
	 **************************************************************************/

	// we do not allow instantiation of the Notifications class directly - users
	// must go via the builder API (that is, calling create())
	private GargoyleNotification() {
		// no-op
	}

	/***************************************************************************
	 * * Public API * *
	 **************************************************************************/

	/**
	 * Call this to begin the process of building a notification to show.
	 */
	public static GargoyleNotification create() {
		return new GargoyleNotification();
	}

	/**
	 *  2017.6.8 kyj
	 *  configuration. popup width and height
	 *   
	 **/
	public DoubleProperty getPrefWidth() {
		return prefWidth;
	}

	public void setPrefWidth(DoubleProperty prefWidth) {
		this.prefWidth = prefWidth;
	}

	public DoubleProperty getPrefHeight() {
		return prefHeight;
	}

	public void setPrefHeight(DoubleProperty prefHeight) {
		this.prefHeight = prefHeight;
	}

	/**
	 * Specify the text to show in the notification.
	 */
	public GargoyleNotification text(String text) {
		this.text = text;
		return this;
	}

	/**
	 * Specify the title to show in the notification.
	 */
	public GargoyleNotification title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Specify the graphic to show in the notification.
	 */
	public GargoyleNotification graphic(Node graphic) {
		this.graphic = graphic;
		return this;
	}

	/**
	 * Specify the position of the notification on screen, by default it is
	 * {@link Pos#BOTTOM_RIGHT bottom-right}.
	 */
	public GargoyleNotification position(Pos position) {
		this.position = position;
		return this;
	}

	/**
	 * The dialog window owner - which can be {@link Screen}, {@link Window}
	 * or {@link Node}. If specified, the notifications will be inside
	 * the owner, otherwise the notifications will be shown within the whole
	 * primary (default) screen.
	 */
	public GargoyleNotification owner(Object owner) {
		if (owner instanceof Screen) {
			this.screen = (Screen) owner;
		} else {
			this.owner = Utils.getWindow(owner);
		}
		return this;
	}

	/**
	 * Specify the duration that the notification should show, after which it
	 * will be hidden.
	 */
	public GargoyleNotification hideAfter(Duration duration) {
		this.hideAfterDuration = duration;
		return this;
	}

	/**
	 * Specify what to do when the user clicks on the notification (in addition
	 * to the notification hiding, which happens whenever the notification is
	 * clicked on).
	 */
	public GargoyleNotification onAction(EventHandler<ActionEvent> onAction) {
		this.onAction = onAction;
		return this;
	}

	/**
	 * Specify that the notification should use the built-in dark styling,
	 * rather than the default 'modena' notification style (which is a
	 * light-gray).
	 */
	public GargoyleNotification darkStyle() {
		styleClass.add(STYLE_CLASS_DARK);
		return this;
	}

	/**
	 * Specify that the close button in the top-right corner of the notification
	 * should not be shown.
	 */
	public GargoyleNotification hideCloseButton() {
		this.hideCloseButton = true;
		return this;
	}

	/**
	 * Specify the actions that should be shown in the notification as buttons.
	 */
	public GargoyleNotification action(Action... actions) {
		this.actions = actions == null ? FXCollections.<Action> observableArrayList() : FXCollections.observableArrayList(actions);
		return this;
	}

	/**
	 * Instructs the notification to be shown.
	 */
	public void show() {
		NotificationPopupHandler.getInstance().show(this);
	}

	/***************************************************************************
	 * * Private support classes * *
	 **************************************************************************/

	// not public so no need for JavaDoc
	private static final class NotificationPopupHandler {

		private static final NotificationPopupHandler INSTANCE = new NotificationPopupHandler();

		private double startX;
		private double startY;
		private double screenWidth;
		private double screenHeight;

		static final NotificationPopupHandler getInstance() {
			return INSTANCE;
		}

		private final Map<Pos, List<Popup>> popupsMap = new HashMap<>();
		private final double padding = 15;

		// for animating in the notifications
		private ParallelTransition parallelTransition = new ParallelTransition();

		private boolean isShowing = false;

		public void show(GargoyleNotification notification) {
			Window window;
			if (notification.owner == null) {
				/*
				 * If the owner is not set, we work with the whole screen.
				 */
				Rectangle2D screenBounds = notification.screen.getVisualBounds();
				startX = screenBounds.getMinX();
				startY = screenBounds.getMinY();
				screenWidth = screenBounds.getWidth();
				screenHeight = screenBounds.getHeight();

				window = Utils.getWindow(null);
			} else {
				/*
				 * If the owner is set, we will make the notifications popup
				 * inside its window.
				 */
				startX = notification.owner.getX();
				startY = notification.owner.getY();
				screenWidth = notification.owner.getWidth();
				screenHeight = notification.owner.getHeight();
				window = notification.owner;
			}
			show(window, notification);
		}

		private void show(Window owner, final GargoyleNotification notification) {
			// Stylesheets which are added to the scene of a popup aren't 
			// considered for styling. For this reason, we need to find the next 
			// window in the hierarchy which isn't a popup.  
			Window ownerWindow = owner;
			while (ownerWindow instanceof PopupWindow) {
				ownerWindow = ((PopupWindow) ownerWindow).getOwnerWindow();
			}
			// need to install our CSS
			Scene ownerScene = ownerWindow.getScene();
			if (ownerScene != null) {
				String stylesheetUrl = Notifications.class.getResource("notificationpopup.css").toExternalForm(); //$NON-NLS-1$
				if (!ownerScene.getStylesheets().contains(stylesheetUrl)) {
					// The stylesheet needs to be added at the beginning so that
					// the styling can be adjusted with custom stylesheets.
					ownerScene.getStylesheets().add(0, stylesheetUrl);
				}
			}

			final Popup popup = new Popup();
			popup.setAutoFix(false);

			final Pos p = notification.position;

			final NotificationBar notificationBar = new NotificationBar() {
				@Override
				public String getTitle() {
					return notification.title;
				}

				@Override
				public String getText() {
					return notification.text;
				}

				@Override
				public Node getGraphic() {
					return notification.graphic;
				}

				@Override
				public ObservableList<Action> getActions() {
					return notification.actions;
				}

				@Override
				public boolean isShowing() {
					return isShowing;
				}

				@Override
				protected double computeMinWidth(double height) {
					String text = getText();
					Node graphic = getGraphic();

					if (notification.prefWidth.get() != -1)
						return notification.prefWidth.get();

					if ((text == null || text.isEmpty()) && (graphic != null)) {
						return graphic.minWidth(height);
					}
					return 400;
				}

				@Override
				protected double computeMinHeight(double width) {
					String text = getText();
					Node graphic = getGraphic();

					if (notification.prefHeight.get() != -1)
						return notification.prefHeight.get();

					if (ValueUtil.isNotEmpty(graphic)) {
						return graphic.minHeight(width);
					}

					if ((text == null || text.isEmpty()) && (graphic != null)) {
						return graphic.minHeight(width);
					}
					return 100;
				}

				@Override
				public boolean isShowFromTop() {
					return NotificationPopupHandler.this.isShowFromTop(notification.position);
				}

				@Override
				public void hide() {
					isShowing = false;

					// this would slide the notification bar out of view,
					// but I prefer the fade out below
					// doHide();

					// animate out the popup by fading it
					createHideTimeline(popup, this, p, Duration.ZERO).play();
				}

				@Override
				public boolean isCloseButtonVisible() {
					return !notification.hideCloseButton;
				}

				@Override
				public double getContainerHeight() {
					return startY + screenHeight;
				}

				@Override
				public void relocateInParent(double x, double y) {
					// this allows for us to slide the notification upwards
					switch (p) {
					case BOTTOM_LEFT:
					case BOTTOM_CENTER:
					case BOTTOM_RIGHT:
						popup.setAnchorY(y - padding);
						break;
					default:
						// no-op
						break;
					}
				}
			};

			notificationBar.getStyleClass().addAll(notification.styleClass);

			notificationBar.setOnMouseClicked(e -> {
				if (notification.onAction != null) {
					ActionEvent actionEvent = new ActionEvent(notificationBar, notificationBar);
					notification.onAction.handle(actionEvent);

					// animate out the popup
					createHideTimeline(popup, notificationBar, p, Duration.ZERO).play();
				}
			});

			
			notification.prefWidth.unbind();
			notification.prefHeight.unbind();
			notification.prefWidth.bind(notificationBar.prefWidthProperty());
			notification.prefHeight.bind(notificationBar.prefHeightProperty());
			
			popup.getContent().add(notificationBar);
			popup.show(owner, 0, 0);

			// determine location for the popup
			double anchorX = 0, anchorY = 0;
			final double barWidth = notificationBar.getWidth();
			final double barHeight = notificationBar.getHeight();

			// get anchorX
			switch (p) {
			case TOP_LEFT:
			case CENTER_LEFT:
			case BOTTOM_LEFT:
				anchorX = padding + startX;
				break;

			case TOP_CENTER:
			case CENTER:
			case BOTTOM_CENTER:
				anchorX = startX + (screenWidth / 2.0) - barWidth / 2.0 - padding / 2.0;
				break;

			default:
			case TOP_RIGHT:
			case CENTER_RIGHT:
			case BOTTOM_RIGHT:
				anchorX = startX + screenWidth - barWidth - padding;
				break;
			}

			// get anchorY
			switch (p) {
			case TOP_LEFT:
			case TOP_CENTER:
			case TOP_RIGHT:
				anchorY = padding + startY;
				break;

			case CENTER_LEFT:
			case CENTER:
			case CENTER_RIGHT:
				anchorY = startY + (screenHeight / 2.0) - barHeight / 2.0 - padding / 2.0;
				break;

			default:
			case BOTTOM_LEFT:
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
				anchorY = startY + screenHeight - barHeight - padding;
				break;
			}

			popup.setAnchorX(anchorX);
			popup.setAnchorY(anchorY);

			isShowing = true;
			notificationBar.doShow();

			addPopupToMap(p, popup);

			// begin a timeline to get rid of the popup
			Timeline timeline = createHideTimeline(popup, notificationBar, p, notification.hideAfterDuration);
			timeline.play();
		}

		private void hide(Popup popup, Pos p) {
			popup.hide();
			removePopupFromMap(p, popup);
		}

		private Timeline createHideTimeline(final Popup popup, NotificationBar bar, final Pos p, Duration startDelay) {
			KeyValue fadeOutBegin = new KeyValue(bar.opacityProperty(), 1.0);
			KeyValue fadeOutEnd = new KeyValue(bar.opacityProperty(), 0.0);

			KeyFrame kfBegin = new KeyFrame(Duration.ZERO, fadeOutBegin);
			KeyFrame kfEnd = new KeyFrame(Duration.millis(500), fadeOutEnd);

			Timeline timeline = new Timeline(kfBegin, kfEnd);
			timeline.setDelay(startDelay);
			timeline.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					hide(popup, p);
				}
			});

			return timeline;
		}

		private void addPopupToMap(Pos p, Popup popup) {
			List<Popup> popups;
			if (!popupsMap.containsKey(p)) {
				popups = new LinkedList<>();
				popupsMap.put(p, popups);
			} else {
				popups = popupsMap.get(p);
			}

			doAnimation(p, popup);

			// add the popup to the list so it is kept in memory and can be
			// accessed later on
			popups.add(popup);
		}

		private void removePopupFromMap(Pos p, Popup popup) {
			if (popupsMap.containsKey(p)) {
				List<Popup> popups = popupsMap.get(p);
				popups.remove(popup);
			}
		}

		private void doAnimation(Pos p, Popup changedPopup) {
			List<Popup> popups = popupsMap.get(p);
			if (popups == null) {
				return;
			}

			final double newPopupHeight = changedPopup.getContent().get(0).getBoundsInParent().getHeight();

			parallelTransition.stop();
			parallelTransition.getChildren().clear();

			final boolean isShowFromTop = isShowFromTop(p);

			// animate all other popups in the list upwards so that the new one
			// is in the 'new' area.
			// firstly, we need to determine the target positions for all popups
			double sum = 0;
			double targetAnchors[] = new double[popups.size()];
			for (int i = popups.size() - 1; i >= 0; i--) {
				Popup _popup = popups.get(i);

				final double popupHeight = _popup.getContent().get(0).getBoundsInParent().getHeight();

				if (isShowFromTop) {
					if (i == popups.size() - 1) {
						sum = startY + newPopupHeight + padding;
					} else {
						sum += popupHeight;
					}
					targetAnchors[i] = sum;
				} else {
					if (i == popups.size() - 1) {
						sum = changedPopup.getAnchorY() - popupHeight;
					} else {
						sum -= popupHeight;
					}

					targetAnchors[i] = sum;
				}
			}

			// then we set up animations for each popup to animate towards the
			// target
			for (int i = popups.size() - 1; i >= 0; i--) {
				final Popup _popup = popups.get(i);
				final double anchorYTarget = targetAnchors[i];
				if (anchorYTarget < 0) {
					_popup.hide();
				}
				final double oldAnchorY = _popup.getAnchorY();
				final double distance = anchorYTarget - oldAnchorY;

				Transition t = new CustomTransition(_popup, oldAnchorY, distance);
				t.setCycleCount(1);
				parallelTransition.getChildren().add(t);
			}
			parallelTransition.play();
		}

		private boolean isShowFromTop(Pos p) {
			switch (p) {
			case TOP_LEFT:
			case TOP_CENTER:
			case TOP_RIGHT:
				return true;
			default:
				return false;
			}
		}

		class CustomTransition extends Transition {

			private WeakReference<Popup> popupWeakReference;
			private double oldAnchorY;
			private double distance;

			CustomTransition(Popup popup, double oldAnchorY, double distance) {
				popupWeakReference = new WeakReference<>(popup);
				this.oldAnchorY = oldAnchorY;
				this.distance = distance;
				setCycleDuration(Duration.millis(350.0));
			}

			@Override
			protected void interpolate(double frac) {
				Popup popup = popupWeakReference.get();
				if (popup != null) {
					double newAnchorY = oldAnchorY + distance * frac;
					popup.setAnchorY(newAnchorY);
				}
			}

		}
	}
}
