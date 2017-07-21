/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import com.kyj.fx.voeditor.visual.framework.PrimaryStageCloseable;

/**
 *
 *  페이징이 포함된 뷰에서 사용할 SimpleTextView
 * @author KYJ
 *
 */
public class PagedSimpleTextView extends SimpleTextView implements PrimaryStageCloseable {

	private BigTextView bigTextView;

	public PagedSimpleTextView(BigTextView bigTextView, String content, boolean showButtons) {
		super(content, showButtons);
		this.bigTextView = bigTextView;
	}

	@Override
	public void initHelpers() {
		this.helper = new PagedCodeAreaHelper<>(bigTextView, codeArea);
	}
}
