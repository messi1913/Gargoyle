/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 5. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.poi.util.IOUtils;

import com.sun.star.uno.RuntimeException;

/**
 * @author KYJ
 *
 */
public class MimeHelper {

	/**
	 *
	 * File을 MIME (HTML기반) 이미지로 교체함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 31.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String imageTo(File file) {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return imageTo(fileInputStream);
		} catch (Exception e) {
			throw new RuntimeException("can't converting file.");
		}
	}

	/**
	 * Stream 을 MIME (HTML기반) 이미지로 교체함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 31.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String imageTo(InputStream is) throws IOException {
		byte[] byteArray = IOUtils.toByteArray(is);

		StringBuilder b = new StringBuilder();
		// BASE64로 컨버팅.
		byteArray = Base64.getMimeEncoder().encode(byteArray);

		b.append(new String("<img src=\"data:image/jpg;base64,"));
		b.append(new String(byteArray));
		b.append(new String("\" alt=\"\" width=\"683\" height=\"420\" />"));
		return b.toString();
	}

	/**
	 * Str을 MIME (HTML기반) 이미지로 교체함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 5. 31.
	 * @param str
	 * @return
	 */
	public static String stringTo(String str) {
		return new StringBuffer().append("<p>").append(str).append("<p>").toString();
	}
}
