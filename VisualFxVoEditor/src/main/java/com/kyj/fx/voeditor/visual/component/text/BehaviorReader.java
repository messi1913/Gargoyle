/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * behavior 파일을 읽어오는 기능을 수행 <br/>
 * 
 * @author KYJ
 *
 */
public class BehaviorReader {

	private File f;

	/**
	 * wib or bfm 파일 <br/>
	 * 
	 * @param f
	 */
	public BehaviorReader(File f) {
		this.f = f;
	}

	public String readBehavior() {
		return this.readBehavior(this.f);
	}

	private String readBehavior(File wib) {

		try (FileInputStream is = new FileInputStream(wib)) {

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				int c = 0;
				while ((c = is.read()) != -1) {
					out.write(c);
				}

				String decompress = decompress(out.toByteArray());
				return decompress;
			} catch (DataFormatException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected String decompress(byte[] compressed) throws DataFormatException, IOException {
		Inflater decompresser = new Inflater(true);

		byte[] result = new byte[1024];

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			decompresser.setInput(compressed);
			while (!decompresser.finished()) {
				int count = decompresser.inflate(result);
				out.write(result, 0, count);
			}
			decompresser.end();
			return out.toString();
		}

	}
}
