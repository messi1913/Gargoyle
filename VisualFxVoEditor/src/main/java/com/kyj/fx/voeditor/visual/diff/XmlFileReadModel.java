/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class XmlFileReadModel implements IURLReadModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlFileReadModel.class);

	/*
	 * 파일 읽기 정의
	 */
	@Override
	public List<String> readLines(URL url) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try (InputStream in = new BOMInputStream(url.openStream());) {

			int tmp = -1;
			while ((tmp = in.read()) != -1) {
				out.write(tmp);
			}
		}

		String string = out.toString();
		LOGGER.debug(string);
		XMLDiffFormatter xmlFormatter = new XMLDiffFormatter();
		String format = xmlFormatter.format(string);

		return Stream.of(format.split("\n")).collect(Collectors.toList());
	}

}
