/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.stream
 *	작성일   : 2016. 5. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.stream;

import java.io.IOException;
import java.io.OutputStream;

/***************************
 *
 * 스트링 스트림.
 *
 * @author KYJ
 *
 ***************************/
public class StringOutputStream extends OutputStream {

	StringBuilder mBuf;

	public StringOutputStream() {
		mBuf = new StringBuilder();
	}

	public String getString() {
		return mBuf.toString();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void write(int b) throws IOException {
		mBuf.append((char) b);
	}
}
