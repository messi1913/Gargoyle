/**
 * KYJ
 * 2015. 10. 12.
 */
package com.kyj.fx.voeditor.visual.momory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.dock.tab.DockTabPane;
import com.kyj.fx.voeditor.visual.loder.DynamicClassLoader;
import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.main.layout.SystemLayoutViewController;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * 프로그램 Runtime시에 프로그램이 가동되기위해 필요한 변수들을 기억한다.
 *
 * @author KYJ
 *
 */
public class SharedMemory {
	private static Logger LOGGER = LoggerFactory.getLogger(SharedMemory.class);

	private SharedMemory() {

	}

	/**
	 * 메인스테이지
	 *
	 * @최초생성일 2015. 11. 27.
	 */
	private static Stage primaryStage;
	/**
	 * 워크스페이스 탭
	 *
	 * @최초생성일 2015. 11. 27.
	 */
	private static DockTabPane tabWorkspace;
	/**
	 * 시스템 레이아웃뷰 컨트롤러
	 *
	 * @최초생성일 2015. 11. 27.
	 */
	private static SystemLayoutViewController systemLayoutViewController;

	/**
	 * 로드된 클래스정보
	 *
	 * @최초생성일 2015. 11. 27.
	 */
	private static List<ProjectInfo> listClases;

	private static List<ProjectInfo> listSources;

	private static Object lock = new Object();

	public final static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static synchronized void setPrimaryStage(Stage primaryStage) {
		SharedMemory.primaryStage = primaryStage;
	}

	public static synchronized List<ProjectInfo> loadClasses() {
		return loadClasses(false);
	}

	public static synchronized List<ProjectInfo> loadSources() {
		return loadSources(false);
	}

	/**
	 * 특정조건에 일치하는 클래스 파일들을 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 17.
	 * @param packageNamePattern
	 * @return
	 */
	public static synchronized <T> List<Class<?>> findClasses(Predicate<String> packageNamePattern, Predicate<Class<?>> classInfoPattern) {
		List<ProjectInfo> loadClasses = loadClasses(false);

		List<Class<?>> collect = loadClasses.stream().flatMap(pro -> {
			return pro.getClasses().stream();
		}).filter(packageNamePattern).map(className -> {
			try {
				return Class.forName(className);
			} catch (Exception e) {
			}
			return null;
		}).filter(rslt -> rslt != null).filter(classInfoPattern).collect(Collectors.toList());
		return collect;
	}

	@SuppressWarnings("unchecked")
	public static synchronized List<ProjectInfo> loadClasses(boolean reflesh) {

		String classDirName = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		try {
			File file = new File(classDirName);

			if (!file.exists()) {
				throw new RuntimeException("base dir does not exists.");
			}

			if (listClases == null || reflesh)
				listClases = DynamicClassLoader.listClases(classDirName);
		} catch (Exception e) {
			LOGGER.error("exception %s", e.toString());
		}

		if (listClases == null)
			return Collections.EMPTY_LIST;
		// copy해서 사용
		return new ArrayList<>(listClases);
	}

	@SuppressWarnings("unchecked")
	public static synchronized List<ProjectInfo> loadSources(boolean reflesh) {

		String classDirName = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		try {
			if (listSources == null || reflesh)
				listSources = DynamicClassLoader.listSources(classDirName);
		} catch (Exception e) {
			LOGGER.error("exception %s", e.toString());
		}

		if (listSources == null)
			return Collections.EMPTY_LIST;
		// copy해서 사용
		return new ArrayList<>(listSources);
	}

	public static void initLoad() {
		Service<Void> serviceClasses = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {

				Task<Void> task = new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						String classDirName = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
						try {
							listClases = DynamicClassLoader.listClases(classDirName);
						} catch (Exception e) {
							LOGGER.error(String.format("exception %s", e.toString()));
						}
						return null;
					}

				};

				return task;
			}
		};
		serviceClasses.start();

		Service<Void> serviceSources = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {

				Task<Void> task = new Task<Void>() {

					@Override
					protected Void call() throws Exception {

						String classDirName = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
						try {
							listSources = DynamicClassLoader.listSources(classDirName);
						} catch (Exception e) {
							LOGGER.error(String.format("exception %s", e.toString()));
						}
						return null;
					}

				};

				return task;
			}
		};
		serviceSources.start();
	}

	/**
	 * 초기화처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 27.
	 */
	public static void init() {

		synchronized (lock) {

			if (listClases != null) {
				listClases.clear();
			}

			if (listSources != null)
				listSources.clear();
			initLoad();
		}

	}

	public static void setWorkspaceTab(DockTabPane tabPanWorkspace) {
		tabWorkspace = tabPanWorkspace;
	}

	public static DockTabPane getWorkspaceTab() {
		return tabWorkspace;
	}

	public static void setSystemLayoutView(SystemLayoutViewController sys) {
		systemLayoutViewController = sys;
	}

	public static final SystemLayoutViewController getSystemLayoutViewController() {
		return systemLayoutViewController;
	}

}
