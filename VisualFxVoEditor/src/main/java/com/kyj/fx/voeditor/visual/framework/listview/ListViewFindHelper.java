/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.listview
 *	작성일   : 2017. 6. 5.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.listview;

import com.kyj.fx.voeditor.visual.component.popup.ListViewSearchAndReplaceView;

import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * @author KYJ
 *
 */
public class ListViewFindHelper {

	public static <T> void install(ListView<T> listview, StringConverter<T> converter) {
		installKeyHanlder(listview, converter);
	}

	private static <T> void installKeyHanlder(ListView<T> listview, StringConverter<T> converter) {
		listview.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {

			if (ev.getCode() == KeyCode.F && ev.isControlDown()) {
				ListViewSearchAndReplaceView<T> finder = new ListViewSearchAndReplaceView<>(listview, converter);
				
				//문자열을 찾은다음 할 행동.
				finder.setOnFound(t -> {
					int rowIndex = t.getRowIndex();
					listview.getSelectionModel().select(rowIndex);
					listview.scrollTo(rowIndex);
				});
				
				finder.show();
			}
		});
	}
}
