/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.mail
 *	작성일   : 2017. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.mail;

import java.util.Collections;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.mail.Mail;
import com.kyj.fx.voeditor.visual.framework.thread.DemonThreadFactory;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.MailUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.concurrent.Task;

/**
 * @author KYJ
 *
 */
public class SendMailHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SendMailHandler.class);
	private MailViewComposite mailComposite;

	private Consumer<Exception> errorHandler = ex -> {
		LOGGER.error(ValueUtil.toString(ex));
	};

	public SendMailHandler(MailViewComposite mailComposite) {
		this.mailComposite = mailComposite;
	}

	/**
	 * 메일보기
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 17.
	 */
	public void sendMail() {

		FxUtil.showLoading(new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				Mail mail = mailComposite.getMail();

				try {
					MailUtil.sendMail(mail, Collections.emptyMap());
				} catch (Exception e) {
					if (errorHandler != null)
						errorHandler.accept(e);
				}

				return null;
			}
		});

	}
}
