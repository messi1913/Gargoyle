/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.word
 *	작성일   : 2016. 12. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.word;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.MimeHelper;

/**
 *
 *  HTML 컨텐츠를 Mime 타입의 컨텐츠로 바꾸기 위한 어댑터
 *
 *  HTML파일을 Mime타입으로 바꾸는 로직 기술.
 * @author KYJ
 *
 */
public class HtmlMimeAdapter extends AbstractMimeAdapter {

	/**
	 * @param mimeFile
	 */
	public HtmlMimeAdapter(File htmlFile) {
		super(htmlFile);
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.word.AbstractMimeAdapter#getContent()
	 */
	@Override
	public String getContent() {
		String content = "";
		try {
			content = MimeHelper.toMime(FileUtil.readFile(super.mimeFile, LoadFileOptionHandler.getDefaultHandler()));
		} catch (UnsupportedEncodingException e) {
		}
		return content;
	}

}
