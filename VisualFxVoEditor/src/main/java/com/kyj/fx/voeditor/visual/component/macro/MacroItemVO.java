/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MacroItemVO {

	private StringProperty id;
	private StringProperty displayText;
	private StringProperty content;
	private MACRO_ITEM_TYPE type = MACRO_ITEM_TYPE.UNKNOWN;

	private ObservableList<MacroItemVO> childrens;

	public MacroItemVO() {
		this.id = new SimpleStringProperty("");
		this.displayText = new SimpleStringProperty("");
		this.content = new SimpleStringProperty("");
	}

	public final StringProperty idProperty() {
		return this.id;
	}

	public final String getId() {
		return this.idProperty().get();
	}

	public final void setId(final String id) {
		this.idProperty().set(id);
	}

	public final StringProperty displayTextProperty() {
		return this.displayText;
	}

	public final String getDisplayText() {
		return this.displayTextProperty().get();
	}

	public final void setDisplayText(final String displayText) {
		this.displayTextProperty().set(displayText);
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

	public ObservableList<MacroItemVO> getChildrens() {
		return childrens;
	}

	public void setChildrens(ObservableList<MacroItemVO> childrens) {
		this.childrens = childrens;
	}

	public MACRO_ITEM_TYPE getType() {
		return type;
	}

	public void setType(MACRO_ITEM_TYPE type) {
		this.type = type;
	}

}
