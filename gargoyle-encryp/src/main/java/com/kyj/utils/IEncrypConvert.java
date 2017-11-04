/********************************
 *	프로젝트 : gargoyle-encryp
 *	패키지   : com.kyj.utils
 *	작성일   : 2017. 11. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.utils;

/**
 * @author KYJ
 *
 */
public interface IEncrypConvert {

	/**
	 * 암호화
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 4.
	 * @param message
	 * @return
	 */
	public String encryp(String message) throws Exception;

	/**
	 * 복호화
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 4.
	 * @param message
	 * @return
	 */
	public String decryp(String message) throws Exception;
}
