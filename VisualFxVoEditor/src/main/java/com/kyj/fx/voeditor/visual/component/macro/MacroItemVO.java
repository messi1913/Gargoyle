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

	private StringProperty id;
	private StringProperty name;
	private StringProperty content;

	/**
	 * 
	 * U : Unknown , F : File, D : Dir
	 * 
	 * @최초생성일 2016. 9. 4.
	 */
	private StringProperty type;

	private ObservableList<MacroItemVO> childrens;

	public MacroItemVO() {
		this.id = new SimpleStringProperty("");
		this.name = new SimpleStringProperty("");
		this.content = new SimpleStringProperty("");
		this.type = new SimpleStringProperty("U");
		this.childrens = FXCollections.observableArrayList();
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

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
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

	public final StringProperty typeProperty() {
		return this.type;
	}

	public final String getType() {
		return this.typeProperty().get();
	}

	public final void setType(final String type) {
		this.typeProperty().set(type);
	}

	@Override
	public String toString() {
		return id.get();
	}

}
