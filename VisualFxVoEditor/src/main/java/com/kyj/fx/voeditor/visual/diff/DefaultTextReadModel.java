/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 1. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KYJ
 *
 */
public class DefaultTextReadModel implements IReadModel<String, String> {

	/*
	 * 파일 읽기 정의
	 */
	@Override
	public List<String> readLines(String text) throws IOException {
		final List<String> lines = new ArrayList<String>();
		String line;

		final BufferedReader in = new BufferedReader(new StringReader(text));
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();
		return lines;
	}

}
