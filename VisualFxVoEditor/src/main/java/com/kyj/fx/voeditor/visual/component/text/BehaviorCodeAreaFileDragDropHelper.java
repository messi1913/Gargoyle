/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2017. 12. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.util.List;

import org.fxmisc.richtext.CodeArea;

import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

/**
 * @author KYJ
 *@Deprecated 사용하지않음.
 */
@Deprecated
public class BehaviorCodeAreaFileDragDropHelper extends AbstractFileDragDropHelper<CodeArea> {

	public BehaviorCodeAreaFileDragDropHelper(CodeArea node) {
		super(node);
	}

	@Override
	public void onDagOver(DragEvent ev) {
		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {
			ev.acceptTransferModes(TransferMode.LINK);
			ev.consume();
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 14.
	 * @param content
	 */
	public void setContent(String content) {
		node.getUndoManager().mark();
		node.clear();
		node.replaceText(0, 0, content);
		node.getUndoManager().mark();
	}

	@Override
	public void onDragDropped(DragEvent ev) {

		if (ev.isConsumed())
			return;

		if (ev.getDragboard().hasFiles()) {

			List<File> files = ev.getDragboard().getFiles();

			// tbDatabase.getItems().add(e)
			files.stream().filter(f -> f.getName().endsWith(".wib")).findFirst().ifPresent(f -> {

				BehaviorReader reader = new BehaviorReader(f);
				setContent(reader.readBehavior());
				// String encoding = "UTF-8";
				// try {
				// encoding = FileUtil.findEncoding(f);
				// } catch (IOException e1) {
				// // Not Important.
				// }
				//
				// try (FileInputStream is = new FileInputStream(f)) {
				// setContent(FileUtil.readToString(is, encoding));
				// } catch (Exception e) {
				// LOGGER.error(ValueUtil.toString(e));
				// }

			});

			ev.setDropCompleted(true);
			ev.consume();
		}

	}

}
