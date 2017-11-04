/********************************
 *	프로젝트 : gargoyle-encryp
 *	패키지   : com.kyj.utils
 *	작성일   : 2017. 11. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.utils;

/**
 * 
 * @author KYJ
 *
 */
public class GargoyleEncrypConverter implements IEncrypConvert {

	@Override
	public String encryp(String message) throws Exception {
		return EncrypUtil.encryp(message);
	}

	@Override
	public String decryp(String message) throws Exception {
		return EncrypUtil.decryp(message);
	}

}
