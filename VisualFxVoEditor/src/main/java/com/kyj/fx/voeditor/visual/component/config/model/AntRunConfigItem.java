/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.model
 *	작성일   : 2017. 3. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * AntRunConfigView 화면에서 사용하는
 * 아이템 정보를 표현하기 위해 사용.
 *
 * @author KYJ
 *
 */
public class AntRunConfigItem {

	private BooleanProperty chk;
	private StringProperty targetName;
	private StringProperty targetDesc;

	public AntRunConfigItem(String targetName, String targetDesc) {
		this.targetName = new SimpleStringProperty(targetName);
		this.targetDesc = new SimpleStringProperty(targetDesc);
		this.chk = new SimpleBooleanProperty();
	}

	public final StringProperty targetNameProperty() {
		return this.targetName;
	}

	public final java.lang.String getTargetName() {
		return this.targetNameProperty().get();
	}

	public final void setTargetName(final java.lang.String targetName) {
		this.targetNameProperty().set(targetName);
	}

	public final StringProperty targetDescProperty() {
		return this.targetDesc;
	}

	public final java.lang.String getTargetDesc() {
		return this.targetDescProperty().get();
	}

	public final void setTargetDesc(final java.lang.String targetDesc) {
		this.targetDescProperty().set(targetDesc);
	}

	public final BooleanProperty chkProperty() {
		return this.chk;
	}

	public final boolean isChk() {
		return this.chkProperty().get();
	}

	public final void setChk(final boolean chk) {
		this.chkProperty().set(chk);
	}

}
