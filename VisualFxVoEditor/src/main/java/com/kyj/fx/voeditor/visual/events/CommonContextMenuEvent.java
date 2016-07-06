/**
 * KYJ
 * 2015. 10. 14.
 */
package com.kyj.fx.voeditor.visual.events;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

/**
 * @author KYJ
 *
 */
public class CommonContextMenuEvent extends ActionEvent {

	private static final long serialVersionUID = 20121107L;

	private Node node;

	private MenuItem menuItem;

	private int mode;

	public CommonContextMenuEvent(Node node, MenuItem menuItem, int mode) {
		super();
		this.node = node;
		this.menuItem = menuItem;
		this.mode = mode;
	}

	/**
	 * KYJ
	 * 
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * KYJ
	 * 
	 * @param node
	 *            the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * KYJ
	 * 
	 * @return the menuItem
	 */
	public MenuItem getMenuItem() {
		return menuItem;
	}

	/**
	 * KYJ
	 * 
	 * @param menuItem
	 *            the menuItem to set
	 */
	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	/**
	 * KYJ
	 * 
	 * @return the mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * KYJ
	 * 
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

}