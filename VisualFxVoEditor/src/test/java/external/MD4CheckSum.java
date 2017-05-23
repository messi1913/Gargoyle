/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2017. 5. 22.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.pdfbox.io.RandomAccessFile;

/**
 * @author KYJ
 *
 */
public class MD4CheckSum {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 5. 22. 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		File file = new File("C:\\Users\\KYJ\\Music\\뜸부기\\다모뮤직(damo.kr) 멜론(Melon) 5월 1일 실시간 Top100", "015 볼빨간사춘기 - 좋다고 말해.mp3");

		RandomAccessFile accessFile = new RandomAccessFile(file, "r");
		byte[] b = new byte[3];
		if (accessFile.available() >= 3 && (accessFile.read(b) != -1) && "ID3".equals(new String(b))) {
			accessFile.seek(3);
			b = new byte[4];
			accessFile.read(b, 0, 4);
			int id3Size = b[0] << 21 | b[1] << 14 | b[2] << 7 | b[3];
			System.out.println(id3Size);
			accessFile.seek(id3Size + 10);
		} else {
			accessFile.seek(0);
		}

		for (int i = 0; i < 500000; i++) {
			int a = accessFile.read();
			if (a == 255) {
				a = accessFile.read();
				if ((a >> 5) == 7) {
					accessFile.seek(accessFile.getPosition() - 2);
				}
			}
		}

		b = new byte[163840];
		accessFile.read(b, 0, 163840);
		accessFile.close();
		System.out.println(testMD5(b));
		System.out.println("e27b9cde01eac6e77caba1ba599f67d7");

	}

	public static String testMD5(String str) {
		return testMD5(str.getBytes());
	}

	public static String testMD5(byte[] b) {
		String MD5 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(b);
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			//			MD5 = new String(byteData, "UTF-8");
			MD5 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			MD5 = null;
		}

		return MD5;

	}

}
