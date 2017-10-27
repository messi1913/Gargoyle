/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.logview.helper.converter
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.converter;

import com.kyj.fx.voeditor.visual.util.XMLFormatter;

/**
 * @author KYJ
 *
 */
public class XMLFormatConverter extends AbstractConverter<String> {

	/**
	 */
	public XMLFormatConverter() {

	}

	public XMLFormatConverter(String regex, String fromPattern, String toPattern) {
		super(regex, fromPattern, toPattern);

	}

	public XMLFormatConverter(String fromPattern, String toPattern) {
		super(fromPattern, toPattern);
	}

	public XMLFormatConverter(String regex) {
		super(regex);
	}

	@Override
	public String convert(String data) {

		XMLFormatter newInstnace = XMLFormatter.newInstnace();
//		newInstnace.setSuppressDeclaration(false);
		return newInstnace.format(data);
	}

}
