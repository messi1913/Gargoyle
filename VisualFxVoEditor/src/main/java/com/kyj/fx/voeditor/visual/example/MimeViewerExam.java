/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.stream.Field;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * @author calla
 *
 */
public class MimeViewerExam extends Application {

	private Map<String, String> meta = new HashMap<String,String>();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		WebView center = new WebView();
		WebEngine engine = center.getEngine();

		URL resource = MimeViewerExam.class.getResource("Sample.html");
		// String externalForm = resource.toExternalForm();
		String readToString = FileUtil.readToString(resource.openStream());

		System.out.println("##########################################");
		System.out.println("mime string");
		System.out.println(readToString);
		System.out.println("##########################################");

		final MessageBuilder builder = new DefaultMessageBuilder();
		final Message message = builder.parseMessage(resource.openStream());
		Body body = ((Entity) message).getBody();

		StringBuilder sb = new StringBuilder();
		extracted(sb, body);

		
		
		
		
		Document parse = Jsoup.parse(sb.toString());
		Elements select = parse.select("img");
		select.forEach(e ->{
			String attr = e.attr("src");
			if(attr.startsWith("cid:")){
				String cid = attr.replace("cid:", "");
				String string = meta.get(cid);
				
				
				e.attr("src", "data:image/jpg;base64,".concat(string));
			}
			

		});
		
		
		System.out.println("##########################################");
		System.out.println("html string");
		System.out.println(sb.toString());
		System.out.println("##########################################");
		System.out.println(parse.toString());
		engine.loadContent(sb.toString(), "text/html");
		primaryStage.setScene(new Scene(new BorderPane(center)));
		primaryStage.show();

		
		
	}

	private void extracted(StringBuilder sb, Body body) {

		if (body instanceof Multipart) {
			Multipart mbody = (Multipart) body;
			for (Entity part : mbody.getBodyParts()) {
				extracted(sb, part);
			}
		} else if (body instanceof MessageImpl) {
			extracted(sb, body);
		} else if (body instanceof TextBody) {
			/*
			 * A text body. Display its contents.
			 */
			TextBody textBody = (TextBody) body;
			try {
				Reader r = textBody.getReader();
				StringBuilder _sb = new StringBuilder();
				int c;
				while ((c = r.read()) != -1) {
					_sb.append((char) c);
				}
				System.out.println(_sb.toString());
				sb.append(_sb.toString());
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		} else if (body instanceof BinaryBody) {
			
			BinaryBody bBody = (BinaryBody) body;

			Entity parent = bBody.getParent();
//			String dispositionType = parent.getDispositionType();
//			String filename = parent.getFilename();
//			String contentTransferEncoding = parent.getContentTransferEncoding();
//			String mimeType = parent.getMimeType();
			
			Field field = parent.getHeader().getField("Content-ID");
			String body2 = field.getBody();
			String contentId = body2.replace("<", "").replace(">", "");
			
			
			
			StringBuffer buf = new StringBuffer();

			try (InputStream is = bBody.getInputStream()) {
				int read = -1;
				while ((read = is.read()) != -1) {
					buf.append((char) read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			meta.put(contentId, buf.toString());
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
