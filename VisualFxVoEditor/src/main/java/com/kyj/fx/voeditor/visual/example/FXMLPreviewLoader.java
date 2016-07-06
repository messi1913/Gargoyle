/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 6. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.kyj.fx.voeditor.visual.framework.parser.FXMLSaxHandler;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class FXMLPreviewLoader extends Application {

	static File file = new File(
			"C:\\Users\\KYJ\\JAVA_FX\\webWorkspace\\sos-client\\src\\main\\java\\com\\samsung\\sds\\sos\\client\\view\\rel\\rs\\RelRsA00M.fxml");

	public static void main(String[] args) throws FileNotFoundException {

		launch(args);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane borderPane = new BorderPane();

		FXMLLoader loader = new FXMLLoader() {

		};

		// InputStream resourceAsStream =
		// FXMLPreviewLoader.class.getResourceAsStream("lang_ko.properties");
		//
		// loader.setResources(new PropertyResourceBundle(new
		// InputStreamReader(resourceAsStream, "UTF-8")) {
		// /*
		// * @inheritDoc
		// */
		// @Override
		// public boolean containsKey(String key) {
		// return true;
		// }
		//
		// /*
		// * @inheritDoc
		// */
		// @Override
		// public Object handleGetObject(String key) {
		// if (key == null) {
		// return "";
		// }
		//
		// Object result = null;
		//
		// try {
		// result = super.handleGetObject(key);
		// } catch (Exception e) {
		// ;
		// }
		//
		// return (result == null) ? key : result;
		// }
		// });

		// loader.setLocation(/*FXMLPreviewLoader.class.getResource("ColumnExam3.fxml")*/url);

		loader.setBuilderFactory(new BuilderFactory() {

			@Override
			public Builder<?> getBuilder(Class<?> param) {
				return new JavaFXBuilderFactory().getBuilder(param);
			}
		});
		loader.setControllerFactory(new Callback<Class<?>, Object>() {
			@Override
			public Object call(Class<?> param) {
				return null;
			}
		});

		FileInputStream inputStream = new FileInputStream(file);

		InputStream is = null;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			FXMLSaxHandler handler = new FXMLSaxHandler(out);
			sp.parse(inputStream, handler);

			String string = out.toString("UTF-8");

			string = ValueUtil.regexReplaceMatchs("<!\\[CDATA\\[[?<a-zA-Z. *?>]+]]>", string, str -> {
				return ValueUtil.regexMatch("<\\?import [a-zA-Z.*?]+>", str);
			});

			System.out.println(string);

			byte[] bytes = string.getBytes();
			is = new ByteArrayInputStream(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// FileInputStream inputStream = new FileInputStream(file);

		borderPane.setCenter(loader.load(is));
		Scene scene = new Scene(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	class Dummy {

	}
}
