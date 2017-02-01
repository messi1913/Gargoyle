/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bci.view
 *	작성일   : 2016. 5. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.sun.btrace.client.Main;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class BtraceRunner {

	private static Logger LOGGER = LoggerFactory.getLogger(BtraceRunner.class);

	//	private static final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);

	public static void run(ApplicationModel vo, File sampleFileName) {

		if (sampleFileName == null || !sampleFileName.exists()) {
			LOGGER.warn("file does not exists.");
			return;
		}

		String baseDir = System.getProperties().get("user.dir").toString();
		// 프로그램을 실행시키기 위한 기본 환경설정을 처리함.

		System.setProperty("%BTRACE_HOME%", baseDir.concat("\\").concat("btrace"));
		// 실행한 java파일
		// String sampleJavaFileName = sampleFileName;
		// 목적 프로세스id
		Integer pid = vo.getProcessId();

		if (pid == 0 || pid == -1) {
			LOGGER.warn("wrong pid number. exit.");
			return;
		}

		String javaSourcePathName = sampleFileName.getAbsolutePath();
		LOGGER.debug(javaSourcePathName);

		// 목적프로그램 dump파일을 생성할 위치, 필요없을시 주석.
		String btracedumpDir = baseDir.concat("\\btracedump");

		/* -v 디버그모드 -d 덤프 */
		List<String> params = Arrays.asList(/* "-v" , */ "-d", btracedumpDir);
		List<String> asList = Arrays.asList(pid.toString(), javaSourcePathName);
		final List<String> arrayList = new ArrayList<String>(params.size() + asList.size());
		arrayList.addAll(params);
		arrayList.addAll(asList);


		// Btrace라이브러리중 메인함수인 btrace-client.jar파일의 메인함수를 호출한다.

		// Thread thread = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// }
		// });
		// thread.start();

		Service<Void> service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {

				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							// argument 통합.
							String[] arguments = arrayList.stream().toArray(String[]::new);
							String argus = Arrays.toString(arguments);
							LOGGER.debug(String.format("Btrace arguments : %s", argus));

//							Stream.of(arguments).forEach(action);;
							Main.main(arguments);

							Thread.sleep(2000L);
						} catch (Exception e) {
							LOGGER.error(ValueUtil.toString(e));
						}
						return null;
					}
				};
			}
		};

		//		service.setExecutor(newFixedThreadPool);

		service.setOnSucceeded(e -> {
			LOGGER.debug("end ");
		});

		service.start();

	}
}
