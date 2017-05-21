/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.utube
 *	작성일   : 2017. 5. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.utube;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class UtubeItemDVO {

	private StringProperty url;
	private BooleanProperty check;

	public UtubeItemDVO(String url) {
		this.url = new SimpleStringProperty(url);
		this.check = new SimpleBooleanProperty();
	}

	public final StringProperty urlProperty() {
		return this.url;
	}

	public final String getUrl() {
		return this.urlProperty().get();
	}

	public final void setUrl(final String url) {
		this.urlProperty().set(url);
	}

	public final BooleanProperty checkProperty() {
		return this.check;
	}

	public final boolean isCheck() {
		return this.checkProperty().get();
	}

	public final void setCheck(final boolean check) {
		this.checkProperty().set(check);
	}

}
