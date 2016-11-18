/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import java.util.List;

/**
 * 실시간검색어 그룹단위 아이템 VO
 * @author KYJ
 *
 */
public class RealtimeSearchVO {

	private String title;
	private List<RealtimeSearchItemVO> items;

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the items
	 */
	public final List<RealtimeSearchItemVO> getItems() {
		return items;
	}

	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param items the items to set
	 */
	public final void setItems(List<RealtimeSearchItemVO> items) {
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("RealtimeSearchVO [title=%s, items=%s]", title, items);
	}

}
