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
	public byte[] encryp(byte[] message) throws Exception;

	/**
	 * 복호화
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 4.
	 * @param message
	 * @return
	 */
	public byte[] decryp(byte[] message) throws Exception;
}
