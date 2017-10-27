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
public interface Converter<T> {

	void setMatcher(String regex);

	String getMatcher();

	T convert(String data);

	String getFromPattern();

	void setFromPattern(String pattern);

	String getToPattern();

	void setToPattern(String pattern);

	void setChanger(Function<MatchItem<T>, MatchItem<T>> changer);

	Function<MatchItem<T>, MatchItem<T>> getChanger();
}
