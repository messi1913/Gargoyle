/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : SaveSQLFileConsumer.java
 *	date      : 2015. 11. 19.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.io.File;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
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
			if (file != null && file.exists()) {

				//인코딩이 존재하지않는경우 UTF-8로 치환.
				String encoding = option.getEncoding();
				if(ValueUtil.isEmpty(encoding))
					encoding = "UTF-8";

				content = FileUtils.readFileToString(file, encoding);
			} else {

				//만약 파일이 존재하지않는다면 option파라미터에서 제공되는 처리내용을 반영.
				Function<File, String> fileNotFoundThan = option.getFileNotFoundThan();
				if (fileNotFoundThan != null) {
					content = fileNotFoundThan.apply(file);
				}
			}

		} catch (Exception e) {
			option.setException(e);
			LOGGER.error(ValueUtil.toString(e));
		}
		return content;
	}
}
