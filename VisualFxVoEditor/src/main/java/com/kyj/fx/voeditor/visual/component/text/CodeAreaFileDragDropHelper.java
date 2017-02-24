/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.text
 *	작성일   : 2016. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

/**
 * @author KYJ
 *
 */
public class CodeAreaFileDragDropHelper extends AbstractFileDragDropHelper<CodeArea> {

	private static Logger LOGGER = LoggerFactory.getLogger(CodeAreaFileDragDropHelper.class);

	public CodeAreaFileDragDropHelper(CodeArea codeArea) {
		super(codeArea);
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

				if (f.length() > dragDropLimitSize()) {

					DialogUtil.showMessageDialog("파일 용량이 너무 큽니다.");
					return;

				}

				try (FileInputStream is = new FileInputStream(f)) {
					setContent(FileUtil.readToString(is));
				} catch (Exception e) {
					LOGGER.error(ValueUtil.toString(e));
				}

			});

			ev.setDropCompleted(true);
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

	/**
	 * 드래그 드롭시 파일 제한 사이즈 정의
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 10.
	 * @return
	 */
	protected long dragDropLimitSize() {
		return 5 * 1024 * 1024;
	}

	/*********************************************************/

}
