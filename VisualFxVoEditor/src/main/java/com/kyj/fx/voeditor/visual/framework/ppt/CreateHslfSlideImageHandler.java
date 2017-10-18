/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.ppt
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.ppt;

import java.io.File;
import java.io.IOException;

import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureData.PictureType;

import com.kyj.fx.voeditor.visual.util.FileUtil;

/**
 * @author KYJ
 *
 */
public abstract class CreateHslfSlideImageHandler {

	private HSLFSlideShow ppt;
	private HSLFSlide slide;

	public CreateHslfSlideImageHandler(HSLFSlideShow ppt) {
		this.ppt = ppt;
		this.slide = createSlide(this.ppt);
	}

	protected abstract HSLFSlide createSlide(HSLFSlideShow ppt);

	public HSLFPictureShape createImage(File imageFile) {
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

	public final HSLFSlideShow getPpt() {
		return ppt;
	}

	public final HSLFSlide getSlide() {
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
	public HSLFPictureShape createImage(File imageFile, PictureType format) {
		if (format == null)
			return null;
		try {

			HSLFPictureData addPicture = this.ppt.addPicture(FileUtil.getBytes(imageFile), format);
			return this.slide.createPicture(addPicture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
