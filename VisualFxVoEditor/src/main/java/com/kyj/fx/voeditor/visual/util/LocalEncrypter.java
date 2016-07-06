
/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * 대칭키를 활용한 암호화 처리.
 *
 * 간단한 로컬파일기반 암호화 처리를 하는데 이용
 *
 * @author KYJ
 *
 */
class LocalEncrypter {
	private static String algorithm = "DESede";
	private static Encoder encoder = Base64.getEncoder();
	private static Decoder decoder = Base64.getDecoder();

	/**
	 * 키생성
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static Key generateKey() throws NoSuchAlgorithmException {
		return KeyGenerator.getInstance(algorithm).generateKey();
	}

	/**
	 * 키를 저장
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param file
	 * @param key
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveKey(File file, Key key) throws FileNotFoundException, IOException {
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))) {
			os.writeObject(key);
		}
	}

	/**
	 * 키를 문자열로 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String saveKey(Key key) throws IOException {
		String result = "";
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			try (ObjectOutputStream os = new ObjectOutputStream(out)) {
				os.writeObject(key);
			}
			byte[] byteArray = out.toByteArray();
			result = encoder.encodeToString(byteArray);
		}
		return result;
	}

	/**
	 * 키를 읽어옴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Key readKey(String source) throws Exception {
		byte[] decode = decoder.decode(source);
		try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(decode))) {
			return (Key) is.readObject();
		}
	}

	/**
	 * 키를 읽어옴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Key readKey(File file) throws Exception {
		try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(file))) {
			return (Key) is.readObject();
		}
	}

	/**
	 * 암호화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param input
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String input, Key key) throws Exception {
		byte[] _encrypt = _encrypt(input, key);
		return new String(encoder.encode(_encrypt));
	}

	/**
	 * 복호화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param encodeString
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encodeString, Key key) throws Exception {
		byte[] decode = decoder.decode(encodeString);
		return _decrypt(decode, key);
	}

	/**
	 * 암호화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param input
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static byte[] _encrypt(String input, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] inputBytes = input.getBytes();
		return cipher.doFinal(inputBytes);
	}

	/**
	 * 복호화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param encryptionBytes
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static String _decrypt(byte[] encryptionBytes, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
		String recovered = new String(recoveredBytes);
		return recovered;
	}

}