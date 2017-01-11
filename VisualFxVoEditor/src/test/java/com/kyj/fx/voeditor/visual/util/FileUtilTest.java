/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 3. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.junit.Assert;
import org.junit.Test;

import com.sun.btrace.BTraceUtils.Strings;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class FileUtilTest {

	/**
	 * 루트 경로를 기준으로 someDir의 상대경로를 구한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 31.
	 */
	@Test
	public void relativizeTest() {
		File rootDir = new File("C:\\");
		File someDir = new File("C:\\NVIDIA\\DisplayDriver");

		{
			Path relativize = rootDir.toPath().relativize(someDir.toPath());

			System.out.println(relativize.toString());
			Assert.assertEquals("NVIDIA\\DisplayDriver", relativize.toString());
		}

		{
			Path relativize = someDir.toPath().relativize(rootDir.toPath());
			System.out.println(relativize.toString());
			Assert.assertEquals("..\\..", relativize.toString());
		}

	}

	@Test
	public void watchTest() throws IOException {
		File file = new File("c:\\someDir");
		file.mkdirs();

		WatchService newWatchService = FileSystems.getDefault().newWatchService();
		WatchKey register = file.toPath().register(newWatchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		
		System.out.println("Watch Service Registered ..");

		while (true) {
			try {
				System.out.println("start");
				WatchKey key = newWatchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path fileName = ev.context();

					System.out.println(kind.name() + ": " + fileName);

					if (key == ENTRY_MODIFY && fileName.toString().equals("DirectoryWatchDemo.java")) {
						System.out.println("My source file has changed!!!");
					}
				}
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			} catch (InterruptedException e) {
				break;
			}
		}

		System.out.println("end ");
	}

	@Test
	public void asynchReadTest() throws IOException, InterruptedException {
		FileUtil.asynchRead(new File("pom.xml").toPath(), b -> {

			String x = new String(b);
			System.out.println(x);
			return x;
		});

		Thread.sleep(2000);
	}

	@Test
	public void hexRead() throws IOException {
		{
			byte[] readAllBytes = Files.readAllBytes(new File("nas.docx").toPath());

			for (int i = 0; i < readAllBytes.length; i++) {

				if (i % 16 == 0)
					System.out.println("");

				byte b = readAllBytes[i];
				System.out.print(Strings.toHexString(b));

			}
		}

		System.out.println("##############################################################");
		System.out.println("##############################################################");
		System.out.println("##############################################################");

		{
			byte[] readAllBytes = Files.readAllBytes(new File("unnas.docx").toPath());

			for (int i = 0; i < readAllBytes.length; i++) {

				if (i % 16 == 0)
					System.out.println("");

				byte b = readAllBytes[i];
				System.out.print(Strings.toHexString(b));

			}
		}
	}
}
