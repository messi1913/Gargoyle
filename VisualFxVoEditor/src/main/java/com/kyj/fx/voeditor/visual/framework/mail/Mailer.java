package com.kyj.fx.voeditor.visual.framework.mail;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.kyj.fx.voeditor.visual.util.MailUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class Mailer {

	private static final Logger LOGGER = LoggerFactory.getLogger(Mailer.class);

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

	public JavaMailSenderImpl getMailSender() {
		return mailSender;
	}

	/**
	 * @param mailUseYn
	 *            the mailUseYn to set
	 */

	public void setMailUseYn(String mailUseYn) {
		this.mailUseYn = mailUseYn;
	}

	@Deprecated
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

		// new MimeMailMessage(new MimeMessage());
		// SimpleMailMessage message = new SimpleMailMessage();
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(mail.getMailTo().stream().toArray(String[]::new));

		helper.setCc(mail.getMailCc().stream().toArray(String[]::new));

		helper.setBcc(mail.getBcc().stream().toArray(String[]::new));

		helper.setSentDate(new Date());

		if (mail.getMailSubject() != null) {
			helper.setSubject(mail.getMailSubject());
		} else {
			helper.setSubject(this.mailTitle);
		}

		if (ValueUtil.isNotEmpty(encoding))
			_encoding = encoding;

		if (mailSenderInfo != null) {

			Properties javaMailProperties = mailSenderInfo.getJavaMailProperties();
			if (javaMailProperties != null)
				mailSender.setJavaMailProperties(javaMailProperties);

			String sendUserId = mailSenderInfo.getSendUserId();
			String sendUserPassword = mailSenderInfo.getSendUserPassword();
			if (ValueUtil.isNotEmpty(sendUserId)) {
				mailSender.setUsername(sendUserId);
			}
			helper.setFrom(mailSender.getUsername());

			if (ValueUtil.isNotEmpty(sendUserPassword)) {
				mailSender.setPassword(sendUserPassword);
			}

			mailSender.setDefaultEncoding(mailSenderInfo.getDefaultEncoding());

			String host = mailSenderInfo.getHost();
			if (ValueUtil.isNotEmpty(host))
				mailSender.setHost(host);

			String port = mailSenderInfo.getPort();
			if (ValueUtil.isNotEmpty(port)) {
				try {

					mailSender.setPort(Integer.parseInt(port));
				} catch (NumberFormatException e) {
					LOGGER.error(ValueUtil.toString(e));
					throw e;
				}
			}

			switch (mailSenderInfo.getType()) {
			case POP3:
				mailSender.getJavaMailProperties().put("mail.pop3.starttls.enable", "true");
				mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "false");
				break;
			case SMPT:
				mailSender.getJavaMailProperties().put("mail.pop3.starttls.enable", "false");
				mailSender.getJavaMailProperties().put("mail.smtp.starttls.enable", "true");
				break;
			}

		}

		String mailContent = mail.getMailContent();

		if (ValueUtil.isEmpty(mailContent)) {
			Template template = null;
			if (ValueUtil.isNotEmpty(mailTemplate)) {
				template = MailUtil.createTemplate(mailTemplate);
			} else {
				template = MailUtil.createTemplate(ClassLoader.getSystemResource("templates/emailtemplate.vm"));
			}
			template.setEncoding(_encoding);
			StringWriter stringWriter = new StringWriter();
			template.merge(velocityContext, stringWriter);
			helper.setText(stringWriter.toString());
		}

		else {
			helper.setText("", mailContent);
		}

		// attachment
		List<AttachmentItem> attachmentItems = mail.getAttachmentItems();
		for (AttachmentItem item : attachmentItems) {
			helper.addAttachment(item.getDisplayName(), item.getDataSource());
		}

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