/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 11. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.model.vo;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author KYJ
 *
 */
public class ConfigurationTreeItem {
	private StringProperty itemName = new SimpleStringProperty();
	private ConfigurationTreeItem parent;
	private List<ConfigurationTreeItem> childrens;

	public ConfigurationTreeItem() {
		super();
	}

	public ConfigurationTreeItem(List<ConfigurationTreeItem> childrens) {
		super();
		this.childrens = childrens;
	}

	public ConfigurationTreeItem(ConfigurationTreeItem children) {
		super();
		childrens = Arrays.asList(children);
	}

	/**
	 * @return the parent
	 */
	public ConfigurationTreeItem getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(ConfigurationTreeItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the childrens
	 */
	public List<ConfigurationTreeItem> getChildrens() {
		return childrens;
	}

	/**
	 * @param childrens
	 *            the childrens to set
	 */
	public void setChildrens(List<ConfigurationTreeItem> childrens) {
		this.childrens = childrens;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return itemName.get();
	}

	public final StringProperty itemNameProperty() {
		return this.itemName;
	}

	public final java.lang.String getItemName() {
		return this.itemNameProperty().get();
	}

	public final void setItemName(final java.lang.String itemName) {
		this.itemNameProperty().set(itemName);
	}

}
