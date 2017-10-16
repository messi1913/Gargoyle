/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.ppt
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.ppt;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;

import org.apache.poi.sl.usermodel.PictureData.PictureType;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

/**
 * @author KYJ
 *
 */
public class DefaultPPTCreateImageHandler extends PPTCreateImageHandler {

	public DefaultPPTCreateImageHandler(XMLSlideShow ppt) {
		super(ppt);
	}

	@Override
	protected XSLFSlide createSlide(XMLSlideShow ppt) {
		XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
		XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
		return ppt.createSlide(layout);
	}

	@Override
	public XSLFPictureShape createImage(File imageFile, PictureType format) {

		{
			XSLFTextShape placeholder = getSlide().getPlaceholder(0);
			placeholder.setText(imageFile.getName());
		}

		{

			XSLFTextShape bodyShape = getSlide().getPlaceholder(1);
			// placeholder.clearText();
			// placeholder.setText("Hello World\nMy Name is Kim.\nThis is PPT
			// Wrtie Test.\n김영준\n0123456789");

			List<XSLFShape> shapes = getSlide().getShapes();
			XSLFShape xslfShape = shapes.get(1);
			Rectangle2D anchor = xslfShape.getAnchor();

			XSLFPictureShape shape = super.createImage(imageFile, getType(imageFile));
			if (shape != null) {
				shape.setAnchor(anchor);
			}
			getSlide().removeShape(bodyShape);
		}

		return null;

	}

}
