/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.dom4j.io.XMLResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * 
 * xlst convert program.
 * 
 * @author KYJ
 */
@FXMLController(value = "XsltTransformComposite.fxml", css = "XsltTransformComposite.css", isSelfController = true)
public class XsltTransformComposite extends BorderPane {
	private static final Logger LOGGER = LoggerFactory.getLogger(XsltTransformComposite.class);

	@FXML
	private StackPane spXmlData, spXlstData, spTransfromResult;
	private XMLEditor xeXmlData = new XMLEditor();
	private XMLEditor xeXsltData = new XMLEditor();
	private XMLEditor xeTransform = new XMLEditor();

	private TransformerFactory tFactory;

	public XsltTransformComposite() {
		FxUtil.loadRoot(XsltTransformComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
		tFactory = TransformerFactory.newInstance();
	}

	@FXML
	public void initialize() throws IOException {

		spXmlData.getChildren().add(xeXmlData);
		spXlstData.getChildren().add(xeXsltData);
		spTransfromResult.getChildren().add(xeTransform);

	}

	@FXML
	public void btnConvertOnAction() {

		try {

			StreamSource xlstSource = new StreamSource(new ByteArrayInputStream(xeXsltData.getText().getBytes()));
			StreamSource dataSource = new StreamSource(new ByteArrayInputStream(xeXmlData.getText().getBytes()));

			Transformer newTransformer = tFactory.newTransformer(xlstSource);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			newTransformer.transform(dataSource, new XMLResult(out));

			this.xeTransform.setText(out.toString("UTF-8"));
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}
}
