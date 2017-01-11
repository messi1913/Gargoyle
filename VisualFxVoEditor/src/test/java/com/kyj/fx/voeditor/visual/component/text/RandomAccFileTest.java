/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 2. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class RandomAccFileTest {



	@Test
	public void readTest() {
		String fileName = "C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\VisualFxVoEditor\\log\\fxeditor.log.2016-02-15";
		// 읽어들일 사이즈
		int seekSize = 1024;
		long size = 0;
		long totalPage = (size / 1024) + 1;
		int userWantRreadPage = 1;
		try {
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			// String readLine = file.readLine();
			// 문자열 총 길이

			System.out.println("total length : " + file.length() + "\n");

			byte[] data = null;

			// 루프 사이즈 = 총길이/seekSize + (총길이%seekSize의 나머지가 0이면 0을 반환 0이아니면 1을
			// 반환)
			size = file.length() / seekSize + (file.length() % seekSize == 0 ? 0 : 1);

			for (int i = 0; i < size; i++) {
				data = new byte[seekSize];

				// seekSize 만큼 증가
				file.seek(i * seekSize);
				file.read(data);

				// 바이트 데이터를 문자열로 변환(trim()을 사용해 공백을 제거)
				System.out.printf("pointer : %02d str : %s \n", file.getFilePointer(), new String(data).trim());
			}

			// 파일 닫기
			file.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void logger_reader() {

		String fileName = new File("hs_err_pid4012.log").getAbsolutePath();
		// 읽어들일 사이즈
		int seekSize = 1024;
		long size = 0;
		int totalPage = 0;
		try {
			RandomAccessFile file = new RandomAccessFile(fileName, "rw");
			// String readLine = file.readLine();
			// 문자열 총 길이

			System.out.println("total length : " + file.length() + "\n");

			byte[] data = null;

			// 루프 사이즈 = 총길이/seekSize + (총길이%seekSize의 나머지가 0이면 0을 반환 0이아니면 1을
			// 반환)
			size = file.length() / seekSize + (file.length() % seekSize == 0 ? 0 : 1);

			totalPage  = (int) (file.length() / seekSize)  -  (file.length() % seekSize >  0 ? 0 : 1);
			System.out.println("total page : " + totalPage);

			data = new byte[seekSize];


			// seekSize 만큼 증가
			file.seek( totalPage * seekSize  );

			file.read(data);

			// 바이트 데이터를 문자열로 변환(trim()을 사용해 공백을 제거)
			long filePointer = file.getFilePointer();
			System.out.printf("pointer : %02d str : %s \n", filePointer, new String(data).trim());



			//다음 읽기

			file.read(data);
			filePointer = file.getFilePointer();
			System.out.printf("pointer : %02d str : %s \n", filePointer, new String(data).trim());

			// 파일 닫기
			file.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void readPage() {
		String fileName = "C:\\Users\\KYJ\\JAVA_FX\\gagoyleWorkspace\\VisualFxVoEditor\\log\\fxeditor.log.2016-02-15";
		int seekSize = 1024 * 200;
		long size = 0;
		long totalPage = 0;
		int iwantReadPage = 0;
		try {
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			size = file.length();
			totalPage = (size / seekSize) + 1;

			byte[] data = new byte[seekSize];
			file.seek(iwantReadPage * seekSize);
			int read = file.read(data);

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

}
