/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.ppt
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.ppt;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.kyj.fx.voeditor.visual.util.FileUtil;

/**
 * @author KYJ
 *
 */
public abstract class PPTCreateImageHandler {

	private XMLSlideShow ppt;
	private XSLFSlide slide;

	public PPTCreateImageHandler(XMLSlideShow ppt) {
		this.ppt = ppt;
		this.slide = createSlide(this.ppt);
	}

	protected abstract XSLFSlide createSlide(XMLSlideShow ppt);

	public XSLFPictureShape createImage(File imageFile) {
		return createImage(imageFile, getType(imageFile));
	}

	protected PictureType getType(File imageFile) {

		try {
			String fileExtension = FileUtil.getFileExtension(imageFile).toUpperCase();
			PictureType valueOf = PictureData.PictureType.valueOf(fileExtension);
			return valueOf;
		} catch (IllegalArgumentException e) {
			// Nothing...
		}
		return null;
	}

	public final XMLSlideShow getPpt() {
		return ppt;
	}

	public final XSLFSlide getSlide() {
		return slide;
	}

	/**
	 * 
	 * ppt문서에 이미지 메타정보를 추가하고
	 * 
	 * 슬라이드에 이미지를 추가한다.
	 * 
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 16.
	 * @param imageFile
	 * @param format
	 * @return
	 */
	public XSLFPictureShape createImage(File imageFile, PictureType format) {
		if (format == null)
			return null;
		try {
			XSLFPictureData addPicture = this.ppt.addPicture(FileUtil.getBytes(imageFile), format);
			return this.slide.createPicture(addPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
