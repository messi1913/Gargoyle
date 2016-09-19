/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MacroFavorTreeItemCreator {

	private AbstractMacroFavorDAO dao;

	public MacroFavorTreeItemCreator(Supplier<Connection> connectionSupplier) {
		dao = new MacroFavorDAO(connectionSupplier);
	}

	class ReadChildrens implements Function<MacroItemVO, List<MacroItemVO>> {

		@Override
		public List<MacroItemVO> apply(MacroItemVO item) {
			try {
				if (item == null || ValueUtil.isEmpty(item.getMacroId())) {
					return dao.getRoots();
				} else {
					return dao.getChildrens(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return Collections.emptyList();
		}
	}

	/**
	 * 자식 요소들을 만드는 구체적인 처리로직을 기술.
	 * 
	 * @최초생성일 2016. 9. 4.
	 */
	private Function<MacroItemVO, List<MacroItemVO>> childrenFunc = new ReadChildrens();

	/**
	 * 파일 트리를 생성하기 위한 노드를 반환한다.
	 *
	 * @Date 2015. 10. 14.
	 * @param f
	 * @return
	 * @User KYJ
	 */
	public TreeItem<MacroItemVO> createRoot(final MacroItemVO f) {

		TreeItem<MacroItemVO> treeItem = new TreeItem<MacroItemVO>(f) {
			private boolean isLeaf;
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<MacroItemVO>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					MacroItemVO f = getValue();

					if (f == null || "F".equals(f.getMacroItemType()))
						isLeaf = true;
					else
						isLeaf = false;
				}
				return isLeaf;
			}

			private ObservableList<TreeItem<MacroItemVO>> buildChildren(TreeItem<MacroItemVO> treeItem) {

				MacroItemVO f = treeItem.getValue();
				if (f == null) {
					return FXCollections.emptyObservableList();
				}

				List<MacroItemVO> childrens = childrenFunc.apply(f);
				if (childrens.isEmpty()) {
					return FXCollections.emptyObservableList();
				} else {
					ObservableList<TreeItem<MacroItemVO>> children = FXCollections.observableArrayList();
					for (MacroItemVO child : childrens) {
						TreeItem<MacroItemVO> createNode = createRoot(child);
//						createNode.setExpanded(true);
						children.add(createNode);
					}

					return children;
				}
			}

		};
//		treeItem.setExpanded(true);

		return treeItem;
	}

}
