package com.kyj.fx.voeditor.visual.component.grid.xml;

import java.util.Arrays;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class XmlAttributeTableViewFactoryTest {

	public static class App extends Application {

		@Override
		public void start(Stage primaryStage) throws Exception {

			String data = FileUtil.readToString(XmlAttributeTableViewFactoryTest.class.getResourceAsStream("CustomValues.xml"));
			Optional<NodeList> xpathNodes = XMLUtils.toXpathNodes(data, "//Variable");
			xpathNodes.ifPresent(n -> {
				XmlAttributeTableViewFactory factory = new XmlAttributeTableViewFactory(
						Arrays.asList("Name", "Category", "Description", "Value"));
				factory.setColumnHandler((tc, name) -> {

					if ("Value".equals(name)) {
						tc.setEditable(true);
					}
					return tc;
				});
				XmlAttributeTableView generate = factory.generate(n);

				BorderPane borderPane = new BorderPane();
				Button btnSave = new Button("save");
				btnSave.setOnAction(ev -> {

					DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder builder = df.newDocumentBuilder();
						Document doc = builder.newDocument();

						Element createElement = doc.createElement("CustomVariables");
						//
						ObservableList<Node> items = generate.getItems();
						for (Node node : items) {
							createElement.appendChild(node);
							// Element eleVar = doc.createElement("Variable");
							// eleVar.setAttributeNode((Attr) node);
							// createElement.setAttributeNode(node);
						}

						System.out.println(doc.getTextContent());
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				});

				borderPane.setTop(new HBox(btnSave));
				borderPane.setCenter(generate);
				primaryStage.setScene(new Scene(generate));
			});

			primaryStage.show();
		}

		public static void main(String[] args) {
			launch(args);
		}
	}

	@Test
	public void testGenerate() {
		App.main(new String[] {});
	}

}
