/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.external.school
 *	작성일   : 2015. 11. 27.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/
package com.kyj.external.school;

import java.util.List;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class AnswerTableEditorUtilTest {

	@Test
	public void convertTest() {

		String text = "14442 42414 34344 44412 43K11 32223";

		List<List<String>> convert = AnswerTableEditorUtil.convertNumbering(text,5);

		convert.forEach(System.out::println);

	}
}
