/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

/**
 * 실시간 검색어 단위 아이템 VO
 * @author KYJ
 *
 */
public class RealtimeSearchItemVO {

	private String keyword;
	private int rank;
	private String link;

	/**
	 * @return the rank
	 */
	public final int getRank() {
		return rank;
	}

	/**
	 * @return the keyword
	 */
	public final String getKeyword() {
		return keyword;
	}

	/**
	 * @return the link
	 */
	public final String getLink() {
		return link;
	}

	/**
	 * @param rank the rank to set
	 */
	public final void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @param keyword the keyword to set
	 */
	public final void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @param link the link to set
	 */
	public final void setLink(String link) {
		this.link = link;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("RealtimeSearchItemVO [keyword=%s, rank=%s, link=%s]", keyword, rank, link);
	}

}
