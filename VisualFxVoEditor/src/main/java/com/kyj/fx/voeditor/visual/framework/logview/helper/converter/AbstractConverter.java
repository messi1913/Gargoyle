/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.logview.helper.converter
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.converter;

import java.util.function.Function;

import com.kyj.fx.voeditor.visual.framework.logview.helper.core.LogViewHelper.MatchItem;

/**
 * @author KYJ
 *
 */
public abstract class AbstractConverter<T> implements Converter<T> {

	private String regex;
	private String fromPattern;
	private String toPattern;
	private Function<MatchItem<T>, MatchItem<T>> changer;

	/**
	 */
	public AbstractConverter() {

	}

	public AbstractConverter(String regex) {
		super();
		this.regex = regex;
	}

	public AbstractConverter(String fromPattern, String toPattern) {
		super();
		this.fromPattern = fromPattern;
		this.toPattern = toPattern;
	}

	public AbstractConverter(String regex, String fromPattern, String toPattern) {
		super();
		this.regex = regex;
		this.fromPattern = fromPattern;
		this.toPattern = toPattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.framework.logview.helper.converter.Converter#
	 * getFromPattern()
	 */
	@Override
	public String getFromPattern() {
		return this.fromPattern;
	}

	@Override
	public void setFromPattern(String pattern) {
		this.fromPattern = pattern;
	}

	@Override
	public String getToPattern() {
		return this.toPattern;
	}

	@Override
	public void setToPattern(String pattern) {
		this.toPattern = pattern;

	}

	@Override
	public void setMatcher(String regex) {
		this.regex = regex;
	}

	@Override
	public String getMatcher() {
		return regex;
	}

	@Override
	public void setChanger(Function<MatchItem<T>, MatchItem<T>> changer) {
		this.changer = changer;
	}

	@Override
	public Function<MatchItem<T>, MatchItem<T>> getChanger() {
		return this.changer;
	}

}
