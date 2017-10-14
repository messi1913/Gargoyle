package com.kyj.fx.voeditor.visual.framework.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.kyj.fx.voeditor.visual.util.MailUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class Mailer {
	private JavaMailSenderImpl mailSender;

	@Deprecated
	private VelocityEngine velocityEngine;

	/**
	 * @최초생성일 2017. 10. 14.
	 */
	private String mailTitle;
	/**
	 * @최초생성일 2017. 10. 14.
	 */
	private String mailTemplate;
	/**
	 * @최초생성일 2017. 10. 14.
	 */
	private String encoding;
	/**
	 * @최초생성일 2017. 10. 14.
	 */
	private String mailUseYn;

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	// public void setMailFrom(String mailFrom) {
	// this.mailFrom = mailFrom;
	// }

	public void setMailTitle(String mailTitle) {
		this.mailTitle = mailTitle;
	}

	public void setMailTemplate(String mailTemplate) {
		this.mailTemplate = mailTemplate;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param mailUseYn
	 *            the mailUseYn to set
	 */

	public void setMailUseYn(String mailUseYn) {
		this.mailUseYn = mailUseYn;
	}

	public void sendMail(SenderMailInfo mailSenderInfo, List<Mail> mails, VelocityContext velocityContext) throws Exception {

		for (Mail mail : mails) {
			sendMail(mailSenderInfo, mail, velocityContext);
		}
	}

	public void sendMail(SenderMailInfo mailSenderInfo, Mail mail, VelocityContext velocityContext) throws Exception {

		if (this.mailUseYn != null) {

			if ("N".equals(this.mailUseYn)) {
				throw new Exception("Mail Serivce's configuration is not set useYn Y ");
			}
		}

		String _encoding = MailConst.MAILER_DEFAULT_ENCODING;

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(mail.getMailTo().stream().toArray(String[]::new));

		if (mail.getMailSubject() != null) {
			message.setSubject(mail.getMailSubject());
		} else {
			message.setSubject(this.mailTitle);
		}

		if (ValueUtil.isNotEmpty(encoding))
			_encoding = encoding;

		if (mailSenderInfo != null) {
			String sendUserId = mailSenderInfo.getSendUserId();
			String sendUserPassword = mailSenderInfo.getSendUserPassword();
			if (ValueUtil.isNotEmpty(sendUserId)) {
				mailSender.setUsername(sendUserId);
			}
			message.setFrom(mailSender.getUsername());

			if (ValueUtil.isNotEmpty(sendUserPassword)) {
				mailSender.setPassword(sendUserPassword);
			}

			mailSender.setDefaultEncoding(mailSenderInfo.getDefaultEncoding());
			String host = mailSenderInfo.getHost();
			if (host != null)
				mailSender.setHost(host);
			Properties javaMailProperties = mailSenderInfo.getJavaMailProperties();
			if (javaMailProperties != null)
				mailSender.setJavaMailProperties(javaMailProperties);
			Properties props = new Properties();

			props.put("mail.smtp.host", "smtp.naver.com");
			props.put("mail.smtp.user", "callakrsos");
			props.put("SSL", "true");
			props.put("mail.transport.protocol", "smtps");

			// Session session = Session.getDefaultInstance(props, null);
			// if (session != null)
			// mailSender.setSession(session);
		}

		// MailUtil.getTemplate(velocityEngine,mail.getTemplateName(),this.mailTemplate);
		Template template = null;
		if (ValueUtil.isNotEmpty(mailTemplate)) {
			template = MailUtil.createTemplate(mailTemplate);
		} else {
			template = MailUtil.createTemplate(ClassLoader.getSystemResource("templates/emailtemplate.vm"));
		}

		template.setEncoding(_encoding);

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);
		message.setText(stringWriter.toString());

		// MimeMessage createMimeMessage = mailSender.createMimeMessage();
		// createMimeMessage.addHeader("text/html", stringWriter.toString());

		mailSender.send(message);

	}

	private File writeTemplate(String templateName) {
		File file = new File(templateName);
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			String result = IOUtils.toString(classLoader.getResourceAsStream("templates/emailtemplate.vm"));

			FileUtils.writeStringToFile(file, result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
}