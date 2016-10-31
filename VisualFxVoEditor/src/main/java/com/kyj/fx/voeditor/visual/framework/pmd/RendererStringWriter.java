/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pmd
 *	작성일   : 2016. 10. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pmd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author KYJ
 *
 */
public class RendererStringWriter implements RenderWriterable {

	private StringWriter stringWriter = new StringWriter();

	public Writer getWriter() {
		return stringWriter;
	}

	public String getResultString() {
		return this.stringWriter.getBuffer().toString();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 31.
	 */
	public void close() {

		try {
			if (this.stringWriter != null)
				this.stringWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
