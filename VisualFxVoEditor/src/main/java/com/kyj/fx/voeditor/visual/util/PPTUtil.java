/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hslf.usermodel.HSLFSlideShow;
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

	public static void createHSLFSlideShowFileSimpleImages(File pptFile, File... images) throws IOException {
		HSLFSlideShow ppt = new HSLFSlideShow();
		for (File image : images) {

			DefaultHslfSlideCreateImageHandler handler = new DefaultHslfSlideCreateImageHandler(ppt);
			handler.setBackgroundFile(new File("C:\\Users\\KYJ\\Pictures\\10.png"));
			handler.backgroundImage();
			handler.createImage(image);
		}

		try (FileOutputStream stream = new FileOutputStream(pptFile)) {
			ppt.write(stream);
		}
	}

}
