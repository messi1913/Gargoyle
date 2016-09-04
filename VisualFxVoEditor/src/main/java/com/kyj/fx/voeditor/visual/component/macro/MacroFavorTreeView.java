/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import com.google.common.base.Function;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/***************************
 * 
 * Mactro 처리할 SQL에 대한 즐겨찾기 기능을 지원하기 위한 뷰
 * 
 * @author KYJ
 *
 ***************************/
public class MacroFavorTreeView extends TreeView<MacroItemVO> implements Callback<TreeView<MacroItemVO>, TreeCell<MacroItemVO>> {

	public MacroFavorTreeView() {
		setCellFactory(this);
	}

	/**
	 * String Conveter
	 * 
	 * @최초생성일 2016. 9. 3.
	 */
	private Function<MacroItemVO, String> stringConverter = new Function<MacroItemVO, String>() {

		@Override
		public String apply(MacroItemVO input) {
			if (input == null)
				return "";
			return input.getName();
		}
	};

	@Override
	public TreeCell<MacroItemVO> call(TreeView<MacroItemVO> param) {
		TreeCell<MacroItemVO> treeCell = createTreeCell();
		return treeCell;
	}

	protected TreeCell<MacroItemVO> createTreeCell() {
		return new TreeCell<MacroItemVO>() {

			@Override
			protected void updateItem(MacroItemVO item, boolean empty) {
				super.updateItem(item, empty);

				if (!empty) {
					String apply = stringConverter.apply(item);
					setText(apply);
				} else {
					setText("");
				}

			}
		};
	}
}
