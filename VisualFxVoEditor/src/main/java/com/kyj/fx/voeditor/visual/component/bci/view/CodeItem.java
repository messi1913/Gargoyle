/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bci.view
 *	작성일   : 2016. 5. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.io.File;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class CodeItem {

	private ObjectProperty<File> file;

	private StringProperty name;

	private StringProperty desc;

	private BooleanProperty selection;

	public CodeItem() {
		file = new SimpleObjectProperty<>();
		name = new SimpleStringProperty();
		desc = new SimpleStringProperty();
		selection = new SimpleBooleanProperty();
	}

	public ObjectProperty<File> fileProperty() {
		return this.file;
	}

	public File getFile() {
		return this.fileProperty().get();
	}

	public void setFile(final File file) {
		this.fileProperty().set(file);
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(name);
	}

	public StringProperty descProperty() {
		return this.desc;
	}

	public String getDesc() {
		return this.descProperty().get();
	}

	public void setDesc(final String desc) {
		this.descProperty().set(desc);
	}

	public BooleanProperty selectionProperty() {
		return this.selection;
	}

	public boolean isSelection() {
		return this.selectionProperty().get();
	}

	public void setSelection(final boolean selection) {
		this.selectionProperty().set(selection);
	}

}
