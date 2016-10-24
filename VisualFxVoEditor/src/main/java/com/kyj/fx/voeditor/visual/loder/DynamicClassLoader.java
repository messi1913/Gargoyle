/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.loder
 *	작성일   : 2015. 10. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.SystemUtils;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.kyj.fx.voeditor.visual.main.model.vo.ClassPath;
import com.kyj.fx.voeditor.visual.main.model.vo.ClassPathEntry;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 이 클래스로더에서 사용하고자하는 주된 목적은 자바 프로젝트를 대상으로 특정클래스를 로딩하는게 주 목적이다. 단순히 Jar파일을 로드하고자
 * 하는 목적이 아님을 참고하길.
 *
 * @author KYJ
 *
 *
 */
public class DynamicClassLoader {

	private static final String ENCODING = "UTF-8";
	private static final String CLASS_FILE_EXTENSION = ".class";
	private static final String JAVA_FILE_EXTENSION = ".java";
	private static final String FXML_FILE_EXTENSION = ".fxml";
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicClassLoader.class);
	public static final String CLASSPATH_FILE_NAME = ".classpath";

	// private static ClassLoader loader;

	// public static ClassLoader getLoader() {
	// return loader;
	// }

	protected DynamicClassLoader() {
	}

	/**
	 * Node값이 null일때 대체할 값을 리턴
	 *
	 * @최초생성일 2015. 10. 26.
	 */
	private static StringSupplier supplier;

	/**
	 * XML파싱처리시 Node값이 null일때 리턴할 정보를 리턴함. 여기서는 ""공백값을 리턴. XML노드가 리턴해주는 값이
	 * 어떤정보냐에 따라 객체생성하는부분을 일일히 if체크 하기 귀찮고 너저분한것같아 해당API를 사용
	 *
	 * @author KYJ
	 *
	 */
	static class StringSupplier implements Supplier<String> {
		public String get() {
			return "";
		}
	}

	/**
	 * Node값이 null일때 대체할 값을 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @return
	 */
	public static Supplier<String> getSupplyer() {
		if (supplier == null)
			supplier = new StringSupplier();
		return supplier;
	}

	public static void load(String classDirName) throws Exception {
		List<ProjectInfo> listClases = listClases(classDirName);

		// 로딩
		for (ProjectInfo info : listClases) {
			String projectDir = info.getProjectDir();

			info.getClasses().forEach(clazz -> {
				try {
					load(projectDir, clazz);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

	}

	public static List<ProjectInfo> listClases(String classDirName) throws Exception {
		File file = new File(classDirName);
		// 기본적인 파일의 존재유무 및 디렉토리인지 체크.
		if (!file.exists())
			throw new FileNotFoundException(file + " Not found!");

		//
		if (!file.isDirectory())
			throw new IllegalArgumentException("only directory.");

		/*
		 * 디렉토리안에 클래스패스 정보가 존재하는지 확인하고 존재한다면 classpath에 기술된 정보 기준으로 클래스 파일을
		 * 로드한다. 프로그램내에서 workspace를 선택한 경우일수있고, 프로젝트를 선택한 두가지의 경우가 있기때문에 두가지의
		 * 케이스를 고려한 로직이 들어간다.
		 */

		/*
		 * 일단 워크스페이스를 선택한경우라고 가정하고 워크스페이스내에 폴더들을 순차적으로 돌아보면서 classpath의 존재유무를 찾고
		 * 존재하는케이스는 따로 모아놓는다. 파일레벨은 워크스페이스(0레벨)-프로젝트(1레벨)로 가정하여 1레벨까지만 이동한다.
		 */
		List<File> listFiles = findClassPaths(file);

		/*
		 * classpath파일을 찾은경우 그 파일path를 기준으로 클래스들을 로딩한다.
		 */
		List<ProjectInfo> allClasses = new ArrayList<>();

		if (listFiles != null && !listFiles.isEmpty())
			LOGGER.debug(" im will working...");

		long startTime = System.currentTimeMillis();

		int searchedDirCount = 0;
		StringBuffer srchedDirNames = new StringBuffer();
		for (File f : listFiles) {
			try {
				ClassPath parsingClassPath = parsingClassPath(f.getAbsolutePath());

				// 프로젝트파일.
				File projectFile = f.getParentFile();

				// output 속성값의 존재유무만 확인하여 컴파일되는 경로를 찾는다.
				List<ProjectInfo> collect = parsingClassPath.toStream().filter(

						entry -> {
							boolean notEmpty = ValueUtil.isNotEmpty(entry.getOutput());
							LOGGER.debug(String.format("srch entry path : %s is Traget %b ", entry.getPath(), notEmpty));

							return notEmpty;
						}

				).map(pram -> pram.getOutput()).distinct().parallel().flatMap(new Function<String, Stream<ProjectInfo>>() {
					@Override
					public Stream<ProjectInfo> apply(String entry) {
						LOGGER.debug(String.format("entry : %s", entry));
						File compiledFilePath = new File(projectFile, entry);
						int length = compiledFilePath.getAbsolutePath().length() + 1;
						List<String> findClases = findClases(projectFile.getAbsolutePath(), compiledFilePath, length);
						LOGGER.debug(compiledFilePath.toString());
						LOGGER.debug(findClases.toString());
						LOGGER.debug(String.valueOf(findClases.size()));

						ProjectInfo classInfo = new ProjectInfo();
						classInfo.setProjectName(projectFile.getName());
						classInfo.setProjectDir(compiledFilePath.getAbsolutePath());
						classInfo.setClasses(findClases);

						return Stream.of(classInfo);
					}
				}).collect(Collectors.toList());

				allClasses.addAll(collect);
				searchedDirCount++;
				srchedDirNames.append(f.getAbsolutePath()).append(SystemUtils.LINE_SEPARATOR);
			} catch (SAXParseException e) {
				LOGGER.error(String.format("정상적인 XML 형태가 아님. 파일명 :  %s", f.getAbsolutePath()));
				LOGGER.error(String.format("%d 행 :: %d 열", e.getLineNumber(), e.getColumnNumber()));
			}

		}
		long endTime = System.currentTimeMillis();

		long costMillisend = endTime - startTime;

		LOGGER.debug(String.format("Total Cost time : %s (ms) searched Directory Count : %d ", costMillisend, searchedDirCount));
		LOGGER.debug(String.format("Searched Dirs info \n%s", srchedDirNames.toString()));
		return allClasses;
	}

	public static List<ProjectInfo> listSources(String classDirName) throws Exception {
		File file = new File(classDirName);
		// 기본적인 파일의 존재유무 및 디렉토리인지 체크.
		if (!file.exists())
			throw new FileNotFoundException(file + " Not found!");

		//
		if (!file.isDirectory())
			throw new IllegalArgumentException("only directory.");

		/*
		 * 디렉토리안에 클래스패스 정보가 존재하는지 확인하고 존재한다면 classpath에 기술된 정보 기준으로 클래스 파일을
		 * 로드한다. 프로그램내에서 workspace를 선택한 경우일수있고, 프로젝트를 선택한 두가지의 경우가 있기때문에 두가지의
		 * 케이스를 고려한 로직이 들어간다.
		 */

		/*
		 * 일단 워크스페이스를 선택한경우라고 가정하고 워크스페이스내에 폴더들을 순차적으로 돌아보면서 classpath의 존재유무를 찾고
		 * 존재하는케이스는 따로 모아놓는다. 파일레벨은 워크스페이스(0레벨)-프로젝트(1레벨)로 가정하여 1레벨까지만 이동한다.
		 */
		List<File> listFiles = findClassPaths(file);

		/*
		 * classpath파일을 찾은경우 그 파일path를 기준으로 클래스들을 로딩한다.
		 */
		List<ProjectInfo> allClasses = new ArrayList<>();

		if (listFiles != null && !listFiles.isEmpty())
			LOGGER.debug(" im will working...");

		long startTime = System.currentTimeMillis();

		int searchedDirCount = 0;
		StringBuffer srchedDirNames = new StringBuffer();
		for (File f : listFiles) {
			try {
				ClassPath parsingClassPath = parsingClassPath(f.getAbsolutePath());

				// 프로젝트파일.
				File projectFile = f.getParentFile();

				// output 속성값의 존재유무만 확인하여 컴파일되는 경로를 찾는다.
				List<ProjectInfo> collect = parsingClassPath.toStream().filter(

						entry -> {
							//							boolean notEmpty = ValueUtil.isNotEmpty(entry.getOutput());
							boolean notEmpty = ValueUtil.isNotEmpty(entry.getPath());
							LOGGER.debug(String.format("srch entry path : %s is Traget %b ", entry.getPath(), notEmpty));
							//

							return notEmpty;
						}

				).filter(entry -> {
					return StringUtils.equals("src", entry.getKind());
				}).map(pram -> pram.getPath()).distinct().parallel().flatMap(new Function<String, Stream<ProjectInfo>>() {
					@Override
					public Stream<ProjectInfo> apply(String entry) {
						LOGGER.debug(String.format("entry : %s", entry));
						File compiledFilePath = new File(projectFile, entry);
						int length = compiledFilePath.getAbsolutePath().length() + 1;
						List<String> findJavaSources = findSource(projectFile.getAbsolutePath(), compiledFilePath, length);
						LOGGER.debug(compiledFilePath.toString());
						LOGGER.debug(findJavaSources.toString());
						LOGGER.debug(String.valueOf(findJavaSources.size()));

						ProjectInfo classInfo = new ProjectInfo();
						classInfo.setProjectName(projectFile.getName());
						classInfo.setProjectDir(compiledFilePath.getAbsolutePath());
						classInfo.setJavaSources(findJavaSources);

						return Stream.of(classInfo);
					}
				}).collect(Collectors.toList());

				allClasses.addAll(collect);
				searchedDirCount++;
				srchedDirNames.append(f.getAbsolutePath()).append(SystemUtils.LINE_SEPARATOR);
			} catch (SAXParseException e) {
				LOGGER.error(String.format("정상적인 XML 형태가 아님. 파일명 :  %s", f.getAbsolutePath()));
				LOGGER.error(String.format("%d 행 :: %d 열", e.getLineNumber(), e.getColumnNumber()));
			}

		}
		long endTime = System.currentTimeMillis();

		long costMillisend = endTime - startTime;

		LOGGER.debug(String.format("Total Cost time : %s (ms) searched Directory Count : %d ", costMillisend, searchedDirCount));
		LOGGER.debug(String.format("Searched Dirs info \n%s", srchedDirNames.toString()));
		return allClasses;
	}

	/**
	 * 특정 디렉토리안에 .classpath 파일이 존재하는지 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param filePathName
	 * @return
	 */
	public static List<File> findClassPaths(File filePathName) {
		return new FileSearcher(filePathName, 2, new String[] { CLASSPATH_FILE_NAME }).find();
	}

	/**
	 * 특정 디렉토리안에 .class 파일이 존재하는지 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param filePathName
	 * @return
	 */
	public static List<String> findClases(final String projectName, final File filePathName, final int length) {
		Function<File, String> toPackageName = file -> {
			String absolutePath = file.getAbsolutePath();
			String pathPattern = absolutePath.substring(length);
			String packagePath = pathPattern.replaceAll("\\\\", ".");
			int indexOf = packagePath.indexOf('$');
			if (indexOf >= 0) {
				return packagePath.substring(0, indexOf);
			} else {
				// 6의 의미는 ".class".length()
				return packagePath.substring(0, (packagePath.length() - 6));
			}
		};

		FileSearcher fileSearcher = new FileSearcher(filePathName, -1, new String[] { CLASS_FILE_EXTENSION }, new Predicate<File>() {

			// 탐색하지않을 파일명을 기입한다.
			List<String> exceptNames = ConfigResourceLoader.getInstance()
					.getValues(ConfigResourceLoader.FILTER_NOT_SRCH_DIR_NAME_SOURCE_TYPE, ",");

			@Override
			public boolean test(File file) {
				return !exceptNames.contains(file.getName());
			}
		});

		List<String> find = fileSearcher.find(toPackageName);
		return find.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 특정 디렉토리안에 .java 파일이 존재하는지 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param filePathName
	 * @return
	 */
	public static List<String> findSource(final String projectName, final File filePathName, final int length) {
		// 변환처리.
		Function<File, String> toPackageName = file -> {
			String absolutePath = file.getAbsolutePath();
			// String pathPattern = absolutePath.substring(length);
			// String packagePath = pathPattern.replaceAll("\\\\", ".");
			// int indexOf = packagePath.indexOf('$');
			// if (indexOf >= 0) {
			// return packagePath.substring(0, indexOf);
			// } else {
			// // 6의 의미는 ".class".length()
			// return packagePath.substring(0, (packagePath.length() - 6));
			// }
			return absolutePath;
		};

		FileSearcher fileSearcher = new FileSearcher(filePathName, -1, new String[] { JAVA_FILE_EXTENSION, FXML_FILE_EXTENSION },
				new Predicate<File>() {

					// 탐색하지않을 파일명을 기입한다.
					List<String> exceptNames = ConfigResourceLoader.getInstance()
							.getValues(ConfigResourceLoader.FILTER_NOT_SRCH_DIR_NAME_CLASS_TYPE, ",");

					@Override
					public boolean test(File file) {
						return !exceptNames.contains(file.getName());
					}
				});

		List<String> find = fileSearcher.find(toPackageName);

		return find.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 특정디렉토리안에 classpath파일의 존재유무를 확인한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param classDirName
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean containsClasspath(File classDirName) {
		File[] listFiles = classDirName.listFiles((f, fileName) -> CLASSPATH_FILE_NAME.equals(fileName));
		if (listFiles == null || listFiles.length == 0)
			return false;
		return true;
	}

	/**
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 23.
	 * @param classFullPath
	 *            로딩하고자하는 클래스의 경로.. 예를들면 아래와 같다.
	 *            "C:\\Users\\KYJ\\JAVA_FX\\webWorkspace\\meerkat-core\\target\\classes"
	 * @param loadClass
	 * @throws MalformedURLException
	 */
	public static Class<?> load(String classFullPath, String classForName) throws Exception {
		URL url = new File(classFullPath).toURI().toURL();
		LOGGER.debug(String.format("Path : %s", url.toString()));
		LOGGER.debug(String.format("Load class Name : %s", classForName));
		/*
		 * URL클래스로더는 한번 로드된 JAR파일을 삭제할수없다고 한다. 이 부분이 좀 애매한데. 로드된 클래스정보가 언제 사용될지
		 * 알고 없앤단말인가.. 사실 로드된 정보는 프로그램이 종료될때까지 유지되야되는게 맞다고 생각이 들긴한데..
		 *
		 * 참고로 로드된 클래스정보를 삭제가 불가능한건아닌데. JDK7에 추가된 close를 호출하면 가능하다고한다.
		 *
		 * 이코드에선 로드한 클래스정보는 삭제하지않도록하겠다.
		 *
		 * 2015.10.30 성능개선을 위해 loader를 전역으로 따로 빼서 처리했는데. 한개의 프로젝트를 로드하고 다른 프로젝트를
		 * 로드하려고 시도하는경우 클래스를 못찾는 버그가 있었다. 그래서 전역에 있던걸 다시 로컬로 빼서 처리함.
		 */
		// if (loader == null)
		ClassLoader loader = URLClassLoader.newInstance(new URL[] { url }, DynamicClassLoader.class.getClassLoader());
		try {
			if (loader != null)
				return loader.loadClass(classForName);
		} catch (Exception e) {

		}

		Class<?> forName = Class.forName(classForName, true, loader);
		return forName;
	}

	/**
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 23.
	 * @param JarFile
	 *            "C:\\Users\\KYJ\\JAVA_FX\\webWorkspace\\meerkat-core\\xxx.jar"
	 * @param classForName
	 *            로딩하고자하는 클래스
	 * @throws MalformedURLException
	 */
	public static Class<?> loadFromJarFile(File jarFile, String classForName) throws Exception {
		URL url = jarFile.toURI().toURL();

		/*
		 * URL클래스로더는 한번 로드된 JAR파일을 삭제할수없다고 한다. 이 부분이 좀 애매한데. 로드된 클래스정보가 언제 사용될지
		 * 알고 없앤단말인가.. 사실 로드된 정보는 프로그램이 종료될때까지 유지되야되는게 맞다고 생각이 들긴한데..
		 *
		 * 참고로 로드된 클래스정보를 삭제가 불가능한건아닌데. JDK7에 추가된 close를 호출하면 가능하다고한다.
		 *
		 * 이코드에선 로드한 클래스정보는 삭제하지않도록하겠다.
		 *
		 * 2015.10.30 성능개선을 위해 loader를 전역으로 따로 빼서 처리했는데. 한개의 프로젝트를 로드하고 다른 프로젝트를
		 * 로드하려고 시도하는경우 클래스를 못찾는 버그가 있었다. 그래서 전역에 있던걸 다시 로컬로 빼서 처리함.
		 */
		// if (loader == null)

		ClassLoader loader = URLClassLoader.newInstance(new URL[] { url }, DynamicClassLoader.class.getClassLoader());
		try {

			// RuntimeJarLoader.loadJarIndDir(loader,
			// jarFile.getAbsolutePath());
			if (loader != null) {
				return loader.loadClass(classForName);
			}
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		Class<?> forName = Class.forName(classForName, true, loader);
		return forName;
	}

	// static class RuntimeJarLoader {
	//
	// public static void loadJarIndDir(String dir) {
	// final URLClassLoader loader = (URLClassLoader)
	// ClassLoader.getSystemClassLoader();
	// loadJarIndDir(loader, dir);
	// }
	//
	// public static void loadJarIndDir(ClassLoader loader, String dir) {
	// try {
	// final Method method = URLClassLoader.class.getDeclaredMethod("addURL",
	// new Class[] { URL.class });
	// method.setAccessible(true);
	//
	// new File(dir).listFiles(new FileFilter() {
	// public boolean accept(File jar) {
	// // jar 파일인 경우만 로딩
	// if (jar.toString().toLowerCase().contains(".jar")) {
	// try {
	// // URLClassLoader.addURL(URL url) 메소드 호출
	// method.invoke(loader, new Object[] { jar.toURI().toURL() });
	// System.out.println(jar.getName() + " is loaded.");
	// } catch (Exception e) {
	// System.out.println(jar.getName() + " can't load.");
	// }
	// }
	// return false;
	// }
	// });
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	// }
	// }

	/**
	 * classPath의 정보를 파싱하여 데이터셋으로 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param filePathName
	 * @return
	 * @throws Exception
	 */
	public static ClassPath parsingClassPath(String filePathName) throws Exception {

		DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = newInstance.newDocumentBuilder();

		{
			FileTypeMap defaultFileTypeMap = MimetypesFileTypeMap.getDefaultFileTypeMap();
			String contentType = defaultFileTypeMap.getContentType(filePathName);
			LOGGER.debug(String.format("File path Name : %s Content type : %s ", filePathName, contentType));
		}

		Reader reader = new InputStreamReader(new FileInputStream(new File(filePathName)), ENCODING);
		InputSource is = new InputSource(reader);

		Document parse = builder.parse(is);

		ClassPath classPath = new ClassPath();
		classPath.setFilePathName(filePathName);
		classPath.setApplyedEncoding(ENCODING);

		NodeList elementsByTagName2 = parse.getElementsByTagName("classpath");
		int length = elementsByTagName2.getLength();
		for (int i = 0; i < length; i++) {
			Node classPathNode = elementsByTagName2.item(i);
			NodeList childNodes = classPathNode.getChildNodes();
			int classEntrySize = childNodes.getLength();
			for (int e = 0; e < classEntrySize; e++) {
				Node classEntryNode = childNodes.item(e);
				NamedNodeMap attributes = classEntryNode.getAttributes();
				if (attributes == null)
					continue;

				// 코드에서 필요로하는 부분만 XML을 파싱해서 데이터셋에 담는다.
				String kind = emptyThan(attributes.getNamedItem("kind"));
				String output = emptyThan(attributes.getNamedItem("output"));
				String path = emptyThan(attributes.getNamedItem("path"));

				ClassPathEntry classPathEntry = new ClassPathEntry(kind, output, path);
				classPath.addEntry(classPathEntry);
			}
		}
		return classPath;
	}

	/**
	 * Node의 값이 비어있을때 대체할 텍스트를 리턴한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 26.
	 * @param node
	 * @return
	 */
	private static String emptyThan(Node node) {
		if (node == null)
			return getSupplyer().get();
		return node.getNodeValue();
	}

}

/**
 * 클래스path를 탐색하는 기능을 갖는 클래스
 *
 * @author KYJ
 *
 */
class FileSearcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSearcher.class);

	private File root;
	private int maxLevel = 1;

	private String[] fileExtensions;
	private Predicate<File> filter;

	private static final Predicate<File> DEFAULT_FILTER = new Predicate<File>() {

		/*
		 *  탐색하지않을 파일명을 기입한다.
		 *  디폴트로는 소스 디렉토리가 존재하는 위치에 있는 대상은 필터링된다.
		 */
		List<String> exceptNames = ConfigResourceLoader.getInstance().getValues(ConfigResourceLoader.FILTER_NOT_SRCH_DIR_NAME_SOURCE_TYPE,
				",");

		@Override
		public boolean test(File file) {
			return !exceptNames.contains(file.getName());
		}
	};

	/**
	 * @return the filter
	 */
	public final Predicate<File> getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	//	public final void setFilter(Predicate<File> filter) {
	//		this.filter = filter;
	//	}

	public FileSearcher(File root) {

		this(root, -1, new String[] { DynamicClassLoader.CLASSPATH_FILE_NAME }, DEFAULT_FILTER);
	}

	public FileSearcher(File root, Predicate<File> filter) {
		this(root, -1, new String[] { DynamicClassLoader.CLASSPATH_FILE_NAME }, filter);
	}

	public FileSearcher(File root, int maxLevel, String[] fileExtensions) {
		this(root, -1, new String[] { DynamicClassLoader.CLASSPATH_FILE_NAME }, DEFAULT_FILTER);
	}

	public FileSearcher(File root, int maxLevel, String[] fileExtensions, Predicate<File> filter) {
		this.root = root;
		this.maxLevel = maxLevel;
		this.fileExtensions = fileExtensions;
		this.filter = filter;
	}

	public List<File> find() {
		return find(file -> file);
	}

	public <T> List<T> find(Function<File, T> func) {

		List<T> findFiles = new ArrayList<>();
		findClassPaths(findFiles, root, func);
		return findFiles;
	}

	private <T> void findClassPaths(List<T> findFiles, File file, Function<File, T> func) {
		findClassPaths(findFiles, file, 0, func);
	}

	private <T> void findClassPaths(List<T> findFiles, File file, int currentLevel, Function<File, T> func) {
		if (!getFilter().test(file)) {
			LOGGER.debug(String.format("탐색하지않는 디렉토리 %s", file.getAbsolutePath()));
			return;
		}
		/*
		 * 일단 워크스페이스를 선택한경우라고 가정하고 워크스페이스내에 폴더들을 순차적으로 돌아보면서 classpath의 존재유무를 찾고
		 * 존재하는케이스는 따로 모아놓는다. 파일레벨은 워크스페이스(0레벨)-프로젝트(1레벨)로 가정하여 1레벨까지만 이동한다.
		 */

		// 원하는 파일을 찾은경우 리스트에 추가하고 종료

		if (file.isFile()) {
			for (String extension : fileExtensions) {
				if (file.getName().indexOf(extension) >= 0) {
					findFiles.add(func.apply(file));
					break;
				}
			}

			return;
		}

		/*
		 * maxlevel보다 높은경우 탐색 중단. -1일경우 파일의 끝까지 탐색
		 */
		if (maxLevel != -1) {
			if (maxLevel < currentLevel) {
				// 디렉토리인경우만 출력
				if (file.isDirectory())
					LOGGER.debug("[레벨끝] 탐색 중단. " + file.toString() + " 레벨 : " + currentLevel);
				return;
			}
		}

		// 디렉토리인경우 레벨을 늘리고 재탐색
		if (file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (listFiles == null)
				return;
			for (File subFile : listFiles) {
				// 디렉토리인경우 하위 레벨 재탐색.
				findClassPaths(findFiles, subFile, (currentLevel + 1), func);
			}
		}
	}
}