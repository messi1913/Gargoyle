/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.console
 *	작성일   : 2016. 3. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class SystemConsole {

	private static ReadOnlyConsole console = new ReadOnlyConsole();

	static PrintStream out;

	static {
		try {

			String ENC = System.getProperty("sun.stdout.encoding", "UTF-8");
			out = new PrintStream(new ConsoleOutputStream(), true, ENC);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static ReadOnlyConsole getInstance() throws IOException {

		System.setOut(out);
		System.setErr(out);

		return console;
	}

	static class ConsoleOutputStream extends ByteArrayOutputStream {
		//		console.appendText((char) b);
		public ConsoleOutputStream() {
			super(4096);
		}

		/* (non-Javadoc)
		 * @see java.io.ByteArrayOutputStream#write(byte[], int, int)
		 */
		@Override
		public synchronized void write(byte[] b, int off, int len) {
			super.write(b, off, len);
			console.appendText(new String(b), false);
		}

		/* (non-Javadoc)
		 * @see java.io.ByteArrayOutputStream#write(int)
		 */
		@Override
		public synchronized void write(int b) {
			// TODO Auto-generated method stub
			super.write(b);
			console.appendText((char) b);
		}

	}

	/**
	 * 스트림 콘솔 모드로 초기화
	 * @throws IOException
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 25.
	 */
	public void close() throws IOException {
		console.close();
		System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
}
