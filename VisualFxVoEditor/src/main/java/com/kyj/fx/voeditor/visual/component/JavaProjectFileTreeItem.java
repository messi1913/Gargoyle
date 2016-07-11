/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 07. 11
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.InputStream;

import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * 자바프로젝트 트리Item 처리 기반 Tree
 *
 * @author KYJ
 *
 */
public class JavaProjectFileTreeItem extends FileTreeItem {

	public JavaProjectFileTreeItem(FileWrapper fileWrapper) {
		super(fileWrapper);
	}

	@Override
	protected Node createGraphcis(FileWrapper fileWrapper) {
		InputStream imageStream = ClassLoader.getSystemClassLoader().getResourceAsStream("META-INF/images/eclipse/project.gif");
		ImageView createImageView = FxUtil.createImageView(imageStream);
		String meta = String.format("[%b][%b]", fileWrapper.isJavaProjectFile(), fileWrapper.isSVNConnected());
		return new HBox(createImageView, new Label(fileWrapper.getFile().getName()), new Label(meta));
	}

	@Override
	protected String getMetadata() {
		return String.format("[%b][%b]", getValue().isJavaProjectFile(), getValue().isSVNConnected());
	}

}