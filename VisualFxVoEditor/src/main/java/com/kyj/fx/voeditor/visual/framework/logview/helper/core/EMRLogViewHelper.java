/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 10. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.logview.helper.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.ColumnName;
import com.kyj.fx.voeditor.visual.framework.logview.helper.converter.AbstractConverter;
import com.kyj.fx.voeditor.visual.framework.logview.helper.converter.Converter;
import com.kyj.fx.voeditor.visual.framework.logview.helper.converter.DateConverter;
import com.kyj.fx.voeditor.visual.framework.logview.helper.converter.XMLFormatConverter;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class EMRLogViewHelper extends LogViewHelper<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EMRLogViewHelper.class);

	public static final DateConverter DATE_CONVERTER = new DateConverter("[0-9]{14}", DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS,
			DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS);

	public static final XMLFormatConverter XML_CONVERTER = new XMLFormatConverter("<TransactionRequest>.+</TransactionRequest>");

	public static class RequestMessageTypeConverter extends AbstractConverter<String> {

		public RequestMessageTypeConverter() {
			super("Message => Request:");
		}

		@Override
		public String convert(String data) {
			return "Request";
		}

	}

	public static class ResponseMessageTypeConverter extends AbstractConverter<String> {

		public ResponseMessageTypeConverter() {
			super("Message => Response:");
		}

		@Override
		public String convert(String data) {
			return "Response";
		}

	}

	public EMRLogViewHelper() {

		// Request

		ArrayList<Converter<String>> converter = new ArrayList<>();

		// Date
		converter.add(DATE_CONVERTER);

		// XML
		converter.add(XML_CONVERTER);

		converter.add(new RequestMessageTypeConverter());

		converter.add(new ResponseMessageTypeConverter());

		// Replace Item Name to TransactionType
		XML_CONVERTER.setChanger(m -> {
			String matched = m.getMatched().trim();
			Optional<String> xpathText = XMLUtils.toXpathText(matched, "//TransactionType");
			if (xpathText.isPresent()) {
				m.setName(xpathText.get());
			}
			return m;
		});

		// set Data Converter
		setConverter(converter);

		// Nothing.
		setMatchedListener(t -> t);
	}

	public <T> T getValue(Function<Map<String, MatchItem<String>>, T> convert) {
		return convert.apply(getItem());
	}

	private Function<Map<String, MatchItem<String>>, String> defaultStringDataConveter = item -> {
		Collection<MatchItem<String>> values = item.values();

		String dateString = "";
		String transactionType = "";
		String xml = "";
		String serviceType = "";

		for (MatchItem<String> m : values) {

			Converter<String> converter = m.getConverter();

			if (converter instanceof DateConverter) {
				dateString = m.getMatched();
			} else if (converter instanceof XMLFormatConverter) {
				xml = m.getMatched();
				transactionType = m.getName();
			} else if (converter instanceof RequestMessageTypeConverter) {
				if (m.getMatched() != null)
					serviceType = m.getMatched();
			} else if (converter instanceof ResponseMessageTypeConverter) {
				if (m.getMatched() != null)
					serviceType = m.getMatched();
			}
		}

		return String.format("type : [%s][%s][%s]\nXML:%s", serviceType, transactionType, dateString, xml);
	};

	public String getValue() {
		return defaultStringDataConveter.apply(getItem());
	}

	public DefaultFxDataInfo getFxValue() {
		return getValue(item -> {
			DefaultFxDataInfo info = new DefaultFxDataInfo();
			Collection<MatchItem<String>> values = item.values();

			if (values.isEmpty())
				return null;
			for (MatchItem<String> m : values) {

				Converter<String> converter = m.getConverter();

				if (converter instanceof DateConverter) {
					info.setDateString(m.getMatched());
				} else if (converter instanceof XMLFormatConverter) {
					info.setXml(m.getMatched());
					info.setTransactionType(m.getName());
				} else if (converter instanceof RequestMessageTypeConverter) {
					if (m.getMatched() != null) {
						info.setServiceType(m.getMatched());
					}
				} else if (converter instanceof ResponseMessageTypeConverter) {
					if (m.getMatched() != null) {
						info.setServiceType(m.getMatched());
					}
				}
			}
			return info;
		});
	}

	public static class DefaultFxDataInfo extends AbstractDVO {

		@ColumnName(value = "트랜잭션")
		private StringProperty transactionType;

		@ColumnName(value = "요청타입")
		private StringProperty serviceType;

		@ColumnName(value = "날짜")
		private StringProperty dateString;

		@ColumnName(value = "XML")
		private StringProperty xml;

		public DefaultFxDataInfo() {
			transactionType = new SimpleStringProperty();
			serviceType = new SimpleStringProperty();
			dateString = new SimpleStringProperty();
			xml = new SimpleStringProperty();

		}

		public final StringProperty transactionTypeProperty() {
			return this.transactionType;
		}

		public final String getTransactionType() {
			return this.transactionTypeProperty().get();
		}

		public final void setTransactionType(final String transactionType) {
			this.transactionTypeProperty().set(transactionType);
		}

		public final StringProperty serviceTypeProperty() {
			return this.serviceType;
		}

		public final String getServiceType() {
			return this.serviceTypeProperty().get();
		}

		public final void setServiceType(final String serviceType) {
			this.serviceTypeProperty().set(serviceType);
		}

		public final StringProperty dateStringProperty() {
			return this.dateString;
		}

		public final String getDateString() {
			return this.dateStringProperty().get();
		}

		public final void setDateString(final String dateString) {
			this.dateStringProperty().set(dateString);
		}

		public final StringProperty xmlProperty() {
			return this.xml;
		}

		public final String getXml() {
			return this.xmlProperty().get();
		}

		public final void setXml(final String xml) {
			this.xmlProperty().set(xml);
		}

		@Override
		public String toString() {
			return String.format("type : [%s][%s][%s]\nXML:%s", serviceType.get(), transactionType.get(), dateString.get(), xml.get());
		}

	}
}
