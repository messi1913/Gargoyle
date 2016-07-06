/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.layout
 *	작성일   : 2016. 3. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.layout;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * @author KYJ
 *
 */
public class GroupPane extends Control {

	private ObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();

	private StringProperty title = new SimpleStringProperty("Group");

	public GroupPane() {

	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected Skin<GroupPane> createDefaultSkin() {
		return new GroupPaneSkin(this);
	}

	public void setContent(Node node) {
		contentProperty.set(node);
	}

	public Node getContent() {
		return contentProperty.get();
	}

	public ObjectProperty<Node> contentProperty() {
		return contentProperty;
	}

	public final ObjectProperty<Node> contentPropertyProperty() {
		return this.contentProperty;
	}

	public final javafx.scene.Node getContentProperty() {
		return this.contentPropertyProperty().get();
	}

	public final void setContentProperty(final javafx.scene.Node contentProperty) {
		this.contentPropertyProperty().set(contentProperty);
	}

	public final StringProperty titlePropert() {
		return this.title;
	}

	public final java.lang.String getTitle() {
		return this.titlePropert().get();
	}

	public final void setTitle(final java.lang.String titlePropert) {
		this.titlePropert().set(titlePropert);
	}

}
