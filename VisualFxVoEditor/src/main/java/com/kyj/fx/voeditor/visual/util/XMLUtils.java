/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.StringReader;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;

/**
 * @author KYJ
 *
 */
public class XMLUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

	/**
	 */
	public XMLUtils() {

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 5.
	 * @param data
	 * @param xpath
	 * @return
	 */
	public static Optional<NodeList> toXpathNodes(String data, String xpath) {
		return toXpathValue(data, xpath, (doc, exp) -> {
			NodeList result = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
			return result;
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 5.
	 * @param data
	 * @param xpath
	 * @param errorHandler
	 * @return
	 */
	public static Optional<NodeList> toXpathNodes(String data, String xpath, ExceptionHandler errorHandler) {
		return toXpathValue(data, xpath, (doc, exp) -> {
			NodeList result = (NodeList) exp.evaluate(doc, XPathConstants.NODESET);
			return result;
		}, errorHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 5.
	 * @param data
	 * @param xpath
	 * @return
	 */
	public static Optional<String> toXpathText(String data, String xpath) {
		return toXpathValue(data, xpath, (doc, exp) -> {
			return exp.evaluate(doc, XPathConstants.STRING).toString();
		});
	}

	/**
	 * @author KYJ
	 *
	 * @param <T>
	 * @param <R>
	 */
	@FunctionalInterface
	static interface XPathExpressionFunction<T, R> {
		public R apply(org.w3c.dom.Document doc, T t) throws Exception;
	};

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 5.
	 * @param data
	 * @param xpath
	 * @param converter
	 * @return
	 */
	private static <T> Optional<T> toXpathValue(String data, String xpath, XPathExpressionFunction<XPathExpression, T> converter) {
		return toXpathValue(data, xpath, converter, ex -> {
			LOGGER.error(ValueUtil.toString(ex));
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 5.
	 * @param data
	 * @param xpath
	 * @param converter
	 * @param exHandler
	 * @return
	 */
	private static <T> Optional<T> toXpathValue(String data, String xpath, XPathExpressionFunction<XPathExpression, T> converter,
			ExceptionHandler exHandler) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		org.w3c.dom.Document doc = null;
		try {
			factory.setValidating(false);

			factory.setIgnoringComments(true);
			factory.setNamespaceAware(false);
			factory.setXIncludeAware(false);

			// StringInputStream in = new StringInputStream(data, "UTF-8");
			InputSource source = new InputSource(new StringReader(data));

			DocumentBuilder newDocumentBuilder = factory.newDocumentBuilder();
			doc = newDocumentBuilder.parse(source);

			XPathFactory xFactory = XPathFactory.newInstance();
			XPath xPath = xFactory.newXPath();
			XPathExpression compile = xPath.compile(xpath);
			return Optional.of(converter.apply(doc, compile));
			// return result.toString();
		} catch (Exception exp) {
			if (exHandler != null)
				exHandler.handle(exp);
			else
				LOGGER.error(ValueUtil.toString(exp));
		}
		return Optional.empty();
	}
}
