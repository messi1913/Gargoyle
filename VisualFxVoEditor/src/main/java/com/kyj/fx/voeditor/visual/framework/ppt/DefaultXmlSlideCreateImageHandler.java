/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.ppt
 *	작성일   : 2017. 10. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.ppt;

import java.awt.Color;
import java.awt.Dimension;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
public class DefaultXmlSlideCreateImageHandler extends CreateXmlSlideImageHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXmlSlideCreateImageHandler.class);

	public DefaultXmlSlideCreateImageHandler(XMLSlideShow ppt) {
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
			XMLSlideShow ppt = getPpt();
			Dimension pgsize = ppt.getPageSize();

			try {

				// LOGGER.debug("{} {} ", pgsize.width, pgsize.height);
				// BufferedImage img = new BufferedImage(pgsize.width,
				// pgsize.height, BufferedImage.TYPE_4BYTE_ABGR);
				// Graphics2D graphics = img.createGraphics();

				// BufferedImage background = ImageIO.read(new
				// File("C:\\Users\\KYJ\\Pictures\\10.png"));
				// background.Graphics graphics = background.getGraphics();
				// Rectangle2D.Float s = new Rectangle2D.Float(0, 0,
				// pgsize.width, pgsize.height);
				// graphics.fill(s);

				// graphics.drawImage(background, 0, 0, Color.black, new
				// ImageObserver() {
				//
				// @Override
				// public boolean imageUpdate(Image img, int infoflags, int x,
				// int y, int width, int height) {
				// LOGGER.debug(img.toString());
				// return true;
				// }
				// });

				// graphics.setColor(Color.BLACK);
				XSLFSlide slide = getSlide();
//				XSLFAutoShape createAutoShape = slide.createAutoShape();
//				createAutoShape.setFillColor(Color.BLACK);
				// slide.addShape(shape);

				slide.getBackground().setFillColor(Color.BLACK);
				// slide.draw(graphics);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
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
