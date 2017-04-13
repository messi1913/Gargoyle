/**
 * KYJ
 * 2015. 9. 11.
 */
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.framework.mail.MailConst;
import com.kyj.fx.voeditor.visual.framework.mail.Mailer;
import com.kyj.fx.voeditor.visual.framework.mail.SenderMailInfo;

/**
 * @author KYJ
 *
 */
public class MailUtil {
	private static Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

	private MailUtil() {

	}

	/**
	 * 메일전송
	 *
	 * @Date 2015. 9. 13.
	 * @param mail
	 *            메일을 보내기 위한 정보가 담긴 객체
	 * @param mailContent
	 *            메일템플릿안에 들어갈 내용이 담기는 정보.
	 * @throws Exception
	 * @User KYJ
	 */
	public static boolean sendMail(List<Mail> mails, Map<String, Object> mailContent) throws Exception {
		try {
			Mailer bean = BeanUtil.getBean("mailer", Mailer.class);
			VelocityContext merge = toVelocityContext(mailContent);
			SenderMailInfo sender = BeanUtil.getBean("mailSenderInfo", SenderMailInfo.class);
			bean.sendMail(sender, mails, merge);
			
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			return false;
		}
		return true;
	}

	/**
	 * 메일전송
	 *
	 * @Date 2015. 9. 13.
	 * @param mail
	 *            메일을 보내기 위한 정보가 담긴 객체
	 * @param mailContent
	 *            메일템플릿안에 들어갈 내용이 담기는 정보.
	 * @throws Exception
	 * @User KYJ
	 */
	public static boolean sendMail(Mail mail, Map<String, Object> mailContent) throws Exception {
		SenderMailInfo sender = BeanUtil.getBean("mailSenderInfo", SenderMailInfo.class);
		return sendMail(sender, mail, mailContent);
	}

	/**
	 * 메일전송
	 *
	 * @Date 2015. 9. 13.
	 * @param mail
	 *            메일을 보내기 위한 정보가 담긴 객체
	 * @param mailContent
	 *            메일템플릿안에 들어갈 내용이 담기는 정보.
	 * @throws Exception
	 * @User KYJ
	 */
	public static boolean sendMail(SenderMailInfo sender, Mail mail, Map<String, Object> mailContent) throws Exception {
		try {
			Mailer bean = BeanUtil.getBean("mailer", Mailer.class);

			VelocityContext merge = toVelocityContext(mailContent);
			bean.sendMail(sender, mail, merge);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			return false;
		}
		return true;
	}

	/**
	 * Map객체를 VelocityContext 객체로 변환.
	 *
	 * @Date 2015. 9. 13.
	 * @param mailContent
	 * @return
	 * @User KYJ
	 */
	private static VelocityContext toVelocityContext(Map<String, Object> mailContent) {
		Iterator<String> iterator = mailContent.keySet().iterator();
		VelocityContext velocityContext = new VelocityContext();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = mailContent.get(key);

			velocityContext.put(key, value);
		}
		return velocityContext;
	}

	/**
	 * 파일로부터 템플릿 정보를 얻어온다.
	 *
	 * @Date 2015. 9. 13.
	 * @param templateFileName
	 * @return
	 * @throws Exception
	 * @User KYJ
	 */
	public static Template getTemplateFromFile(final String templateFileName) throws Exception {
		String readFileToString = "";
		if(templateFileName.startsWith("classpath:"))
		{
			String res = templateFileName.replace("classpath:", "");
			InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(res);
			readFileToString = ValueUtil.toString(resourceAsStream);
		}
		else
			readFileToString = FileUtils.readFileToString(new File(templateFileName));
		
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(readFileToString);
		SimpleNode node = runtimeServices.parse(reader, templateFileName);
		Template template = new Template();
		template.setRuntimeServices(runtimeServices);
		template.setData(node);
		template.initDocument();
		return template;
	}

	/**
	 * 템플릿 정보를 얻어온다. templateName에 정보가 없다면 defaultTemplateName정보를 사용함.
	 *
	 * @Date 2015. 9. 13.
	 * @param velocityEngine
	 * @param templateName
	 * @param defaultTemplateName
	 * @return
	 * @User KYJ
	 */
	public static Template getTemplate(VelocityEngine velocityEngine, String templateName, String defaultTemplateName) {
		return getTemplate(velocityEngine, templateName, defaultTemplateName, MailConst.MAILER_DEFAULT_ENCODING);
	}

	/**
	 * 템플릿 정보를 얻어온다. templateName에 정보가 없다면 defaultTemplateName정보를 사용함.
	 *
	 * @Date 2015. 9. 13.
	 * @param velocityEngine
	 * @param templateName
	 * @param defaultTemplateName
	 * @param encoding
	 * @return
	 * @User KYJ
	 */
	public static Template getTemplate(VelocityEngine velocityEngine, String templateName, String defaultTemplateName,
			String encoding) {
		Template template = null;
		if (templateName == null) {
			template = velocityEngine.getTemplate("./templates/" + defaultTemplateName, encoding);
		} else {
			template = velocityEngine.getTemplate("./templates/" + velocityEngine, encoding);
		}
		return template;
	}
}
