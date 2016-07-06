/**
 * 
 */
package com.kyj.fx.voeditor.visual.framework.mail;

/**
 * @author user
 *
 */
public class SenderMailInfo {
	/**
	 * 메일 전송자에 대한 계정입력. ex) 네이버id@naver.com
	 */
	private String sendUserId;
	/**
	 * 메일전송자의 비밀번호
	 */
	private String sendUserPassword;

	/**
	 * @return the sendUserId
	 */
	public String getSendUserId() {
		return sendUserId;
	}

	/**
	 * @param sendUserId
	 *            the sendUserId to set
	 */
	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	/**
	 * @return the sendUserPassword
	 */
	public String getSendUserPassword() {
		return sendUserPassword;
	}

	/**
	 * @param sendUserPassword
	 *            the sendUserPassword to set
	 */
	public void setSendUserPassword(String sendUserPassword) {
		this.sendUserPassword = sendUserPassword;
	}

}
