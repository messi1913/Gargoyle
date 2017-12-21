/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2017. 12. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNProperties;

import com.kyj.fx.voeditor.visual.component.scm.SVNFileItem;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * 
 * 형상서버에 특정 리비전 파일만 복사해오는 기능 수행
 * 
 * @author KYJ
 *
 */
public class SVNFileCopyFunction extends SVNCopyFunction<SVNFileItem, File> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVNFileCopyFunction.class);

	private File outDir;
	private String relativePath;
	private String revision = "-1";

	private ExceptionHandler exceptionHandler;

	public SVNFileCopyFunction(String relativePath, String revision, File outDir) {
		this.relativePath = relativePath;
		this.revision = revision;
		this.outDir = outDir;
	}

	@Override
	public File apply(SVNFileItem t) {

		String simpleFileName = t.getSimpleName();
		File file = new File(outDir, simpleFileName);
		File resultFile = null;

		try (FileOutputStream out = new FileOutputStream(file, false)) {

			LOGGER.debug("getCopy] SVN relativePath : {}  out File Request : {} ", relativePath, file.getAbsolutePath());

			long result = t.getManager().getCopy(relativePath, Long.parseLong(revision), new SVNProperties(), out);
			if (result != -1)
				resultFile = file;

		} catch (Exception e1) {
			if (exceptionHandler != null) {
				exceptionHandler.handle(e1);
			} else {
				LOGGER.error(ValueUtil.toString(e1));
			}
		}

		return resultFile;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
