/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 5. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author KYJ
 *
 */
public class IOUtil {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 5. 13.
	 * @return
	 */
	public static java.io.Reader emptyReader() {
		return new java.io.Reader() {

			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				return -1;
			}

			@Override
			public void close() throws IOException {
			}

		};
	}

	public static BufferedReader emptyBufferedReader() {
		return new BufferedReader(emptyReader());
	}

	public static InputStream emptyInputStream() {
		return new InputStream() {

			@Override
			public int read() throws IOException {
				return -1;
			}
		};
	}

	public static BufferedReader toBufferedReader(InputStream is) throws UnsupportedEncodingException {
		return new BufferedReader(new InputStreamReader(is, "UTF-8"));
	}

	public static BufferedReader toBufferedReader(InputStream is, String charset) throws UnsupportedEncodingException {
		return new BufferedReader(new InputStreamReader(is, charset));
	}
}
