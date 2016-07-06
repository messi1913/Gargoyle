/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : SaveSQLFileConsumer.java
 *	date      : 2015. 11. 19.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * SQL문을 로드함.
 *
 * @author KYJ
 *
 */
public class LoadSQLFileFunction extends LoadSQLFunction<File> {

	private Logger LOGGER = LoggerFactory.getLogger(LoadSQLFileFunction.class);

	/*
	 * SQL문을 불러온다.
	 * 
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.functions.SaveSQLConsumer#apply(
	 * java.lang.Object, java.lang.String)
	 */
	@Override
	public String apply(File file, LoadFileOptionHandler option) {
		String content = "";
		try {
			if (file != null && file.exists())
				content = FileUtils.readFileToString(file, option.getEncoding());

		} catch (Exception e) {
			option.setException(e);
			LOGGER.error(ValueUtil.toString(e));
		}
		return content;
	}
}
