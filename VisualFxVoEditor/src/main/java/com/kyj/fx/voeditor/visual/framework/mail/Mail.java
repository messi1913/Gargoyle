package com.kyj.fx.voeditor.visual.framework.mail;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Mail {

	private StringProperty mailFrom;

	private ObservableList<String> mailTo;

	private StringProperty mailCc;

	private StringProperty mailBcc;

	private StringProperty mailSubject;

	private StringProperty mailContent;

	private StringProperty templateName;

	private StringProperty contentType;

	public Mail() {
		mailTo = FXCollections.observableArrayList();
		mailFrom = new SimpleStringProperty();
		mailCc = new SimpleStringProperty();
		mailBcc = new SimpleStringProperty();
		mailSubject = new SimpleStringProperty();
		mailContent = new SimpleStringProperty();
		templateName = new SimpleStringProperty();
		contentType = new SimpleStringProperty("text/html");
	}

	@Override
	public String toString() {
		StringBuilder lBuilder = new StringBuilder();
		lBuilder.append("Mail From:- ").append(getMailFrom());
		lBuilder.append("Mail To:- ").append(getMailToString());
		lBuilder.append("Mail Cc:- ").append(getMailCc());
		lBuilder.append("Mail Bcc:- ").append(getMailBcc());
		lBuilder.append("Mail Subject:- ").append(getMailSubject());
		// lBuilder.append("Mail Send Date:- ").append(getMailSendDate());
		lBuilder.append("Mail Content:- ").append(getMailContent());
		return lBuilder.toString();
	}

	public String getMailToString() {
		Optional<String> reduce = this.mailTo.stream().reduce((str1, str2) -> str1.concat(",").concat(str2));
		if (reduce.isPresent())
			return reduce.get();
		return "";
	}

	public void setMailTo(List<String> mailTo) {
		mailTo.addAll(mailTo);
	}
	
	public void setMailTo(String[] mailTo) {
		this.mailTo.addAll(Arrays.asList(mailTo));
	}
	
	public void addMailTo(String mailTo){
		this.mailTo.add(mailTo);
	}

	public ObservableList<String> getMailTo() {
		return mailTo;
	}

	public final StringProperty mailFromProperty() {
		return this.mailFrom;
	}

	public final String getMailFrom() {
		return this.mailFromProperty().get();
	}

	public final void setMailFrom(final String mailFrom) {
		this.mailFromProperty().set(mailFrom);
	}

	public final StringProperty mailCcProperty() {
		return this.mailCc;
	}

	public final String getMailCc() {
		return this.mailCcProperty().get();
	}

	public final void setMailCc(final String mailCc) {
		this.mailCcProperty().set(mailCc);
	}

	public final StringProperty mailBccProperty() {
		return this.mailBcc;
	}

	public final String getMailBcc() {
		return this.mailBccProperty().get();
	}

	public final void setMailBcc(final String mailBcc) {
		this.mailBccProperty().set(mailBcc);
	}

	public final StringProperty mailSubjectProperty() {
		return this.mailSubject;
	}

	public final String getMailSubject() {
		return this.mailSubjectProperty().get();
	}

	public final void setMailSubject(final String mailSubject) {
		this.mailSubjectProperty().set(mailSubject);
	}

	public final StringProperty mailContentProperty() {
		return this.mailContent;
	}

	public final String getMailContent() {
		return this.mailContentProperty().get();
	}

	public final void setMailContent(final String mailContent) {
		this.mailContentProperty().set(mailContent);
	}

	public final StringProperty templateNameProperty() {
		return this.templateName;
	}

	public final String getTemplateName() {
		return this.templateNameProperty().get();
	}

	public final void setTemplateName(final String templateName) {
		this.templateNameProperty().set(templateName);
	}

	public final StringProperty contentTypeProperty() {
		return this.contentType;
	}

	public final String getContentType() {
		return this.contentTypeProperty().get();
	}

	public final void setContentType(final String contentType) {
		this.contentTypeProperty().set(contentType);
	}

	public void setMailTo(String email) {
		this.mailTo.add(email);
	}

}