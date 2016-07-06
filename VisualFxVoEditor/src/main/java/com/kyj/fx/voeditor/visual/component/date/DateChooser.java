package com.kyj.fx.voeditor.visual.component.date;

import java.util.Date;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 *
 * from https://dzone.com/sites/all/files/DateChooser_styled.zip
 *
 * @author KYJ
 *
 */
public class DateChooser extends Control {

	private static final String DEFAULT_STYLE_CLASS = "date-chooser";
	private Date date;

	public DateChooser(Date preset) {
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);

		 this.getStylesheets().add(DateChooser.class.getResource("calendar.css").toExternalForm());
//		this.getStylesheets().add(DateChooser.class.getResource("lcd.css").toExternalForm());
		this.date = preset;
	}

	public DateChooser() {
		this(new Date());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new DateChooserSkin(this);
	}

	// @Override
	// public String getUserAgentStylesheet() {
	// return "com/kyj/fx/voeditor/visual/component/date/calendar.css";
	// }

	public Date getDate() {
		return date;
	}
}
