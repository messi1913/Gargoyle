/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author KYJ
 *
 */
public class SQLKeywordFactory {

	/* 대문자 */
	private static final String[] KEYWORDS = new String[] { "SELECT", "FROM", "GROUP", "BY", "WHERE", "JOIN", "AND", "UPDATE", "DELETE",
			"CREATE", "DROP", "SET", "NOT", "NULL", "INSERT into", "ALTER", "ORDER", "ENGINE", "COLLATE", "COMMENT", "FOREIGN", "KEY",
			"REFERENCES", "CONSTRAINT", "INDEX", "DEFAULT", "AUTO_INCREMENT", "TABLE", "PRIMARY" };

	private static List<String> SQLKEYWORDS;

	static {
		generate();
	}

	private static void generate() {
		SQLKEYWORDS = Stream.of(KEYWORDS).collect(() -> {
			return new ArrayList<String>();
		}, (collection, next) -> {
			collection.add(next);
			collection.add(next.toLowerCase());
		}, (collection, next) -> {
			collection.addAll(next);
		});

	}

	public static List<String> getKeywords() {

		if (SQLKEYWORDS != null)
			return SQLKEYWORDS;
		else
			generate();

		return SQLKEYWORDS;
	}

}
