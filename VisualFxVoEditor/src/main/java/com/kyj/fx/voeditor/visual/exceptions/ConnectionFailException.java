/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.exceptions
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.exceptions;


/**
 * 데이터베이스 접속 불가 Exception
 * 
 * @author KYJ
 *
 */
public class ConnectionFailException extends GagoyleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionFailException() {
		super();

	}

	public ConnectionFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public ConnectionFailException(String message, Throwable cause) {
		super(message, cause);

	}

	public ConnectionFailException(String message) {
		super(message);

	}

	public ConnectionFailException(Throwable cause) {
		super(cause);
	}

}
