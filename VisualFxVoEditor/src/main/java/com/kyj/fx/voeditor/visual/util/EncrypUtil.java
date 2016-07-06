/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.security.Key;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class EncrypUtil {

	private static String keySource = "rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AAZERVNlZGV1cgACW0Ks8xf4BghU4AIAAHhwAAAAGJK8H55rwSanxHX0vweneQcH5hqF2hZ5FnQAA1JBV35yABlqYXZhLnNlY3VyaXR5LktleVJlcCRUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAGU0VDUkVU";
	private static Key key;

	static {
		try {
			key = LocalEncrypter.readKey(keySource);
		} catch (Exception e) {
			try {
				key = LocalEncrypter.readKey(keySource);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public static String encryp(String str) throws Exception {
		return LocalEncrypter.encrypt(str, key);
	}

	
	public static String decryp(String str) throws Exception {
		return LocalEncrypter.decrypt(str, key);
	}

}
