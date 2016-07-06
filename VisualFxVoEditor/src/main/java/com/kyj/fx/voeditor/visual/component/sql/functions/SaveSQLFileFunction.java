/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : SaveSQLFileConsumer.java
 *	date      : 2015. 11. 19.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * SQL문을 파일로 저장처리하는 로직을 기술함.
 *
 * @author KYJ
 *
 */
public class SaveSQLFileFunction extends SaveSQLFunction<File> {

	private Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SaveSQLFileFunction.class);

	/*
	 * SQL문을 파일로 저장한다.
	 *
	 * @see
	 * com.kyj.fx.voeditor.visual.component.sql.functions.SaveSQLConsumer#apply(
	 * java.lang.Object, java.lang.String)
	 */
	@Override
	public Boolean apply(File t, String u) {

		try {
			FileUtils.writeStringToFile(t, u);
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
			return false;
		}
		return true;
	}

}
