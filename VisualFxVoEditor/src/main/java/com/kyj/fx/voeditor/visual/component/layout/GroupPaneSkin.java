/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.layout
 *	작성일   : 2016. 3. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.layout;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * 그룹패널
 *
 * @author KYJ
 *
 */

public class GroupPaneSkin implements Skin<GroupPane> {

	private GroupPane control;

	private StackPane rootStackPane;

	private AnchorPane contentPane;

	/**
	 * 그룹패널 타이틀.
	 *
	 * @최초생성일 2016. 3. 31.
	 */
	private Label lblTitle;

	private final ChangeListener<? super String> titleChangeListener = (oba, oldTitle, newTitle) -> {
		lblTitle.setText(newTitle);
	};

	private final ChangeListener<? super Number> contentLayoutHeightListener = (oba, oldheight, newheight) -> {
		Double topAnchor = AnchorPane.getTopAnchor(control.getContent());
		AnchorPane.setTopAnchor(control.getContent(), (topAnchor + newheight.doubleValue()));
	};

	// private ObjectProperty<Node> contentProperty = new
	// SimpleObjectProperty<>();

	/**
	 * @param control
	 */
	protected GroupPaneSkin(GroupPane control) {
		this.control = control;
		// contentProperty.bind(control.contentProperty());

		contentPane = new AnchorPane(control.getContent());
		contentPane.setStyle("-fx-background-color:white;-fx-border-color:black;");
		StackPane.setMargin(contentPane, new Insets(15.0, 5.0, 5.0, 5.0));

		StackPane contentStackPane = new StackPane(contentPane);
		lblTitle = new Label(control.getTitle());
		lblTitle.getStyleClass().set(0, ".fx-group-label-title");

		lblTitle.setPadding(new Insets(5));
		StackPane.setMargin(lblTitle, new Insets(7.0, 7.0, 0, 7.0));
		StackPane.setAlignment(lblTitle, Pos.TOP_LEFT);
		lblTitle.setStyle("-fx-background-color:white;");

		rootStackPane = new StackPane();
		rootStackPane.setPrefSize(600d, 500d);
		rootStackPane.getChildren().add(contentStackPane);
		rootStackPane.getChildren().add(lblTitle);
		rootStackPane.setStyle("-fx-background-color:white");

		AnchorPane.setTopAnchor(control.getContent(), 19d);
		AnchorPane.setLeftAnchor(control.getContent(), 5d);
		AnchorPane.setRightAnchor(control.getContent(), 5d);
		AnchorPane.setBottomAnchor(control.getContent(), 5d);

		// [시작] 리스너 등록
		this.control.titlePropert().addListener(titleChangeListener);
		lblTitle.prefWidthProperty().addListener(contentLayoutHeightListener);
	}

	/**
	 * 그룹제목이 보일 위치를 지정한다.
	 *
	 * 디폴트 TOP_LEFT
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 31.
	 * @param pos
	 */
	public void setTitlePos(Pos pos) {
		StackPane.setAlignment(lblTitle, pos);
	}

	/**
	 * 그룹제목 보임여부를 지정한다.
	 *
	 * 디폴트 true
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 31.
	 * @param visible
	 */
	public void setTitleVisible(boolean visible) {
		lblTitle.visibleProperty().set(visible);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Node getNode() {
		return rootStackPane;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public GroupPane getSkinnable() {
		return this.control;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void dispose() {
		// Nothing....
	}

}
