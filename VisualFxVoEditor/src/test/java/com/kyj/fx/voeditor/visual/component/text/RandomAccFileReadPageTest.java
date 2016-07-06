/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 2. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class RandomAccFileReadPageTest {
	RandomAccessFile file;
	String fileName = "C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\VisualFxVoEditor\\log\\fxeditor.log.2016-02-15";
	int seekSize = 1024 * 50;
	long size = 0;
	long totalPage = 0;

	@Before
	public void setting() throws IOException {
		file = new RandomAccessFile(fileName, "r");
		size = file.length();
		totalPage = (size / seekSize) + 1;
	}

	@Test
	public void test() {
		System.out.println("########################################### page 1");
		readPage(0);
		System.out.println("########################################### page 2");
		readPage(1);
	}

	public void readPage(int page) {
		try {
			int iwantReadPage = page;
			byte[] data = new byte[seekSize];

			file.seek(iwantReadPage * seekSize);
			file.read(data);
			System.out.printf("pointer : %02d str : %s \n", file.getFilePointer(), new String(data).trim());
			System.out.println(totalPage);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void close() {
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
