/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFFill;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

import com.kyj.fx.voeditor.visual.framework.ppt.CreateXmlSlideImageHandler;
import com.kyj.fx.voeditor.visual.framework.ppt.DefaultHslfSlideCreateImageHandler;
import com.kyj.fx.voeditor.visual.framework.ppt.DefaultXmlSlideCreateImageHandler;

/**
 * @author KYJ
 *
 */
public class PPTUtil {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 18.
	 * @param pptFile
	 * @param images
	 * @throws IOException
	 * @Deprecated 처리되지않는 함수 존재.
	 */
	@Deprecated
	public static void createXMLSlideShowFileSimpleImages(File pptFile, File... images) throws IOException {

		XMLSlideShow ppt = new XMLSlideShow();
		for (File image : images) {

			CreateXmlSlideImageHandler handler = new DefaultXmlSlideCreateImageHandler(ppt);
			handler.createImage(image);
		}

		try (FileOutputStream stream = new FileOutputStream(pptFile)) {
			ppt.write(stream);
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 19.
	 * @param pptFile
	 * @param images
	 * @throws IOException
	 */
	public static void createHSLFSlideShowFileSimpleImages(File pptFile, File... images) throws IOException {
		HSLFSlideShow ppt = new HSLFSlideShow();

		File backgroundFile = new File("C:\\Users\\KYJ\\Pictures\\10.png");
		// [START] PPT Title
		HSLFSlide titleSlide = ppt.createSlide();
		HSLFTextBox addTitle = titleSlide.addTitle();
		addTitle.setText(pptFile.getName());

		// Font Color
		List<HSLFTextParagraph> textParagraphs = addTitle.getTextParagraphs();
		HSLFTextParagraph hslfTextParagraph = textParagraphs.get(0);
		hslfTextParagraph.setTextAlign(TextAlign.CENTER);
		HSLFTextRun run = hslfTextParagraph.getTextRuns().get(0);
		run.setFontColor(Color.WHITE);
		backgroundImage(ppt, titleSlide, backgroundFile);
		// [END]

		// [START] PPT Content
		for (File image : images) {
			DefaultHslfSlideCreateImageHandler handler = new DefaultHslfSlideCreateImageHandler(ppt);
			HSLFSlide slide = handler.getSlide();
			handler.setPptFile(pptFile);
			// handler.setBackgroundFile(backgroundFile);

			backgroundImage(ppt, slide, backgroundFile);
			handler.createImage(image);
		}
		// [END]

		try (FileOutputStream stream = new FileOutputStream(pptFile)) {
			ppt.write(stream);
		}
	}

	/**
	 * PPT Slide에 backgroundFile 이미지를 그려 넣는다. <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 19.
	 * @param ppt
	 * 		ppt File
	 * @param slide
	 * 		ppt Slide File
	 * @param backgroundFile
	 * 		Background Image File.
	 */
	public static void backgroundImage(HSLFSlideShow ppt, HSLFSlide slide, File backgroundFile) {

		try {

			// 이부분 반드시 호출
			// This slide has its own background.
			// Without this line it will use master's background.
			slide.setFollowMasterBackground(false);

			HSLFFill fill = slide.getBackground().getFill();
			HSLFPictureData pd;
			pd = ppt.addPicture(backgroundFile, PictureData.PictureType.PNG);
			fill.setFillType(HSLFFill.FILL_PICTURE);
			fill.setPictureData(pd);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
