/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MacroItemVO {

	private StringProperty parentMacroId;
	private StringProperty macroId;
	private StringProperty macroName;
	private StringProperty content;

	/**
	 * 
	 * U : Unknown , F : File, D : Dir
	 * 
	 * @최초생성일 2016. 9. 4.
	 */
	private StringProperty macroItemType;

	private ObservableList<MacroItemVO> childrens;

	public MacroItemVO() {
		this.parentMacroId = new SimpleStringProperty();
		this.macroId = new SimpleStringProperty("");
		this.macroName = new SimpleStringProperty("");
		this.content = new SimpleStringProperty("");
		this.macroItemType = new SimpleStringProperty("U");
		this.childrens = FXCollections.observableArrayList();
	}

	public final StringProperty parentMacroIdProperty() {
		return this.parentMacroId;
	}

	public final String getParentMacroId() {
		return this.parentMacroIdProperty().get();
	}

	public final void setParentMacroId(final String parentMacroId) {
		this.parentMacroIdProperty().set(parentMacroId);
	}

	public final StringProperty macroIdProperty() {
		return this.macroId;
	}

	public final String getMacroId() {
		return this.macroIdProperty().get();
	}

	public final void setMacroId(final String macroId) {
		this.macroIdProperty().set(macroId);
	}

	public final StringProperty macroNameProperty() {
		return this.macroName;
	}

	public final String getMacroName() {
		return this.macroNameProperty().get();
	}

	public final void setMacroName(final String macroName) {
		this.macroNameProperty().set(macroName);
	}

	public final StringProperty contentProperty() {
		return this.content;
	}

	public final String getContent() {
		return this.contentProperty().get();
	}

	public final void setContent(final String content) {
		this.contentProperty().set(content);
	}

	public final StringProperty macroItemTypeProperty() {
		return this.macroItemType;
	}

	public final String getMacroItemType() {
		return this.macroItemTypeProperty().get();
	}

	public final void setMacroItemType(final String macroItemType) {
		this.macroItemTypeProperty().set(macroItemType);
	}

	@Override
	public String toString() {
		return macroId.get();
	}

}
