/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.List;

/**
 *
 *
 * @author KYJ
 *
 */
public interface SCMItem<T> {
	/**
	 * 자식 노드를 호출할 내용을 정의한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @return
	 */
	public List<T> getChildrens();
}
