/**
 *
 */
package com.kyj.fx.voeditor.visual.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.mail.Mail;
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
		mail.setMailTo(new String[] { "" });
		Map<String, Object> velocityContext = new HashMap<>();

		velocityContext.put("seq", "667");
		velocityContext.put("date", "2015-09-12");
		velocityContext.put("numbers", "테스트번호입니다.");

		MailUtil.sendMail(Arrays.asList(mail), velocityContext);
	}
}
