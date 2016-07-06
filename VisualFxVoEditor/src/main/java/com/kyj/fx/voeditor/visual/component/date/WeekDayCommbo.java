/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.component.date
 *	작성일   : 2016. 4. 18.
 *	프로젝트 : PASS 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

/**
 * 요일을 표현해주기 위한 콤보박스.
 *
 * @author KYJ
 *
 */
public class WeekDayCommbo extends ChoiceBox<Integer> {

	public WeekDayCommbo() {
		// 디폴트 현재 시간.
		this(getCurrentDayOfWeek());
	}

	public WeekDayCommbo(int dayofWeek) {

		// 콤보박스에 기본적으로 입력할 요일데이터를 입력함.
		List<Integer> collect = Stream.iterate(1, i -> i + 1).limit(7).collect(Collectors.toList());
		collect.forEach(v -> getItems().add(v));
		setConverter(weekDayConverter);
		setValue(dayofWeek);
	}

	/**
	 * 현재 시간을 리턴,
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 18.
	 * @return
	 */
	private static final int getCurrentDayOfWeek() {
		// 디폴트 현재 시간.
		Calendar instance = Calendar.getInstance();
		instance.setTime(new Date());
		return instance.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 요일을 표시하기 위한 포멧터
	 *
	 * @최초생성일 2016. 4. 18.
	 */
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
	private static final StringConverter<Integer> weekDayConverter = new StringConverter<Integer>() {

		@Override
		public String toString(Integer v) {

			if (!(Calendar.SUNDAY <= v && v <= Calendar.SATURDAY)) {
				throw new IllegalArgumentException("weekOfDay is not valide... : " + v);
			}

			Calendar instance = Calendar.getInstance();
			instance.set(Calendar.DAY_OF_WEEK, v);
			return instance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
		}

		@Override
		public Integer fromString(String string) {

			Calendar instance = Calendar.getInstance();
			if (ValueUtil.isEmpty(string))
				return instance.get(Calendar.DAY_OF_WEEK);

			try {
				Date parse = simpleDateFormat.parse(string);
				instance.setTime(parse);
				return instance.get(Calendar.DAY_OF_WEEK);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	};
}
