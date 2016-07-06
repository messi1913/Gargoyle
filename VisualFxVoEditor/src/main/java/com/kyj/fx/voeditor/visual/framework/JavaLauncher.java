/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 5. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.MonitorProperties;
import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;

/**
 * @author KYJ
 *
 */
public class JavaLauncher {

	private static Logger LOGGER = LoggerFactory.getLogger(JavaLauncher.class);

	/**
	 * 프로그램 실행시 arguement 파라미터를 정의해줌. 즉 실행되는 프로세스는 아래키에 해당하는 값을 Virtual-Machine
	 * 인자로 넘겨준다.
	 *
	 * 배치스케줄러에서 실행되는 파라미터라는 의미를 부여하기 위한 값으로 중요.
	 *
	 * @최초생성일 2016. 4. 7.
	 */
	private static final String DEXEC_OWNER_SOS_BATCH_SCHEDULER = MonitorProperties.getInstance()
			.get(MonitorProperties.MONITOR_HIT_ARGUMENT);

	/**
	 * 실행되는 프로세서가 필요한 클래스패스 정보를 넘겨준다.
	 *
	 * @최초생성일 2016. 4. 7.
	 */
	public static final String JAVA_CLASS_PATH = "java.class.path";

	private JavaLauncher() {

	}

	public static void executeProcess(String fullClassName) throws Exception {
		String classPath = System.getProperty(JAVA_CLASS_PATH);
		executeProcess(fullClassName, classPath);
	}

	public static void executeProcess(String fullClassName, String classPath) throws Exception {
		List<String> argument = extractAruments(fullClassName, classPath);
		RuntimeClassUtil.exe(argument);
	}

	private static List<String> extractAruments(String fullClassName, String classPath) {
		List<String> argument = new ArrayList<String>();
		argument.add("java");
		argument.add("-classpath");
		argument.add(classPath);
		argument.add(fullClassName);
		argument.add(DEXEC_OWNER_SOS_BATCH_SCHEDULER);
		LOGGER.debug("Command Arugments....");
		Optional<String> reduce = argument.stream().reduce((str, str2) -> str.concat(" ").concat(str2));
		reduce.ifPresent(LOGGER::debug);
		return argument;
	}

	/**
	 * 프로세스를 실행시키며 거기서 발생되는 output은 파일로 남김.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 6.
	 * @param outputLog
	 * @param fullClassName
	 * @return
	 * @throws Exception
	 */
	public static int executeAndWriteLogger(File outputLog, String fullClassName) throws Exception {
		List<String> argument = extractAruments(fullClassName, System.getProperty(JAVA_CLASS_PATH));
		return RuntimeClassUtil.exe(argument, outputLog);
	}

	/**
	 * 프로세스를 실행시키며 거기서 발생되는 output은 파일로 남김.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 6.
	 * @param outputLog
	 * @param fullClassName
	 * @param classPath
	 * @return
	 * @throws Exception
	 */
	public static void executeAndWriteLogger(File outputLog, String fullClassName, String classPath) throws Exception {
		List<String> argument = extractAruments(fullClassName, classPath);
		RuntimeClassUtil.exe(argument, outputLog);
	}

	public static void taskkillProcess(Integer processId) throws Exception {
		killProcess(processId, false);
	}

	public static void taskFourceKillProcess(Integer processId) throws Exception {
		killProcess(processId, true);
	}

	private static void killProcess(Integer processId, boolean isForcue) throws Exception {
		if (processId == null || processId == -1 || processId == 0)
			return;
		// return -1;

		List<String> argument = new ArrayList<String>();
		argument.add("taskkill");
		if (isForcue)
			argument.add("/F");
		argument.add("/PID");
		argument.add(String.valueOf(processId.intValue()));
		RuntimeClassUtil.exe(argument);
	}

}
