/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.node
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.item.node;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

/**
 * @author KYJ
 *
 */
public class JavaRunableItem extends AbstractRunItem {

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.node.IRunableItem#getRunableType()
	 */
	@Override
	public RunableType getRunableType() {
		return RunableType.JAVA;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.node.IRunableItem#getTabs()
	 */
	@Override
	public ObservableList<Tab> getTabs() {
		return null;
	}

	public String getProject() {
		return null;
	}

	public String getClassName() {
		return null;
	}

	public String getMethodName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem#getTitle()
	 */
	@Override
	public String getTitle() {

		return "Java Run";
	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem#revert()
	 */
	@Override
	public void revert() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem#apply()
	 */
	@Override
	public void apply() {
		// TODO Auto-generated method stub

	}

}
