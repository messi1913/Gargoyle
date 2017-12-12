/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 8.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.util.Optional;

import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.XMLUtils;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;

/**
 * @author KYJ
 *
 */
public class BehaviorReferenceTab extends Tab {

	private SplitPane content;
	private BehaviorReferenceVO behaviorReferenceVO;
	XMLEditor xmlEditor = new XMLEditor();
	BehaviorTextArea txtScript = new BehaviorTextArea();

	public BehaviorReferenceTab(BehaviorReferenceVO behaviorReferenceVO) {
		this.behaviorReferenceVO = behaviorReferenceVO;
		setText(this.behaviorReferenceVO.getTxtFileName());
		setClosable(true);

		addNewContent();
		readAynch();

	}

	public BehaviorReferenceVO getValue() {
		return behaviorReferenceVO;
	}

	protected void readAynch() {
		ExecutorDemons.getGargoyleSystemExecutorSerivce().execute(() -> {
			BehaviorReader reader = new BehaviorReader(this.behaviorReferenceVO.getWib());
			String cont = reader.readBehavior();

			// set Full Text
			Platform.runLater(() -> {
				xmlEditor.setText(cont);
				xmlEditor.moveToLine(1);
			});

			// read script
			Optional<String> xpathText = XMLUtils.toXpathText(cont, "//FunctionFile/text()");
			xpathText.ifPresent(str -> {
				Platform.runLater(() -> {
					txtScript.replaceText(str);
					txtScript.moveToLine(1);
				});
			});

		});
	}

	protected void addNewContent() {
		content = new SplitPane(txtScript, xmlEditor);
		content.setOrientation(Orientation.HORIZONTAL);
		content.setDividerPositions(0.7d, 0.3d);
		setContent(content);
	}

//	public SplitPane getContent() {
//		return content;
//	}

}
