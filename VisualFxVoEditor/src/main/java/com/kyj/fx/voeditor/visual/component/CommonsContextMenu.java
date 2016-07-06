/**
 * KYJ
 * 2015. 10. 14.
 */
package com.kyj.fx.voeditor.visual.component;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;

import com.kyj.fx.voeditor.visual.events.CommonContextMenuEvent;

/**
 * @author KYJ
 *
 */
public class CommonsContextMenu {

	private CommonsContextMenu() {

	}

	/**
	 * cud 메뉴 생성
	 *
	 * @Date 2015. 10. 14.
	 * @param mode
	 * @return
	 * @User KYJ
	 */
	public static void addMenu(Control node) {
		addMenus(node, Menus.useCudButtons());
	}

	/**
	 * 컨텍스트 메뉴 추가.
	 *
	 * @Date 2015. 10. 14.
	 * @param tableView
	 * @param mode
	 *            Menus 클래스 참고
	 * @return
	 * @User KYJ
	 */
	public static void addMenus(Control node, int mode) {
		ContextMenu contextMenu = new ContextMenu();

		if (Menus.isAdd(mode)) {
			contextMenu.getItems().add(addMenuEvent(node));
		}

		if (Menus.isDelete(mode)) {
			contextMenu.getItems().add(deleteMenuEvent(node));
		}

		if (Menus.isUp(mode)) {
			contextMenu.getItems().add(upMenuEvent(node));
		}

		if (Menus.isDown(mode)) {
			contextMenu.getItems().add(downMenuEvent(node));
		}

		node.setContextMenu(contextMenu);
	}

	/**
	 * 컨텍스트 메뉴 추가.
	 * 
	 * @param node
	 * @param items
	 */
	public static void addMenus(Control node, MenuItem... items) {
		ContextMenu contextMenu = new ContextMenu();

		if (items != null) {
			for (MenuItem item : items) {
				contextMenu.getItems().add(item);
			}
		}

		node.setContextMenu(contextMenu);
	}

	/**
	 * @Date 2015. 10. 14.
	 * @param tableView
	 * @return
	 * @User KYJ
	 */
	private static MenuItem addMenuEvent(Control node) {
		MenuItem menuItem = new MenuItem("추가");
		menuItem.addEventHandler(ActionEvent.ACTION, event -> {
			CommonContextMenuEvent addMenuEvent = new CommonContextMenuEvent(node, menuItem, Menus.ADD);
			node.fireEvent(addMenuEvent);
		});
		return menuItem;
	}

	/**
	 * @Date 2015. 10. 14.
	 * @param tableView
	 * @return
	 * @User KYJ
	 */
	private static MenuItem deleteMenuEvent(Control node) {
		MenuItem menuItem = new MenuItem("삭제");
		menuItem.addEventHandler(ActionEvent.ACTION, event -> {
			CommonContextMenuEvent addMenuEvent = new CommonContextMenuEvent(node, menuItem, Menus.DELETE);
			node.fireEvent(addMenuEvent);
		});
		return menuItem;
	}

	/**
	 * 위로
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 15.
	 * @param node
	 * @return
	 */
	private static MenuItem upMenuEvent(Control node) {
		MenuItem menuItem = new MenuItem("위");
		menuItem.addEventHandler(ActionEvent.ACTION, event -> {
			CommonContextMenuEvent addMenuEvent = new CommonContextMenuEvent(node, menuItem, Menus.UP);
			node.fireEvent(addMenuEvent);
		});
		return menuItem;
	}

	/**
	 * 아래로
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 15.
	 * @param node
	 * @return
	 */
	private static MenuItem downMenuEvent(Control node) {
		MenuItem menuItem = new MenuItem("아래");
		menuItem.addEventHandler(ActionEvent.ACTION, event -> {
			CommonContextMenuEvent addMenuEvent = new CommonContextMenuEvent(node, menuItem, Menus.DOWN);
			node.fireEvent(addMenuEvent);
		});
		return menuItem;
	}

}
