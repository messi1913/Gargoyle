/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.ppt
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.ppt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hslf.usermodel.HSLFFill;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideMaster;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.sl.usermodel.TextParagraph.TextAlign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class DefaultHslfSlideCreateImageHandler extends CreateHslfSlideImageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHslfSlideCreateImageHandler.class);

	private File backgroundFile;

	public DefaultHslfSlideCreateImageHandler(HSLFSlideShow ppt) {
		super(ppt);
	}

	@Override
	protected HSLFSlide createSlide(HSLFSlideShow ppt) {
		HSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);

		return ppt.createSlide();
	}

	public void background(Color color) {

		try {
			HSLFSlide slide = getSlide();
			HSLFFill fill = slide.getBackground().getFill();
			fill.setFillType(HSLFFill.FILL_SHADE);
			fill.setBackgroundColor(color);
			fill.setForegroundColor(color);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void backgroundImage() {

		try {
			HSLFSlideShow ppt = getPpt();
			HSLFSlide slide = getSlide();

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

	@Override
	public HSLFPictureShape createImage(File imageFile, PictureType format) {

		HSLFSlideShow ppt = getPpt();
		HSLFSlide slide = getSlide();

		// Set Title
		HSLFTextBox addTitle = slide.addTitle();
		addTitle.setText(imageFile.getName());

		// Font Color
		List<HSLFTextParagraph> textParagraphs = addTitle.getTextParagraphs();
		HSLFTextParagraph hslfTextParagraph = textParagraphs.get(0);
		hslfTextParagraph.setTextAlign(TextAlign.CENTER);
		HSLFTextRun run = hslfTextParagraph.getTextRuns().get(0);
		run.setFontColor(Color.WHITE);

		// Image Processing.
		if (format != null) {

			Dimension pageSize = ppt.getPageSize();

			try {
				// load image file.
				HSLFPictureData picData = ppt.addPicture(imageFile, format);
				// get shape
				HSLFPictureShape picShape = new HSLFPictureShape(picData);

				int y = (int) pageSize.getHeight() / 2;
				int width = (int) (pageSize.getWidth() - 30.0);
				int height = (int) (pageSize.getHeight() / 3.0);

				int x = 15;

				// location 위치를 잡아줌.
				picShape.setAnchor(new Rectangle(x, y, width, height));

				// finally add image 그림 추가
				slide.addShape(picShape);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	public File getBackgroundFile() {
		return backgroundFile;
	}

	public void setBackgroundFile(File backgroundFile) {
		this.backgroundFile = backgroundFile;

	}

}
