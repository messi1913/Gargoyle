/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 10. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Objects;
import com.kyj.fx.voeditor.visual.component.JavaProjectFileTreeItem;
import com.kyj.fx.voeditor.visual.component.JavaProjectFileWrapper;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleParamEmptyException;
import com.kyj.fx.voeditor.visual.framework.FileCheckHandler;
import com.kyj.fx.voeditor.visual.framework.collections.CachedMap;
import com.kyj.fx.voeditor.visual.framework.model.proj.ProjectDescription;
import com.kyj.fx.voeditor.visual.framework.parser.GargoyleJavaParser;
import com.kyj.fx.voeditor.visual.framework.thread.CloseableCallable;
import com.kyj.fx.voeditor.visual.framework.thread.DemonThreadFactory;
import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

import javafx.scene.control.TreeItem;

/**
 *
 * 파일 관련 처리 유틸리티
 *
 * @author KYJ
 *
 */
public class FileUtil implements GargoyleExtensionFilters {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	/**
	 * 이클립스의 경우 프로젝트 디렉토리라는 걸 인식하기 위한 파일 확장자. 이 확장자에는 참조되는 라이브러리에 대한 메타데이터를 정의함.
	 *
	 * @최초생성일 2016. 5. 5.
	 */
	private static final String PROJECT = ".project";
	/**
	 * 자바파일 확장자
	 *
	 * @최초생성일 2016. 5. 5.
	 */
	private static final String JAVA = ".java";
	/**
	 * pdf파일 확장자
	 *
	 * @최초생성일 2016. 5. 5.
	 */
	private static final String PDF = ".pdf";
	/**
	 * FXML파일 확장자
	 *
	 * @최초생성일 2016. 5. 5.
	 */
	private static final String FXML = ".fxml";

	/**
	 * XML 파일 확장자
	 *
	 * @최초생성일 2016. 8. 19.
	 */
	private static final String XML = ".xml";

	private static final String ZIP = ".zip";

	private static final String JAR = ".jar";
	/**
	 * 이미지 파일 확장자들을 정의
	 *
	 * @최초생성일 2016. 5. 5.
	 */
	public static String[] IMAGES_FILES = new String[] { ".jpg", ".png", ".bmp", ".jpeg" };

	/**
	 * 파일 혹은 디렉토리 오픈
	 *
	 * @Date 2015. 10. 15.
	 * @param file
	 * @return
	 * @User KYJ
	 */
	public static boolean browseFile(File file) {
		boolean isSuccess = false;
		if (file != null) {
			try {
				if (file.exists()) {
					if (file.isDirectory()) {
						Desktop.getDesktop().open(file);
						isSuccess = true;
					} else if (file.isFile()) {
						Desktop.getDesktop().open(file.getParentFile());
						isSuccess = true;
					}
				}

			} catch (IOException e) {
				isSuccess = false;
			}
		}
		return isSuccess;
	}

	/**
	 * openFile
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 18.
	 * @param file
	 * @return
	 */
	public static boolean openFile(File file) {
		return openFile(file, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @param errorHandler
	 * @return
	 */
	public static boolean openFile(File file, Consumer<Exception> errorHandler) {
		return fileOpenAction(file, errorHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @return
	 */
	public static boolean editFile(File file) {
		return fileEditAction(file, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @param errorHandler
	 * @return
	 */
	public static boolean editFile(File file, Consumer<Exception> errorHandler) {
		return fileEditAction(file, errorHandler);
	}

	/**
	 * @최초생성일 2017. 10. 10.
	 */
	private static BiFunction<File, Consumer<Exception>, Boolean> openAction = (file, errhandler) -> {
		boolean isSuccess = false;
		try {
			Desktop.getDesktop().open(file);
			isSuccess = true;
		} catch (IOException e) {

			String osName = SystemUtils.OS_NAME.toLowerCase();
			LOGGER.debug(osName);
			if (osName.contains("window")) {
				RuntimeClassUtil.simpleExeAsynchLazy(Arrays.asList("explorer", file.getAbsolutePath()), errhandler);
			} else
				errhandler.accept(e);
		}
		return isSuccess;
	};

	/**
	 * @최초생성일 2017. 10. 10.
	 */
	private static BiFunction<File, Consumer<Exception>, Boolean> editAction = (file, errhandler) -> {
		boolean isSuccess = false;
		try {
			Desktop.getDesktop().edit(file);
			isSuccess = true;
		} catch (IOException e) {
			errhandler.accept(e);
		}
		return isSuccess;
	};

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @param errorHandler
	 * @return
	 */
	private static boolean fileEditAction(File file, Consumer<Exception> errorHandler) {
		return fileAction(file, editAction, errorHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @param errorHandler
	 * @return
	 */
	private static boolean fileOpenAction(File file, Consumer<Exception> errorHandler) {
		return fileAction(file, openAction, errorHandler);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 10.
	 * @param file
	 * @param action
	 * @param errorHandler
	 * @return
	 */
	private static boolean fileAction(File file, BiFunction<File, Consumer<Exception>, Boolean> action, Consumer<Exception> errorHandler) {
		if (Desktop.isDesktopSupported()) {
			if (file.exists()) {
				return action.apply(file, errorHandler);
			}
		}
		return false;
	}

	private static final Map<File, String> cacheReadFile = new CachedMap<>(60000);

	/**
	 * 파일 읽기 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 19.
	 * @param file
	 * @param options
	 * @return
	 */
	public static String readFile(File file, LoadFileOptionHandler options) {
		return readFile(file, false, options);
	}

	/**
	 * 파일 읽기 처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 26.
	 * @param file
	 * @param useCache
	 *            메모리에 읽어온 파일의 컨텐츠를 임시저장함.
	 * @param options
	 * @return
	 */
	public static String readFile(File file, boolean useCache, LoadFileOptionHandler options) {
		String content = "";

		if (useCache && cacheReadFile.containsKey(file)) {
			LOGGER.debug(" file -> {} read from cache.  ", file);
			return cacheReadFile.get(file);
		}

		try {

			if (file != null && options == null) {
				content = FileUtils.readFileToString(file, "UTF-8");
				if (useCache)
					cacheReadFile.put(file, content);

				return content;
			}

			if (file == null) {

				if (options == null)
					return null;

				Function<File, String> fileNotFoundThan = options.getFileNotFoundThan();
				if (fileNotFoundThan != null) {
					content = fileNotFoundThan.apply(file);
					return content;
				}
			}

			List<String> fileNameLikeFilter = options.getFileNameLikeFilter();
			boolean isMatch = false;
			if (fileNameLikeFilter == null) {
				isMatch = true;
			} else {
				for (String ext : fileNameLikeFilter) {
					if (file.getName().endsWith(ext)) {
						isMatch = true;
						break;
					}
				}
			}

			if (isMatch && file.exists()) {

				// 인코딩이 존재하지않는경우 UTF-8로 치환.
				String encoding = options.getEncoding();
				if (ValueUtil.isEmpty(encoding))
					encoding = "UTF-8";

				content = FileUtils.readFileToString(file, encoding);
			} else {

				// 만약 파일이 존재하지않는다면 option파라미터에서 제공되는 처리내용을 반영.
				Function<File, String> fileNotFoundThan = options.getFileNotFoundThan();
				if (fileNotFoundThan != null) {
					content = fileNotFoundThan.apply(file);
				}
			}

		} catch (Exception e) {
			options.setException(e);
			LOGGER.error(ValueUtil.toString(e));
		}

		if (useCache)
			cacheReadFile.put(file, content);

		return content;

	}

	/**
	 * 자바파일인지 확인
	 *
	 * @Date 2015. 10. 18.
	 * @param file
	 * @return
	 * @User KYJ
	 */
	public static boolean isJavaFile(File file) {
		if (file.exists()) {
			return isJavaFile(file.getName());
		}
		return false;
	}

	public static boolean isJavaFile(String fileName) {
		return fileName.endsWith(JAVA);
	}

	/**
	 * FXML파일여부
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static boolean isFXML(File file) {
		if (file != null && file.exists()) {
			return isFXML(file.getName());
		}
		return false;
	}

	/**
	 * FXML파일여부
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param fileName
	 * @return
	 */
	public static boolean isFXML(String fileName) {
		return ValueUtil.isEmpty(fileName) ? false : fileName.endsWith(FXML);
	}

	/********************************
	 * 작성일 : 2016. 8. 19. 작성자 : KYJ
	 *
	 * XML 파일 여부
	 *
	 * @param file
	 * @return
	 ********************************/
	public static boolean isXML(File file) {
		if (file != null && file.exists()) {
			return isXML(file.getName());
		}
		return false;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15.
	 * @param file
	 * @return
	 */
	public static boolean isZip(File file) {
		if (file != null && file.exists()) {
			return isZip(file.getName());
		}
		return false;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 15.
	 * @param file
	 * @return
	 */
	public static boolean isJar(File file) {
		if (file != null && file.exists()) {
			return isJar(file.getName());
		}
		return false;
	}

	/********************************
	 * 작성일 : 2016. 8. 19. 작성자 : KYJ
	 *
	 * XML 파일 여부
	 *
	 * @param fileName
	 * @return
	 ********************************/
	public static boolean isXML(String fileName) {
		return ValueUtil.isEmpty(fileName) ? false : fileName.endsWith(XML);
	}

	private static boolean isZip(String fileName) {
		return ValueUtil.isEmpty(fileName) ? false : fileName.endsWith(ZIP);
	}

	private static boolean isJar(String fileName) {
		return ValueUtil.isEmpty(fileName) ? false : fileName.endsWith(JAR);
	}

	/**
	 * 파일 하위에 .project라는 파일이 존재하며 그 .project파일에 기술된 내용이 실제 디렉토리와 일치한다면 자바
	 * project파일이다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 16.
	 * @param file
	 * @return
	 */
	public static boolean isJavaProject(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				Optional<File> findFirst = Stream.of(file.listFiles()).filter(f -> Objects.equal(f.getName(), PROJECT)).findFirst();
				boolean exists = findFirst.isPresent();
				if (exists) {
					try {
						File projectFile = findFirst.get();
						ProjectDescription loadXml = SAXPasrerUtil.loadXml(projectFile, ProjectDescription.class);
						String name = loadXml.getName();
						return Objects.equal(file.getName(), name);
					} catch (Exception e) {

					}
				}
			}
		}
		return false;
	}

	/**
	 * 이미지 파일 유형인지 판단.
	 *
	 * @param file
	 * @return
	 */
	public static boolean isImageFile(File file) {
		if (file.exists()) {
			return Stream.of(IMAGES_FILES).filter(str -> file.getName().endsWith(str)).findFirst().isPresent();
		}
		return false;
	}

	public static boolean isPdfFile(File file) {
		if (file.exists()) {
			return file.getName().endsWith(PDF);
		}
		return false;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 1.
	 * @param is
	 * @return
	 */
	public static String readToString(InputStream is) {
		return readToString(is, "UTF-8");
	}

	/**
	 * 인코딩 옵션 추가. </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 1.
	 * @param is
	 * @param encoding
	 * @return
	 */
	public static String readToString(InputStream is, String encoding) {
		String result = "";
		if (is != null) {
			BufferedReader br = null;
			StringBuffer sb = new StringBuffer();
			String temp = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, encoding));
				while ((temp = br.readLine()) != null) {
					sb.append(temp).append(System.lineSeparator());
				}
			} catch (Exception e) {

				try {
					if (br != null)
						br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				result = sb.toString();
			}
		}
		return result;
	}

	/**
	 * 사용자가 선택했던 워크스페이스 경로와 userDir의 파일경로를 상대경로화 시켜 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 31.
	 * @param userDir
	 * @return
	 */
	public static Path toRelativizeForGagoyle(File userDir) {
		if (userDir == null || !userDir.exists())
			throw new GagoyleParamEmptyException("userDir is invalide.");

		// 워크스페이스에서 선택했던 기본 디렉토리 정보
		String baseDir = ResourceLoader.getInstance().get(ResourceLoader.BASE_DIR);
		File _baseDir = new File(baseDir);
		Path baseDirPath = _baseDir.toPath();

		return baseDirPath.relativize(userDir.toPath());
	}

	/********************************
	 * 작성일 : 2016. 5. 5. 작성자 : KYJ
	 *
	 * 디렉토리 삭제.
	 *
	 * 일반 delete 함수로는 디렉토리안에 파일들이 존재하는 삭제하는 허용되지않음. 그래서 하위파일들을 먼저 삭제하고 디렉토리삭제를
	 * 처리해야함. 이 함수는 그런 디렉토리 삭제를 지원해주기 위한 함수.
	 *
	 * @param path
	 ********************************/
	public static boolean deleteDir(File path) {
		if (!path.exists()) {
			return false;
		}

		File[] files = path.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				file.delete();
			}
		}

		return path.delete();
	}

	/**
	 * 시스템 Temp 파일 위치 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @return
	 */
	public static File getTempFileSystem() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/********************************
	 * 작성일 : 2016. 7. 13. 작성자 : KYJ
	 *
	 * SnapShot 임시 디렉토리 리턴.
	 *
	 * @return
	 ********************************/
	public static File getTempSnapShotDir() {
		File file = new File("SnapShot");
		if (!file.exists())
			file.mkdir();
		return file;
	}

	/**
	 * Gagoyle에서 사용중인 Temp 파일 디렉토리 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 */
	public static File getTempGagoyle() {
		File file = new File(getTempFileSystem(), "Gagoyle");
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * Gagoyle에서 사용중인 css Temp 파일 위치리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @return
	 */
	public static File getTemplGagoyleCss() {
		File file = new File(getTempGagoyle(), "css");
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * Gagoyle에서 사용중인 fxml Temp 파일 위치리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @return
	 */
	public static File getTemplGagoyleFxml() {
		File file = new File(getTempGagoyle(), "fxml");
		if (!file.exists())
			file.mkdirs();
		return file;
	}

	/**
	 * is 내용을 filr로 write처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param file
	 * @param is
	 * @throws IOException
	 */
	public static void writeFile(File file, InputStream is, Charset charset) throws IOException {

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
			try (InputStreamReader reader = new InputStreamReader(is, charset)) {
				int read = -1;
				while ((read = reader.read()) != -1) {
					writer.write(read);
				}
				writer.flush();
			}
		}
	}

	/**
	 * str 내용을 file로 write처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param file
	 * @param str
	 * @param charset
	 * @throws IOException
	 */
	public static void writeFile(File file, String str, Charset charset) throws IOException {
		try (FileOutputStream out = new FileOutputStream(file, false)) {
			try (OutputStreamWriter writer = new OutputStreamWriter(out, charset)) {
				writer.write(str);
				writer.flush();
			}
		}

	}

	/**
	 * str 내용을 file로 write처리함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 27.
	 * @param file
	 * @param str
	 * @param charset
	 * @param errorHandler
	 */
	public static void writeFile(File file, String str, Charset charset, Consumer<Exception> errorHandler) {
		try {
			writeFile(file, str, charset);
		} catch (Exception e) {
			if (errorHandler != null)
				errorHandler.accept(e);
			else
				LOGGER.error(ValueUtil.toString(e));
		}

	}

	/**
	 * str 내용을 file로 write처리함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param file
	 * @param str
	 * @param charset
	 * @throws IOException
	 */
	public static void writeFile(File file, byte[] bytes, Charset charset) throws IOException {
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {

			for (byte b : bytes) {
				writer.write(b);
			}

			writer.flush();
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * File로부터 packageName을 리턴.
	 *
	 * @param javaFile
	 * @param converter
	 * @param errorHandler
	 * @return
	 ********************************/
	public static Optional<String> getPackageName(File javaFile, FileCheckHandler<String> converter, Consumer<Exception> errorHandler) {

		try {
			if (javaFile == null) {
				return Optional.of(converter.ifNull());
			}

			if (!javaFile.exists()) {
				return Optional.of(converter.notExists());
			}

			if (converter.isMatch(javaFile)) {
				String packageName = GargoyleJavaParser.getPackageName(javaFile, converter);
				return Optional.of(packageName);

			} else {

				return Optional.of(converter.notMatchThan());
			}

		} catch (Exception e) {
			errorHandler.accept(e);
		}

		return Optional.empty();
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * JavaParser
	 *
	 * @param javaFile
	 * @param converter
	 * @param errorHandler
	 ********************************/
	public static void consumeJavaParser(File javaFile, Consumer<CompilationUnit> converter, Consumer<Exception> errorHandler) {

		try {
			if (javaFile != null && javaFile.exists()) {
				CompilationUnit cu = GargoyleJavaParser.getCompileUnit(javaFile);
				converter.accept(cu);
			}
		} catch (Exception e) {
			errorHandler.accept(e);
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 14. 작성자 : KYJ
	 *
	 * JavaProjectTree 반환
	 *
	 * @param treeItem
	 * @return
	 ********************************/
	public static JavaProjectFileTreeItem toJavaProjectFileTreeItem(TreeItem<JavaProjectFileWrapper> treeItem) {
		if (treeItem != null) {
			if (treeItem instanceof JavaProjectFileTreeItem) {
				return (JavaProjectFileTreeItem) treeItem;
			}
			return toJavaProjectFileTreeItem(treeItem.getParent());
		}
		return null;
	}

	/**
	 * 비동기 텍스트 리더 </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 3.
	 * @param location
	 * @param conCompleted
	 * @throws IOException
	 */
	public static <T> void asynchRead(File location, Consumer<String> conCompleted) {

		DemonThreadFactory<String> newInstance = DemonThreadFactory.newInstance();
		Thread newThread = newInstance.newThread(new CloseableCallable<String>() {

			@Override
			public String call() throws Exception {
				return FileUtil.readFile(location, LoadFileOptionHandler.getDefaultHandler());
			}
		}, conCompleted, err -> {
			throw new RuntimeException(err);
		});

		newThread.start();
	}

	public static <T> void asynchRead(Path path, Function<byte[], T> handler) throws IOException {

		AsynchronousFileChannel open = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

		// ByteBuffer 크기를 8k로 축소
		ByteBuffer byteBuffer = ByteBuffer.allocate(8 * 1024);
		long position = 0L;
		Long attachment = 0L;
		long fileSize = open.size();

		open.read(byteBuffer, position, attachment, new CompletionHandler<Integer, Long>() {

			@Override
			public void completed(Integer result, Long attachment) {

				if (result == -1)
					close();

				// 읽기 쓰기 병행시 flip을 호출해줘야함.
				byteBuffer.flip();
				byteBuffer.mark();
				handler.apply(byteBuffer.array());
				byteBuffer.reset();

				// 읽어들인 바이트수가
				// 파일사이즈와 같거나(버퍼 크기와 파일 크기가 같은 경우)
				// 버퍼 사이즈보다 작다면 파일의 끝까지 읽은 것이므로 종료 처리
				if (result == fileSize || result < byteBuffer.capacity()) {

					//// asyncFileChannel 닫기
					close();

					return;
				}
				// 읽을 내용이 남아있으므로 반복 회수를 증가 시키고 다시 읽는다.
				attachment++;
				open.read(byteBuffer, result * attachment, attachment, this);

			}

			@Override
			public void failed(Throwable exc, Long attachment) {
				exc.printStackTrace();
				close();

			}

			public void close() {
				try {
					if (open != null)
						open.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
		});

	}

	/**
	 * 파일확장자 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param extension
	 */
	public static String getFileExtension(File fileName) {
		return getFileExtension(fileName.getName());
	}

	/**
	 * 파일확장자 리턴
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 4.
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		int dotIndex = -1;
		int length = fileName.length();

		for (int i = length-1; i >= 0; i--) {
			if (fileName.charAt(i) == '.') {
				dotIndex = i;
				break;
			}
		}
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	/**
	 * 디렉토리 생성
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 11.
	 * @param file
	 * @throws IOException
	 */
	public static final void mkDirs(File file) throws IOException {

		if (file.exists())
			return;

		File parentFile = null;
		if (file.isFile()) {
			parentFile = file.getParentFile();
		}

		if (parentFile == null || !parentFile.exists()) {
			Path path = Paths.get(file.getAbsolutePath());
			Files.createDirectories(path.getParent());
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24.
	 * @param is
	 *            source
	 * @param file
	 *            dst. file
	 * @throws IOException
	 */
	public static void copy(InputStream is, File file) throws IOException {

		try (FileWriter writer = new FileWriter(file)) {
			int tmp = -1;
			while ((tmp = is.read()) != -1) {
				writer.write(tmp);
			}
			writer.flush();
		}

	}

	public static void copy2(InputStream is, File file) throws IOException {

		try (FileOutputStream out = new FileOutputStream(file)) {
			int tmp = -1;
			while ((tmp = is.read()) != -1) {
				out.write(tmp);
			}
			out.flush();
		}

	}

	/**
	 * 파일을 정렬하는 Comparator를 리턴한다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 8.
	 * @param isAscending
	 * @return
	 */
	public static Comparator<? super File> sortByName(boolean isAscending) {
		int flag = isAscending ? 1 : -1;
		return new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {

				if (o1 == null && o2 != null)
					return flag * 1;
				else if (o1 != null && o2 == null)
					return flag * -1;
				else if (o1 == null && o2 == null)
					return 0;
				return flag * (o1.getName().compareTo(o2.getName()));

			}
		};
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 14.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(File file) throws IOException {
		return FileUtils.readFileToByteArray(file);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 16.
	 * @param imageFile
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytes(File imageFile) throws IOException {
		return toByteArray(imageFile);
	}

	/**
	 * 파일내용에 인코딩 정보가 존재하면 그 인코딩을 리턴합니다. </br>
	 * 만약에 인코딩정보가 없다면 UTF-8을 리턴합니다 </br>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 1.
	 * @param f
	 *            인코딩을 찾을 파일.
	 * @return 인코딩
	 * @throws IOException
	 */
	public static String findEncoding(File f) throws IOException {
		return new FileEncodingFinder(f).getEncoding();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 11. 22.
	 * @param name
	 * @return
	 */
	public static String getFileNameWithoutExtension(String name) {
		return FilenameUtils.removeExtension(name);
	}

}
