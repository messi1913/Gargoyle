/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.word
 *	작성일   : 2016. 12. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.word;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.MimeHelper;
import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 비동기로 Word문서의 내용을 붙여넣고 실행처리할 수 있는 처리를 지원.
 *
 * @author KYJ
 *
 */
public class AsynchWordExecutor implements RuntimeExucteHandlerble {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AsynchWordExecutor.class);

	private File mimeFile;
	private Function<File, String> mimeFileConverter = file -> {
		String html = FileUtil.readFile(file, LoadFileOptionHandler.getDefaultHandler());
		try {
			return MimeHelper.toMime(html);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return null;
	};

	public AsynchWordExecutor(File htmlFile, Function<File, String> mimeFileConverter) throws UnsupportedEncodingException {
		this.mimeFileConverter = mimeFileConverter;
	}

	public AsynchWordExecutor(String html) {
		File tempFileSystem = FileUtil.getTempFileSystem();
		String currentDateString = DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSSS);
		String simpleTemplFileName = String.format("_%s.html", currentDateString);
		mimeFile = new File(tempFileSystem, simpleTemplFileName);
	}

	public String toMime(File htmlFile) throws UnsupportedEncodingException {
		return mimeFileConverter.apply(htmlFile);
	}

	public void execute() {
		if (mimeFile == null || !mimeFile.exists())
			return;

		try {
			toMime(mimeFile);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		RuntimeClassUtil.exeAsynchLazy(findWordLocationCommand(), encoding(), handler());
	}

	/**
	 * word파일의 위치를 찾는 명령코드 Window 기준.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 23.
	 * @return
	 */
	public List<String> findWordLocationCommand() {
		List<String> command = new ArrayList<String>();
		command.add("REG");
		command.add("QUERY");
		command.add("HKCU\\Software\\Microsoft\\Office\\14.0\\Word\\Options");
		command.add("/v");
		command.add("PROGRAMDIR");
		return command;
	}

	public String encoding() {
		return "UTF-8";
	}

	public BiConsumer<Integer, StringBuffer> handler() {
		BiConsumer<Integer, StringBuffer> convert = (code, buf) -> {
			if (code == 0) {
				System.out.println("exit Code : " + code);
				String str = buf.toString();
				String matchingStr = "REG_SZ";
				int indexOf = str.indexOf(matchingStr);
				if (indexOf != -1) {
					str = str.substring(indexOf + 6).trim() + File.separator + "WINWORD.exe";
					LOGGER.debug("레지스트리에서 검색된 MS WORD 경로 :  {}", str);
				}

				File wordFileLocation = new File(str);
				if (wordFileLocation.exists()) {
					try {
						RuntimeClassUtil.simpleExec(getCommand());
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
					}
				}
			}

		};
		return convert;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.word.RuntimeExucteHandlerble#getCommand()
	 */
	@Override
	public List<String> getCommand() {

		return null;
	}

}
