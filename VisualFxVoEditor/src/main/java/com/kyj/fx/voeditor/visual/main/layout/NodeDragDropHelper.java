/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.text.AbstractDragDropHelper;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

/**
 * 드래그 드롭 기능을 처리하는 공통 Helper 클래스.
 *
 * @author KYJ
 *
 */
public class NodeDragDropHelper extends AbstractDragDropHelper<Node> {

	private static Logger LOGGER = LoggerFactory.getLogger(NodeDragDropHelper.class);

	public NodeDragDropHelper(Node n) {
		super(n);
	}

	/*********************************************************/
	// 파일 드래그 드롭 처리.

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.text.AbstractDragDropHelper#onDagOver(javafx.scene.input.DragEvent)
	 */
	@Override
	public void onDagOver(DragEvent ev) {
		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {
			ev.acceptTransferModes(TransferMode.LINK);
			ev.consume();
		}

	}

	/* (non-Javadoc)
	 * @see com.kyj.fx.voeditor.visual.component.text.AbstractDragDropHelper#onDragDropped(javafx.scene.input.DragEvent)
	 */
	@Override
	public void onDragDropped(DragEvent ev) {
		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {

			List<File> files = ev.getDragboard().getFiles();

			// tbDatabase.getItems().add(e)
			files.stream().findFirst().ifPresent(f -> {

				SystemLayoutViewController controller = SharedMemory.getSystemLayoutViewController();
				controller.openFile(f);

			});

			ev.setDropCompleted(true);
			ev.consume();
		}

	}

	/*********************************************************/

}
