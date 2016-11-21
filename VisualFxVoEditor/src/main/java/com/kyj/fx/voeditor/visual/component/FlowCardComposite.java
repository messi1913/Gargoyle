/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import com.jfoenix.controls.JFXMasonryPane;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * @author KYJ
 *
 */

public class FlowCardComposite extends BorderPane {

	private ScrollPane scrollPane;

	private JFXMasonryPane masonryPane;


	/**
	 * @param nodeConverter
	 */
	public FlowCardComposite() {

		scrollPane = new ScrollPane();
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		StackPane stackPane = new StackPane(scrollPane);
		stackPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		masonryPane = new JFXMasonryPane();

		scrollPane.setContent(masonryPane);

		setCenter(stackPane);

		initialize();

		masonryPane.setCache(false);
		setStyle("-fx-background-color : #292929");
	}

	public void setLimitColumn(int limitColumn) {
		masonryPane.setLimitColumn(limitColumn);

	}

	public void setLimitRow(int limitRow) {
		masonryPane.setLimitRow(limitRow);
	}

	public ObservableList<Node> getFlowChildrens() {
		ObservableList<Node> children2 = this.masonryPane.getChildren();
		return children2;
	}

	public void reflesh() {
		this.scrollPane.requestLayout();
	}

	public void initialize() {
		masonryPane.autosize();
	}

}
