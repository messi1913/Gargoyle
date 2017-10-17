/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.mail
 *	작성일   : 2017. 10. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.mail;

import java.util.List;

import com.kyj.fx.voeditor.visual.framework.mail.Mail;

/**
 * @author KYJ
 *
 */
public class RecipientHandler {

	private MailViewComposite mailComposite;

	public RecipientHandler(MailViewComposite mailComposite) {
		this.mailComposite = mailComposite;
	}

	public void build() {
		Mail mail = this.mailComposite.getMail();
		List<MailReceiver> recipients = this.mailComposite.getRecipients();

		for (MailReceiver r : recipients) {
			switch (r.getType()) {
			case "CC":
				mail.getMailCc().add(r.getEmail());
				break;
			case "BCC":
				mail.getBcc().add(r.getEmail());
				break;
			default:

				mail.getMailTo().add(r.getEmail());
				break;
			}

		}

	}
}
