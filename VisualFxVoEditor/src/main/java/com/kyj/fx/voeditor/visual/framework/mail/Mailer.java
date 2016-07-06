package com.kyj.fx.voeditor.visual.framework.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.kyj.fx.voeditor.visual.util.MailUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.star.lang.IllegalArgumentException;

public class Mailer {
	private JavaMailSenderImpl mailSender;

	@Deprecated
	private VelocityEngine velocityEngine;
	private String mailFrom;
	private String mailTitle;
	private String mailTemplate;
	private String encoding;
	private String mailUseYn;

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

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

	public void sendMail(SenderMailInfo mailSenderInfo, List<Mail> mails, VelocityContext velocityContext)
			throws Exception {

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

		if (mail.getMailFrom() != null) {
			message.setFrom(mail.getMailFrom());
		} else {
			message.setFrom(mailFrom);
		}

		message.setTo(mail.getMailTo());

		if (mail.getMailSubject() != null) {
			message.setSubject(mail.getMailSubject());
		} else {
			message.setSubject(this.mailTitle);
		}

		if (encoding != null)
			_encoding = encoding;

		if (mailSenderInfo != null) {
			String sendUserId = mailSenderInfo.getSendUserId();
			String sendUserPassword = mailSenderInfo.getSendUserPassword();
			if (ValueUtil.isEmpty(sendUserId) || ValueUtil.isEmpty(sendUserPassword)) {
				throw new IllegalArgumentException("user id or password is empty...");
			}

			mailSender.setUsername(sendUserId);
			mailSender.setPassword(sendUserPassword);
		}
		// MailUtil.getTemplate(velocityEngine,mail.getTemplateName(),this.mailTemplate);
		Template template = MailUtil.getTemplateFromFile(mailTemplate);
		template.setEncoding(_encoding);

		StringWriter stringWriter = new StringWriter();
		template.merge(velocityContext, stringWriter);
		message.setText( stringWriter.toString());

//		MimeMessage createMimeMessage = mailSender.createMimeMessage();
//		createMimeMessage.addHeader("text/html", stringWriter.toString());


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