/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : PeriodBoxPane
 *	작성일   : 2016. 11. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.date;

import java.time.LocalDate;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/**
 * 기간 관련 데이터를 입력처리하기 위한 Composite
 * @author KYJ
 *
 */
@FXMLController(value = "PeriodBoxView.fxml", isSelfController = true)
public class PeriodBoxComposite extends VBox {

	private static final Logger LOGGER = LoggerFactory.getLogger(PeriodBoxComposite.class);

	@FXML
	private VBox vboxRoot;

	@FXML
	private JFXDatePicker dpStartTime, dpEndTime;

	@FXML
	private JFXDatePicker dpStartDate, dpEndDate, dpStartDay, dpEndDay;

	/**
	 *
	 * @최초생성일 2016. 12. 16.
	 */
	@FXML
	private JFXButton btnCustomAdd;

	@FXML
	private Label lblTime;

	private StringConverter<LocalDate> eeeStringConverter = new StringConverter<LocalDate>() {

		@Override
		public String toString(LocalDate date) {
			return DateUtil.Fx.toString(date, DateUtil.Fx.EEE);
		}

		@Override
		public LocalDate fromString(String eee) {
			return DateUtil.Fx.toDate(eee, DateUtil.Fx.EEE);
		}
	};

	public PeriodBoxComposite() {
		FxUtil.loadRoot(PeriodBoxComposite.class, this, e -> LOGGER.error(ValueUtil.toString(e)));
	}

	@FXML
	public void initialize() {
		dpStartDay.setConverter(eeeStringConverter);
		dpEndDay.setConverter(eeeStringConverter);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @return
	 */
	public LocalDate getStartDay() {
		return getStartDay(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @return
	 */
	public LocalDate getEndDay() {
		return getEndDay(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 19.
	 * @return
	 */
	public LocalDate getStartDate() {
		return getStartDate(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 19.
	 * @return
	 */
	public LocalDate getStartTime() {
		return getStartTime(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 19.
	 * @return
	 */
	public LocalDate getEndDate() {
		return getEndDate(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 19.
	 * @return
	 */
	public LocalDate getEndTime() {
		return getEndTime(ld -> ld);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getStartDay(Function<LocalDate, T> conf) {
		return conf.apply(dpStartDay.getValue());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getEndDay(Function<LocalDate, T> conf) {
		return conf.apply(dpEndDay.getValue());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getStartDate(Function<LocalDate, T> conf) {
		return conf.apply(dpStartDate.getValue());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getEndDate(Function<LocalDate, T> conf) {
		return conf.apply(dpEndDate.getValue());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getStartTime(Function<LocalDate, T> conf) {
		return conf.apply(dpStartTime.getValue());
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 16.
	 * @param conf
	 * @return
	 */
	public <T> T getEndTime(Function<LocalDate, T> conf) {
		return conf.apply(dpEndTime.getValue());
	}

}
