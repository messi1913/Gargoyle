/********************************
 *	프로젝트 : system.monitor
 *	패키지   : com.kyj.monitor
 *	작성일   : 2016. 4. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * 배치 모니터링에 필요한 설정값 로드
 *
 * @author KYJ
 *
 */
public class MonitorProperties {

	private static MonitorProperties loader;
	private Properties properties;

	/**
	 * 배치모니터링에 필요한 필수 파라미터값을정의하는 키.
	 * 
	 * @최초생성일 2016. 4. 5.
	 */
	public static final String MONITOR_ARGUMENT = "monitor.argument";

	/**
	 * 프로그램 실행시 arguement 파라미터를 정의해줌.
	 * 즉 실행되는 프로세스는 아래키에 해당하는 값을 Virtual-Machine 인자로 넘겨준다.
	 * 
	 * 배치스케줄러에서 실행되는 파라미터라는 의미를 부여하기 위한 값으로 중요.
	 * 
	 * @최초생성일 2016. 4. 7.
	 */
	public static final String MONITOR_HIT_ARGUMENT = "monitor.hit.argument";

	/**
	 * 배치모니터링 UI에 대한 메인 stage가 종료되면 실행중이던 모든 배치잡을 종료시킬지 유무.
	 * 
	 * @최초생성일 2016. 4. 5.
	 */
	public static final String MONITOR_UI_CLOSED_CLOSE_ALL_THREAD_YN = "monitor.ui.exit.close.all.thread.yn";

	/**
	 * Runtime에 실행되는 프로세스가 남기는 output message를 로그파일로 남기기 위해 사용하며 이때 남겨진 로그가 아래
	 * 변수키에 해당하는 값의 위치로 로그정보가 남는다.
	 * 
	 * @최초생성일 2016. 4. 6.
	 */
	public static final String SCHEDULER_EXEC_RESULT_LOGGER_DIR = "scheduler.exec.result.logger.dir";

	/**
	 * 스케줄러 차트에서 보여줄 수 있는 아이템 수.
	 * 
	 * 이 숫자에 따라 차트의 시리즈아이템수가 유지된다.
	 * 
	 * @최초생성일 2016. 4. 7.
	 */
	public static final String CHART_SHOWING_ITEM_COUNT = "chart.showing.item.count";
	/**
	 * 스케줄러에서 관리하는 스레드 풀의 수를 정의함.
	 * 
	 * @최초생성일 2016. 4. 7.
	 */
	public static final String MONITOR_THREAD_POOL_COUNT = "monitor.thread.pool.count";

	/**
	 * 스케줄러에서 스레드 생성시 관리하는 스레드이름을 정의함.
	 * 
	 * @최초생성일 2016. 4. 7.
	 */
	public static final String MONITOR_THREAD_GROUP_NAME = "monitor.thread.group.name";

	public static final String APP_CSS_FILE_NAME = "app.css.file.name";
	/**
	 * 배치 모니터링 설정파일
	 * 
	 * @최초생성일 2016. 4. 5.
	 */
	private static String FILE_NAME = "META-INF/scheduler/properties/scheduler.properties";

	private String[] baseKeys = { MONITOR_ARGUMENT };

	public static MonitorProperties getInstance() {
		if (loader == null) {
			loader = new MonitorProperties();
			loader.initialize();
		}

		return loader;
	}

	private MonitorProperties() {

	}

	private void initialize() {
		properties = new Properties();

		FileInputStream inStream = null;
		try {

			ClassLoader classLoader = MonitorProperties.class.getClassLoader();
			properties.load(classLoader.getResourceAsStream(FILE_NAME));
			baseKeyLoad(properties);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 기본키값이 존재하지않으면 로드
	 *
	 * @Date 2015. 10. 15.
	 * @param properties
	 * @User KYJ
	 */
	private void baseKeyLoad(Properties properties) {
		for (String key : baseKeys) {
			if (properties.containsKey(key))
				continue;
			properties.put(key, "");
		}
	}

	public synchronized void putAll(Map<String, Object> bufMap) {
		properties.putAll(bufMap);
		store();
	}

	public synchronized void putAll(Properties prop) {
		prop.entrySet().stream().forEach(e -> {
			properties.put(e.getKey(), e.getValue());
		});

		store();
	}

	public synchronized void put(String key, String value) {
		properties.put(key, value);
		store();
	}

	/**
	 * 파일에 적용처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 4.
	 */
	private void store() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(FILE_NAME);
			properties.store(out, "User Conf...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String get(String key) {
		String property = properties.getProperty(key);
		return property;
	}

	public int getInteger(String key) {
		String property = properties.getProperty(key);
		return Integer.parseInt(property);
	}

	public Enumeration<Object> keySet() {
		return properties.keys();
	}

	public Set<Entry<Object, Object>> getEntry() {
		return properties.entrySet();
	}

	public String getFileName() {
		return FILE_NAME;
	}
}
