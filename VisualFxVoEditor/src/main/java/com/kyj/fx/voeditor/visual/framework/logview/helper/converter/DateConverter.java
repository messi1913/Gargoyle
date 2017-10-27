/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.logview.helper.converter
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import com.kyj.fx.voeditor.visual.framework.logview.helper.core.LogViewHelper.MatchItem;

/**
 * @author KYJ
 *
 */
public class DateConverter extends AbstractConverter<String> {

	/**
	 */
	public DateConverter() {
	}

	public DateConverter(String regex, String fromPattern, String toPattern) {
		super(regex, fromPattern, toPattern);
	}

	public DateConverter(String fromPattern, String toPattern) {
		super(fromPattern, toPattern);
	}

	public DateConverter(String regex) {
		super(regex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.framework.logview.helper.converter.Converter#
	 * convert(java.lang.String)
	 */
	@Override
	public String convert(String data) {

		SimpleDateFormat fromFormat = new SimpleDateFormat(getFromPattern());
		SimpleDateFormat toFormat = new SimpleDateFormat(getToPattern());
		String format = null;
		try {
			Date parse = fromFormat.parse(data);
			format = toFormat.format(parse);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return format;
	}

	@Override
	public Function<MatchItem<String>, MatchItem<String>> getChanger() {
		return null;
	}

}
