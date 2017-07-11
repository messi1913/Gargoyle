/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.image
 *	작성일   : 2017. 5. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.image;

import org.controlsfx.control.CheckModel;

import com.jfoenix.controls.JFXMasonryPane;
import com.kyj.fx.voeditor.visual.util.FxCollectors;

import javafx.collections.ObservableList;

/**
 * @author KYJ
 *
 */
public class JFXMasonryPaneWrapper<T extends NodeWrapper> extends JFXMasonryPane {

	private CheckModel<T> checkModel;

	public JFXMasonryPaneWrapper() {
		this.checkModel = new CheckModel<T>() {

			@Override
			public boolean isEmpty() {
				return getChildren().isEmpty();
			}

			@Override
			public boolean isChecked(T item) {
				return item.isChecked();
			}

			@Override
			public int getItemCount() {
				return getChildren().size();
			}

			@Override
			public ObservableList<T> getCheckedItems() {
				return getChildren().stream().map(c -> (T) c).filter(c -> c.isChecked()).collect(FxCollectors.toObservableList());
			}

			@Override
			public void clearChecks() {
				getChildren().stream().map(c -> (T) c).forEach(c -> c.setCheck(false));
			}

			@Override
			public void clearCheck(NodeWrapper item) {
				item.clearCheck();

			}

			@Override
			public void checkAll() {
				getChildren().stream().map(c -> (T) c).forEach(this::check);
			}

			@Override
			public void check(NodeWrapper item) {
				item.setCheck(true);

			}
		};
	}

	public CheckModel<T> getCheckedModel() {
		return this.checkModel;
	}

}
