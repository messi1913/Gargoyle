/**
 *
 */
package com.kyj.fx.voeditor.visual.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.Template;
import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.framework.mail.Mailer;
import com.kyj.fx.voeditor.visual.framework.mail.SenderMailInfo;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

/**
 * @author user
 *
 */
public class MailUtilTest {

	@Before
	public void init() throws Exception {
		System.out.println("proxy settings..");
		new ProxyInitializable().initialize();

	}

	@Test
	public void sendMailTest() throws Exception {

		Mail mail = new Mail();
		mail.setMailTo(new String[] { "", "" });

		Map<String, Object> velocityContext = new HashMap<>();

		velocityContext.put("seq", "667");
		velocityContext.put("date", "2015-09-12");
		velocityContext.put("numbers", "테스트번호입니다.");

		SenderMailInfo senderMailInfo = new SenderMailInfo();
		senderMailInfo.setSendUserId("");
		senderMailInfo.setSendUserPassword("");

		MailUtil.sendMail(senderMailInfo, mail, velocityContext);
	}

	@Test
	public void bodyMailTest() throws Exception {
		Mail mail = new Mail();
		mail.setMailFrom("");
		mail.setMailTo(new String[] { "" });
		Map<String, Object> param = new HashMap<>();
		Template template = MailUtil.createTemplate(ClassLoader.getSystemResource("templates/emailtemplate.vm"));
		mail.setEmailTemplate(template);
		param.put("seq", "667");
		param.put("date", DateUtil.getCurrentDateString());
		param.put("numbers", "테스트번호입니다.");

		MailUtil.sendMail(mail, param);

	}

	@Test
	public void sendmail() {

		// Recipient's email ID needs to be mentioned.
		String to = "mail to ";

		// Sender's email ID needs to be mentioned
		String from = "mail from";
		final String username = "username";// change accordingly
		final String password = "password";// change accordingly

		// Assuming you are sending email through relay.jangosmtp.net
		String host = "host.";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "25");
		props.put("mail.mime.charset", "UTF-8");

		props.put("mail.debug", "true");
		props.put("SSL", "true");

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject("Testing Subject");

			// Now set the actual message
			message.setText("Hello, this is sample for to check send " + "email using JavaMailAPI ");

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}
}
