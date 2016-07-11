/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 07. 04.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 자바프로젝트 트리Item 처리 기반 Tree
 *
 * @author KYJ
 *
 */
public class FileTreeItem extends TreeItem<FileWrapper> {

	public FileTreeItem(FileWrapper fileWrapper) {
		super(fileWrapper);
		updateGraphics(fileWrapper);
	}

	/********************************
	 * 작성일 : 2016. 7. 10. 작성자 : KYJ
	 *
	 * UI에 보여질 Dispay 처리.
	 * 
	 * @param fileWrapper
	 ********************************/
	private void updateGraphics(FileWrapper fileWrapper) {
		Node value = createGraphcis(fileWrapper);
		value.getStyleClass().add(graphicsCssId());
		setGraphic(value);
	}

	/********************************
	 * 작성일 : 2016. 7. 11. 작성자 : KYJ
	 *
	 *
	 * @param fileWrapper
	 * @return
	 ********************************/
	protected Node createGraphcis(FileWrapper fileWrapper) {
		String meta = getMetadata();
		ImageView createImageView = FxUtil.createImageIconView(fileWrapper.getFile());
		HBox value = new HBox(createImageView, new Label(fileWrapper.getFile().getName()), new Label(meta));
		return value;
	}

	protected String graphicsCssId() {
		return "fiile-tree-item";
	}

	protected String getMetadata() {
		return "";
	}
}