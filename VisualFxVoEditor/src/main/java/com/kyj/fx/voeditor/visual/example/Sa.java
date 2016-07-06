/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 6. 10.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author KYJ
 *
 */
public class Sa {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 10.
	 * @param args
	 */
	public static void main(String[] args) {
		HashSet<String> a = new HashSet<String>();
		a.add("a");
		a.add("b");
		a.add("c");
		a.add("d");

		String insertPreffix = "insert into :tableName ";
		String collect = a.stream().map(str -> "'".concat(str).concat("'")).collect(Collectors.joining(",", "(",")"));
		String insertMiddle = " values ";
		String insertSuffix = "";
		System.out.println(collect);

	}
	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/
}
