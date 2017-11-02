/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kyj.fx.voeditor.visual.framework.logview.helper.converter.Converter;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class LogViewHelper<T> {

	private String readLine;

	private List<Converter<T>> patterns;

	/**
	 * first String : String pattern <br/>
	 * second String : targetText <br/>
	 * 
	 * @최초생성일 2017. 10. 27.
	 */
	private Function<MatchItem<T>, MatchItem<T>> matchedListener;

	private Map<String, MatchItem<T>> items = new HashMap<>();

	public static class MatchItem<T> {

		public MatchItem(LogViewHelper<T> helper, Converter<T> converter, T matchedString) {
			this.helper = helper;
			this.converter = converter;
			this.matchedString = matchedString;
		}

		String name;
		LogViewHelper<T> helper;
		Converter<T> converter;
		T matchedString;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public T getMatched() {
			return matchedString;
		}

		public Converter<T> getConverter() {
			return converter;
		}

		public void setConverter(Converter<T> converter) {
			this.converter = converter;
		}

		public void setMatched(T matched) {
			this.matchedString = matched;
		}

	}

	/**
	 */
	public LogViewHelper() {
		patterns = new ArrayList<>();
	}

	public void setConverter(List<Converter<T>> converter) {
		this.patterns = converter;
	}

	public void setText(String readLine) {
		this.readLine = readLine;
	}

	public Map<String, MatchItem<T>> getItems() {
		return items;
	}

	public void setItems(Map<String, MatchItem<T>> items) {
		this.items = items;
	}

	public boolean read() {
		items.clear();
		boolean exists = false;
		String tmp = readLine;
		if (ValueUtil.isEmpty(tmp))
			return exists;

		for (Converter<T> pt : patterns) {

			M m = regexMatch(pt.getMatcher(), tmp);

			String matched = m.matched;
			if (matched == null)
				continue;

			T replaced = pt.convert(matched);

			MatchItem<T> matchItem = new MatchItem<>(this, pt, replaced);
			matchItem.setName(pt.getMatcher());

			// this is global changer
			matchItem = matchedListener.apply(matchItem);

			// this is local changer
			if (pt.getChanger() != null) {
				matchItem = pt.getChanger().apply(matchItem);
			}

			items.put(pt.getMatcher(), matchItem);

			tmp = m.removedMatch;
			exists = true;
		}

		return exists;
	}

	protected static class M {
		int start = -1;
		int end = -1;
		String matched;
		String removedMatch;
	}

	/**
	 * 일치된 문장은 지움.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 27.
	 * @param pattern
	 * @param data
	 * @return
	 */
	protected M regexMatch(String pattern, String value) {
		Pattern compile = Pattern.compile(pattern);

		StringBuffer sb = new StringBuffer(value);

		Matcher matcher = compile.matcher(value);
		int start = -1;
		int end = -1;
		String find = null;
		if (matcher.find()) {
			start = matcher.start();
			end = matcher.end();
			find = value.substring(start, end);
			sb.replace(start, end, "");
		}

		M m = new M();
		m.start = start;
		m.end = end;
		m.matched = find;
		m.removedMatch = sb.toString();
		return m;
	}

	public Function<MatchItem<T>, MatchItem<T>> getMatchedListener() {
		return matchedListener;
	}

	public void setMatchedListener(Function<MatchItem<T>, MatchItem<T>> matchedListener) {
		this.matchedListener = matchedListener;
	}

	public Map<String, MatchItem<T>> getItem() {
		return this.items;
	}
}
