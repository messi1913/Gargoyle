/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.AddressListField;
import org.apache.james.mime4j.dom.field.ContentTypeField;
import org.apache.james.mime4j.dom.field.DateTimeField;
import org.apache.james.mime4j.dom.field.UnstructuredField;
import org.apache.james.mime4j.field.address.AddressFormatter;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.Field;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import scala.collection.mutable.StringBuilder;

/**
 * @author calla
 *
 */
public class MimeViewerExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		WebView center = new WebView();
		WebEngine engine = center.getEngine();

		URL resource = MimeViewerExam.class.getResource("Sample.html");

		final MessageBuilder builder = new DefaultMessageBuilder();
		final Message message = builder.parseMessage(resource.openStream());
		Body body = ((Entity) message).getBody();

		StringBuilder sb = new StringBuilder();
		extracted(sb, body);

		engine.loadContent(sb.toString());
		primaryStage.setScene(new Scene(new BorderPane(center)));
		primaryStage.show();

	}

	private void extracted(StringBuilder sb, Body body) {
		if (body instanceof TextBody) {
			/*
			 * A text body. Display its contents.
			 */
			TextBody textBody = (TextBody) body;
			try {
				Reader r = textBody.getReader();
				int c;
				while ((c = r.read()) != -1) {
					sb.append((char) c);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		} else if (body instanceof BinaryBody) {
			BinaryBody bBody = (BinaryBody) body;
			extracted(sb, bBody);
		} else if (body instanceof Multipart) {
			Multipart mbody = (Multipart) body;
			for (Entity part : mbody.getBodyParts()) {
				extracted(sb, part);
			}
		}

		/*
		 * Ignore Fields </br>
		 * 
		 * ContentTypeField,AddressListField,DateTimeField UnstructuredField,
		 * Field
		 * 
		 */
		else {
			sb.append(body.toString());
		}
	}

	private void extracted(StringBuilder sb, Entity body) {
		extracted(sb, body.getBody());
	}

}
