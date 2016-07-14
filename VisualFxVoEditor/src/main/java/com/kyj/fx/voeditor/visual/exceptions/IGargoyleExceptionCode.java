/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.exceptions
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.exceptions;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public interface IGargoyleExceptionCode {

	/***************************
	 * 에러코드 정의.
	 * 
	 * @author KYJ
	 *
	 ***************************/
	enum ERROR_CODE {

		EMPTY(""),
		/**
		 * File 타입이 일치하지않는경우 .
		 * 
		 * @최초생성일 2016. 7. 14.
		 */
		FILE_DOES_NOT_MATCH("FILE_DOES_NOT_MATCH"),

		/**
		 * 파라미터 값이 빔.
		 * 
		 * @최초생성일 2016. 7. 14.
		 */
		PARAMETER_EMPTY("PARAMETER_EMPTY");

		String code;

		ERROR_CODE(String code) {
			this.code = code;
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * 에러코드 리턴.
	 * 
	 * @return
	 ********************************/
	public ERROR_CODE getErrorCode();

	/********************************
	 * 작성일 :  2016. 7. 14. 작성자 : KYJ
	 *
	 *
	 * @return
	 ********************************/
	public default String getCodeMessage() {
		return getErrorCode().code;
	}
}
