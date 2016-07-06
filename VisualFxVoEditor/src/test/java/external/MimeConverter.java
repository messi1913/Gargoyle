/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 5. 31.
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.poi.util.IOUtils;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.MimeHelper;

/**
 * @author KYJ
 *
 */
public class MimeConverter {

	@Test
	public void test() throws MalformedURLException, IOException, MessagingException {

		// MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new
		// Properties()));
		//
		// MimeMultipart mimeMultipart = new MimeMultipart();
		//
		// {
		// MimeBodyPart part = new MimeBodyPart();
		// part.setContent("simple", "text/html");
		// mimeMultipart.addBodyPart(part);
		// }
		// {
		// MimeBodyPart part = new MimeBodyPart();
		//
		// File file = new File("123.jpg");
		// String imageTo = MimeHelper.imageTo(file);
		//
		// StringBuilder b = new StringBuilder();
		// b.append(new String("<img src=\"data:image/jpg;base64,"));
		// b.append(imageTo);
		// b.append(new String("\" alt=\"\" width=\"683\" height=\"420\" />"));
		//
		// ByteArrayDataSource ds = new
		// ByteArrayDataSource(b.toString().getBytes(),
		// MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(file));
		// DataHandler dh = new DataHandler(ds);
		// part.setDataHandler(dh);
		// part.setContentID("imgId");
		//
		// String contentID = part.getContentID();
		// System.out.println(contentID);
		// System.out.println(part.getContentMD5());
		// System.out.println(part.getContent());
		//
		// mimeMultipart.addBodyPart(part);
		//
		// }

		FileOutputStream os = new FileOutputStream("mailContent1");

		// mimeMultipart.writeTo(os);

		File file = new File("123.jpg");
		String imageTo = MimeHelper.imageTo(file);

//		StringBuilder b = new StringBuilder();
//		b.append(new String("<img src=\"data:image/jpg;base64,"));
//		b.append(imageTo);
//		b.append(new String("\" alt=\"\" width=\"683\" height=\"420\" />"));

		try (FileWriter fileWriter = new FileWriter("opendInfo.dat")) {
			fileWriter.write(imageTo);
		}

		// mimeMessage.setContent(mimeMultipart);
		// mimeMessage.writeTo(os);
	}

	@Test
	public void simple() throws Exception {

		new ProxyInitializable().initialize();

		HtmlEmail email = new HtmlEmail();
		email.setHostName("mail.myserver.com");
		email.addTo("jdoe@somewhere.org", "John Doe");
		// email.setFrom("me@apache.org", "Me");
		email.setSubject("Test email with inline image");

		// embed the image and get the content id
		// URL url = new URL("https://i.stack.imgur.com/r7Nij.jpg?s=32&g=1");
		String cid = email.embed(new File("123.jpg"));

		// set the html message
		email.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid + "\"></html>");

		// set the alternative message
		email.setTextMsg("Your email client does not support HTML messages");

		MimeMessage mimeMessage = email.getMimeMessage();
		System.out.println(mimeMessage);

		System.out.println(email.sendMimeMessage());
		// send the email
		email.send();
	}
}
