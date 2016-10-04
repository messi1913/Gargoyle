/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 10. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;

/**
 * PMD 관련 유틸리티 클래스.
 *
 * @author KYJ
 *
 */
public class PMDUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PMDUtil.class);

	/**
	 *  파일확장자에 따른 PMD 지원여부를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param extension
	 * @return
	 */
	public static boolean isSupportedLanguageVersions(String extension) {
		return Stream.of(getSupportedLanguageVersions()).filter(l -> l.getLanguage().getExtensions().contains(extension)).findFirst()
				.isPresent();
	}

	/**
	 *  파일확장자에 따른 PMD 지원여부를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param extension
	 * @return
	 */
	public static boolean isSupportedLanguageVersions(File extension) {
		if (extension == null || !extension.exists())
			return false;
		return isSupportedLanguageVersions(FileUtil.getFileExtension(extension));
	}

	/**
	 * 지원가능한 PMD Language를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @return
	 */
	public static LanguageVersion[] getSupportedLanguageVersions() {
		List<LanguageVersion> languageVersions = new ArrayList<>();
		for (LanguageVersion languageVersion : LanguageRegistry.findAllVersions()) {
			LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();

			if (languageVersionHandler != null) {
				Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
				if (parser != null && parser.canParse()) {
					languageVersions.add(languageVersion);
					LOGGER.debug("support parser: {}", parser.toString());
				} else {
					LOGGER.debug("not support parser: {}", parser.toString());
				}
			} else {
				LOGGER.debug("not support parser (handler is null): {}", languageVersion.toString());
			}
		}
		return languageVersions.toArray(new LanguageVersion[languageVersions.size()]);
	}
}
