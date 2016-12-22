/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 29.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.util.ByteBuffer;

/**
 * runtime execution
 *
 * @author KYJ
 *
 */
public class RuntimeClassUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(RuntimeClassUtil.class);

	private static final String DEFAULT_ENCODING = "UTF-8";

	public static List<String> exe(List<String> args) throws Exception {
		return exe(DEFAULT_ENCODING, args);
	}

	/********************************
	 * 작성일 : 2016. 6. 19. 작성자 : KYJ
	 *
	 * 단순 실행처리.
	 *
	 * @param args
	 * @throws Exception
	 ********************************/
	public static void simpleExec(List<String> args) throws Exception {
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.start();
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	public static List<String> exe(String encode, List<String> args) throws Exception {

		List<String> resultList = new ArrayList<String>();
		ProcessBuilder pb = new ProcessBuilder(args);
		InputStream i = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Process p = null;
		try {
			p = pb.start();
			p.waitFor();
			i = p.getInputStream();
			isr = new InputStreamReader(i, encode);
			br = new BufferedReader(isr);

			String temp = null;

			while ((temp = br.readLine()) != null) {
				resultList.add(ValueUtil.rightTrim(temp) + "\n");
			}

			br.close();
			isr.close();
			i.close();
			p.destroy();
			p.exitValue();

		} catch (IOException e) {
			e.printStackTrace();
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (i != null) {
				try {
					i.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (p != null) {
				p.destroy();
				p.exitValue();

			}

		}

		return resultList;
	}

	public static void addShutdownHook(Runnable run) {
		addShutdownHook(new Thread(run));
	}

	public static void addShutdownHook(Thread t) {
		Runtime.getRuntime().addShutdownHook(t);
	}

	/**
	 * 파일을 오픈함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 11.
	 * @param file
	 * @return
	 */
	public static boolean openFile(File file) {
		return FileUtil.openFile(file);
	}

	private static BiFunction<File, InputStream, Void> FILE_WRITER_HANDLER = new BiFunction<File, InputStream, Void>() {

		@Override
		public Void apply(File outputFile, InputStream in) {
			if (outputFile == null || in == null)
				return null;

			try (BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
				try (FileWriter writer = new FileWriter(outputFile, true)) {

					String temp = null;
					while ((temp = br.readLine()) != null) {
						LOGGER.debug(temp);
						writer.write(temp);
						writer.write(System.lineSeparator());
					}

				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			} catch (IOException e1) {
				LOGGER.error(ValueUtil.toString(e1));
			}

			return null;
		}
	};

	/**
	 * 실행하되 실행된 로그 정보를 outputFile위치로 로그를 write한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 6.
	 * @param args
	 * @param outputFile
	 * @return
	 * @throws Exception
	 */
	public static int exe(List<String> args, File outputFile) throws Exception {
		return exe(args, outputFile, FILE_WRITER_HANDLER);
	}

	static int exe(List<String> args, File outputFile, BiFunction<File, InputStream, Void> streamHandler) throws Exception {
		int result = -1;
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);

		Process p = null;
		InputStream is = null;

		try {
			p = pb.start();

			is = p.getInputStream();
			streamHandler.apply(outputFile, is);

			p.waitFor(10, TimeUnit.SECONDS);

			p.destroy();
			result = p.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return result;
	}

	public static int exe(List<String> args, Consumer<String> messageReceiver) throws Exception {
		int result = -1;
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);

		Process p = null;
		BufferedReader br = null;

		try {
			p = pb.start();

			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			p.waitFor(10, TimeUnit.SECONDS);
			String temp = null;

			while ((temp = br.readLine()) != null) {
				messageReceiver.accept(temp);
			}
			p.destroy();
			result = p.exitValue();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return result;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 22.
	 * @param args
	 * @param convert
	 * 		Integer : exitCode
	 * 		InputStream  : dataStream
	 * @return
	 * @throws Exception
	 */
	/*@Deprecated 테스트를 더 해봐야함.*/
	@Deprecated
	public static void exeAsynchLazy(List<String> args, BiConsumer<Integer, ByteBuffer> convert) {

		Thread thread = new Thread() {

			/* (non-Javadoc)
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {

				ProcessBuilder pb = new ProcessBuilder(args);
				pb.redirectErrorStream(true);
				int result = -1;
				Process p = null;
				BufferedReader br = null;
				ByteBuffer byteBuffer = new ByteBuffer();

				try {
					p = pb.start();


					InputStream is = p.getInputStream();
					p.waitFor(10, TimeUnit.SECONDS);


					int n;
					byte[] buf = new byte[4096];
					while ((n = is.read(buf)) != -1) {
						byteBuffer.appendByte(n);
					}

					p.destroy();
					result = p.exitValue();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				convert.accept(result, byteBuffer);
			}

		};

		thread.setDaemon(true);
		thread.start();
	}

}
