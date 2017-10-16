/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

import com.kyj.fx.voeditor.visual.framework.ppt.DefaultPPTCreateImageHandler;
import com.kyj.fx.voeditor.visual.framework.ppt.PPTCreateImageHandler;

/**
 * @author KYJ
 *
 */
public class PPTUtil {

	public static void createFileSimpleImages(File pptFile, File... images) throws FileNotFoundException, IOException {

		XMLSlideShow ppt = new XMLSlideShow();
		for (File image : images) {

			PPTCreateImageHandler handler = new DefaultPPTCreateImageHandler(ppt);
			handler.createImage(image);
		}
		
		try (FileOutputStream stream = new FileOutputStream(pptFile)) {
			ppt.write(stream);
		}

	}

}
