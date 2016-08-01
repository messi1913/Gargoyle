/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 07. 04.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.Collections;
import java.util.List;

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
	 *이 함수에서 리턴되는 노드가 트리를 구성하는 주 Node가 된다.
	 *
	 * @param fileWrapper
	 * @return
	 ********************************/
	protected Node createGraphcis(FileWrapper fileWrapper) {
		String meta = getMetadata();
		ImageView createImageView = getImage(fileWrapper);
		HBox value = new HBox(createImageView, new Label(fileWrapper.getFile().getName()), new Label(meta) );
		List<Node> createAttachLabels = createAttachNodes();
		if(createAttachLabels !=null && !createAttachLabels.isEmpty())
			value.getChildren().addAll(createAttachLabels);
		return value;
	}


	/********************************
	 * 작성일 :  2016. 7. 27. 작성자 : KYJ
	 *
	 * 추가적으로 덧붙일 노드정보가 있으면 오버라이드해서 사용할 수 있도록한다.
	 *
	 * @return
	 ********************************/
	protected List<Node> createAttachNodes(){
		return Collections.emptyList();
	}
	/********************************
	 * 작성일 :  2016. 7. 27. 작성자 : KYJ
	 *
	 * 파일 이미지 리턴.
	 *
	 * @param fileWrapper
	 * @return
	 ********************************/
	protected ImageView getImage(FileWrapper fileWrapper) {
		ImageView createImageView = FxUtil.createImageIconView(fileWrapper.getFile());
		return createImageView;
	}

	/********************************
	 * 작성일 :  2016. 7. 27. 작성자 : KYJ
	 *
	 * 노드의 css명을 리턴
	 * @return
	 ********************************/
	protected String graphicsCssId() {
		return "fiile-tree-item";
	}

	/********************************
	 * 작성일 :  2016. 7. 27. 작성자 : KYJ
	 *
	 * UI에 Diplay되는 텍스트를 리턴. 이클립스로 예를들어 svn이 연결되면 뒤에붙는 메타정보를 표현.
	 *
	 * @return
	 ********************************/
	protected String getMetadata() {
		return "";
	}
}