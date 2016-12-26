/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.word
 *	작성일   : 2016. 12. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.word;

import java.io.File;
import java.io.IOException;

import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.core.MSWord;

/**
 *
 *  HTML 컨텐츠를 Mime 타입의 컨텐츠로 바꾸기 위한 어댑터
 *
 *  HTML파일을 Mime타입으로 바꾸는 로직 기술.
 * @author KYJ
 *
 */
public class ContentWordAdapter extends AbstractMimeAdapter {

	private String content;

	/**
	 * @param mimeFile
	 */
	private ContentWordAdapter() {
		super(null);
	}

	public ContentWordAdapter(String content) {
		super(null);
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.word.AbstractMimeAdapter#getContent()
	 */
	@Override
	public String getContent() {
		return this.content;
	}



	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.word.AbstractMimeAdapter#tmpFileName()
	 */
	@Override
	protected String tmpFileName() {
		String currentDateString = DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSSS);
		String simpleTemplFileName = String.format("_%s.docx", currentDateString);
		return simpleTemplFileName;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.framework.word.AbstractMimeAdapter#toTempFile()
	 */
	@Override
	public File toTempFile() {
		String content = getContent();

		MSWord msWord = new MSWord();
		msWord.addText(content);

		File mimeFile = createNewTempFile();

		try {
			msWord.close(mimeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mimeFile;
	}

}
