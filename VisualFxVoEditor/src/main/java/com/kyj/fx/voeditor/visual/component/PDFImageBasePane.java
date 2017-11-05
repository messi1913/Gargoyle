/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   :  2016. 02. 22.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   :  KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.kyj.fx.voeditor.visual.framework.collections.LimitSizeLinkedHashMap;
import com.kyj.fx.voeditor.visual.util.PDFUtil;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * PDF 조회처리를 위한 패널
 *
 * @author KYJ
 *
 */
public class PDFImageBasePane extends BorderPane implements Closeable {
	private File pdfFile;
	private PDDocument doc;
	private PDFRenderer pdfRenderer;
	// private List<PDPage> allPages;
	private PDPageTree allPages;
	private int totalPageCount;

	private Map<Integer,ImageViewPane> cache = new LimitSizeLinkedHashMap<>(15);
	/**
	 * PDF파일로부터 정보를 읽어서 UI에 표시해준다.
	 *
	 * @param pdfFile
	 * @throws IOException
	 */
	public PDFImageBasePane(File pdfFile) throws IOException {
		if (pdfFile == null)
			throw new NullPointerException("parameter is null");
		else if (!pdfFile.exists())
			throw new FileNotFoundException(pdfFile.getName() + " does not exists.");

		doc = PDDocument.load(pdfFile);

		pdfRenderer = new PDFRenderer(doc);

		PDDocumentCatalog catal = doc.getDocumentCatalog();
		allPages = catal.getPages();

		this.totalPageCount = allPages.getCount();

		Pagination pagination = new Pagination(totalPageCount);
		pagination.setCache(true);
		pagination.setPageFactory(new Callback<Integer, Node>() {

			@Override
			public Node call(Integer param) {
				if(cache.containsKey(param))
					return cache.get(param);
				
				try {
					PDPage pdPage = allPages.get(param);
					PDRectangle mediaBox = pdPage.getMediaBox();
					// PDMetadata metadata = pdPage.getMetadata();
					// System.out.println(metadata);
					double width = mediaBox.getWidth();
					double height = mediaBox.getHeight();
					// Iterator<PDStream> contentStreams =
					// pdPage.getContentStreams();

//					InputStream contents = pdPage.getContents();
//					Iterator<PDStream> contentStreams = pdPage.getContentStreams();
//					while(contentStreams.hasNext())
//					{
//						PDStream next = contentStreams.next();
//						COSInputStream createInputStream = next.createInputStream();
//						return
//					}
					BufferedImage renderImage = pdfRenderer.renderImage(param, 2);
//					BufferedImage renderImage = pdfRenderer.renderImage(param, 500, ImageType.RGB);
					//BufferedImage renderImageWithDPI = pdfRenderer.renderImageWithDPI(param, 300, ImageType.RGB);
					//ImageIOUtil.writeImage(bim, pdfFilename + "-" + (pageCounter++) + ".png", 300);
					// ImageViewPane imageViewPane = null;
					// try (InputStream inputStream = pdPage.getContents()) {
					//
					// imageViewPane = new ImageViewPane(inputStream, width,
					// height);
					// }
					
					
					InputStream inputStream = PDFUtil.toInputStream(renderImage);
					ImageViewPane imageViewPane = new ImageViewPane(inputStream, width, height);
					
					return cache.put(param, imageViewPane);

				} catch (IOException e) {
					e.printStackTrace();
				}
				return new Label("Error");
			}
		});

		numberField.setPrefWidth(60);

		Button btnGo = new Button("Go");
		btnGo.setOnAction(event -> {

			String requestPage = numberField.getText();
			int parseInt = Integer.parseInt(requestPage);
			if (parseInt > -1 && parseInt <= totalPageCount) {
				pagination.currentPageIndexProperty().set(--parseInt);
			}

		});

		this.setCenter(pagination);
		this.setBottom(new HBox(5, numberField, btnGo));
	}

	private TextField numberField = new NumberTextField() ;

	/**
	 * pdf 파일
	 *
	 * @return
	 */
	public final File getPdfFile() {
		return pdfFile;
	}

	/**
	 * @return
	 */
	public final PDDocument getDoc() {
		return doc;
	}

	/**
	 * 전체 페이지 수 정보
	 *
	 * @return
	 */
	public final int getTotalPageCount() {
		return totalPageCount;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public void close() throws IOException {
		if (doc != null)
			doc.close();

	}

}
