/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.capture;

import java.util.List;

import javafx.scene.control.Skin;
import javafx.scene.paint.Color;

/**
 * @author KYJ
 *
 */
public class TableDrawItem extends DrawItem {

	private String title;
	private List<String> items;

	public TableDrawItem(String title, List<String> items) {
		super();
		this.title = title;
		this.items = items;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TableDrawItemSkin(this);
	}

	public String getTitle() {
		return title;
	}

	public List<String> getItems() {
		return items;
	}

	@Override
	public void requestBackgroundColorChange(Color newColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestTextFillChange(Color newColor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestBorderColorChange(Color newColor) {
		// TODO Auto-generated method stub

	}
}
