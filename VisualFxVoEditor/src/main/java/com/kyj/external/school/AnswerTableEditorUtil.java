/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.external.school
 *	작성일   : 2015. 11. 27.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/
package com.kyj.external.school;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author KYJ
 *
 */
public class AnswerTableEditorUtil {

	/**
	 * 문자열패턴이 아래와 같음 아래의 텍스트를 분리함. 객관식문제 분리 생활한문 14442 42414 34344 44412 43K11
	 * 32223
	 * 
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 27.
	 * @param text
	 * @param startingNumber
	 *            시작번호
	 * @return
	 */
	public static List<List<String>> convertNumbering(String text, int startingNumber) {
		return convert(text, list -> {
			List<String> arr = new ArrayList<>();
			for (int i = startingNumber; i < list.size() + startingNumber; i++) {
				arr.add(String.valueOf(i));
			}
			return arr;
		}, null);
	}

	/*
	 * 문자열패턴이 아래와 같음 아래의 텍스트를 분리함.
	 * 
	 * 객관식문제 분리 생활한문 14442 42414 34344 44412 43K11 32223
	 */
	public static List<List<String>> convertNumbering(String text) {
		return convertNumbering(text, 1);
	}

	/*
	 * 문자열패턴이 아래와 같음 아래의 텍스트를 분리함.
	 * 
	 * 객관식문제 분리 생활한문 14442 42414 34344 44412 43K11 32223
	 */

	public static List<List<String>> convert(String text) {
		return convert(text, null, null);
	}
	/**
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 27.
	 * @param text
	 *            형태를 바꿀 데이터
	 * @param prevAppender
	 *            앞에 붙일 헤더
	 * @param suffAppender
	 *            뒤에붙일 데이터
	 * @return
	 */
	public static List<List<String>> convert(String text, Function<List<String>, List<String>> prevAppender,
			Function<List<String>, List<String>> suffAppender) {
		
		List<List<String>> returnList = new ArrayList<>();

		String[] split = text.split("[\\t\\n, ]+");
		List<String> collect = Stream.of(split).filter(str -> !str.trim().isEmpty()).flatMap(str -> {

			List<String> strLit = new ArrayList<>();
			char[] charArray = str.trim().toCharArray();
			for (char c : charArray) {
				strLit.add(String.valueOf(c));
			}
			return strLit.stream();

		}).collect(Collectors.toList());

		if (prevAppender != null) {
			returnList.add(prevAppender.apply(collect));
		}

		returnList.add(collect);

		if (suffAppender != null)
			returnList.add(suffAppender.apply(collect));

		return returnList;
	}

}
