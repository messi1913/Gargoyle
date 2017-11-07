/**
 * 
 */
package com.kyj.fx.voeditor.visual.component.text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLResult;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil.SaveAsModel;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;
import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser.ExtensionFilter;

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
	@FXML
	private CheckMenuItem cmiOpenWithWebView;

	@FXML
	private SplitPane spResultContainer;

	private XMLEditor xeXmlData = new XMLEditor();
	private XMLEditor xeXsltData = new XMLEditor();
	private XMLEditor xeTransform = new XMLEditor();

	private WebView wbOpenWith = new WebView();

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
		wbOpenWith.getEngine().setJavaScriptEnabled(true);
		wbOpenWith.getEngine().setOnAlert(ev -> {
			DialogUtil.showMessageDialog(ev.getData());
		});

		cmiOpenWithWebView.selectedProperty().addListener((oba, o, n) -> {
			if (n) {
				spResultContainer.getItems().add(wbOpenWith);
				wbOpenWith.getEngine().loadContent(xeTransform.getText());
			} else
				spResultContainer.getItems().remove(wbOpenWith);
		});

		cmiOpenWithWebView.setSelected(true);
	}

	@FXML
	public void btnConvertOnAction() {

		try {

			StreamSource xlstSource = new StreamSource(new ByteArrayInputStream(xeXsltData.getText().getBytes()));
			StreamSource dataSource = new StreamSource(new ByteArrayInputStream(xeXmlData.getText().getBytes()));

			Transformer newTransformer = tFactory.newTransformer(xlstSource);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			
			//이 값은 false로 두어야 데이터 변경이 없음.
			format.setTrimText(false);

			newTransformer.transform(dataSource, new XMLResult(out, format));

			String string = out.toString();
			this.xeTransform.setText(string);
			if (cmiOpenWithWebView.isSelected()) {
				wbOpenWith.getEngine().loadContent(xeTransform.getText());
			}

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

	/**
	 * Mapping Sample Data to UI
	 */
	@FXML
	public void miShowSampleDataOnAction() {
		xeXmlData.setText(XsltTransformCompositeSamples.xmlDataSample());
		xeXsltData.setText(XsltTransformCompositeSamples.xsltSample());

		btnConvertOnAction();
	}

	/**
	 * 
	 */
	@FXML
	public void miSaveAsTemplates() {

		File f = DialogUtil.showFileSaveDialog(FxUtil.getWindow(this), c -> {
			c.getExtensionFilters()
					.add(new ExtensionFilter("XLST Template files (*.gxml)", GargoyleExtensionFilters.EXTENSION_COMMONS + "gxml"));
		});
		Map<String, String> map = new HashMap<>();
		map.put("xmlData", xeXmlData.getText());
		map.put("xsltData", xeXsltData.getText());

		FileUtil.writeFile(f, ValueUtil.toJSONString(map), StandardCharsets.UTF_8, err -> LOGGER.error(ValueUtil.toString(err)));

	}

	/**
	 * 
	 */
	@FXML
	public void miSaveResultXmlOnAction() {
		FxUtil.saveAsFx(FxUtil.getWindow(this), new SaveAsModel() {
			@Override
			public String getContent() {
				return xeTransform.getText();
			}
		});
	}

	/**
	 * 템플릿 파일 open
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 1.
	 */
	@FXML
	public void miOpenTemplates() {
		File file = FxUtil.showFileOpenDialog(FxUtil.getWindow(this), c -> {
			c.getExtensionFilters()
					.add(new ExtensionFilter("XLST Template files (*.gxml)", GargoyleExtensionFilters.EXTENSION_COMMONS + "gxml"));
		});

		String cont = FileUtil.readFile(file, LoadFileOptionHandler.getDefaultHandler());
		JSONObject json = ValueUtil.toJSONObject(cont);
		Object xmlObj = json.get("xmlData");

		if (ValueUtil.isNotEmpty(xmlObj)) {
			xeXmlData.setText(xmlObj.toString());
		}

		Object xlstObj = json.get("xsltData");
		if (ValueUtil.isNotEmpty(xlstObj)) {
			xeXsltData.setText(xlstObj.toString());
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 1.
	 */
	@FXML
	public void miOpenSystemBrowserOnAction() {

		File file = new File(FileUtil.getTempGagoyle(), "tmp.html");
		FileUtil.writeFile(file, xeTransform.getText(), StandardCharsets.UTF_8, ex -> LOGGER.error(ValueUtil.toString(ex)));

		if (file.exists())
			RuntimeClassUtil.openFile(file);

	}

	@FXML
	public void miOpenFxBorwserOnAction() {
		FxUtil.openBrowser(this, xeTransform.getText(), false);
	}

	@FXML
	public void miPrintOnAction() {
		FxUtil.printDefefaultJob(FxUtil.getWindow(this), wbOpenWith);
	}
}
