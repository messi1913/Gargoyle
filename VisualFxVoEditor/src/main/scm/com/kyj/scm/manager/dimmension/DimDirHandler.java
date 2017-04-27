/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 24.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.List;

import com.kyj.scm.manager.core.commons.ScmDirHandler;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.RepositoryFolder;

/**
 * 디멘전 트리 탐색 처리 핸들러
 * @author KYJ
 *
 */
public abstract class DimDirHandler implements ScmDirHandler<RepositoryFolder> {

	/**
	 * parent폴더 기준,
	 * filter처리된 아이템 대상으로 하위 아이템들을 순회하는 코드
	 */
	@Override
	public void accept(RepositoryFolder parent) {
		Filter itemFilter = itemFilter();
		List<DimensionsRelatedObject> childItems = parent.getChildItems(/*필터 적용*/ itemFilter);
		childItems.stream().map(f -> (ItemRevision) f.getObject()).forEach(r -> handle(r));
	}

	/**
	 * 디멘전 디렉토리 탐색중 필요한 필터처리내용을 구현
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 27. 
	 * @return
	 */
	public Filter itemFilter() {
		return new Filter();
	}

	/* 해당 디렉토리를 탐색할지 여부를 결정함.
	 * (non-Javadoc)
	 * @see com.kyj.scm.manager.core.commons.ScmDirHandler#test(java.lang.Object)
	 */
	public abstract boolean test(RepositoryFolder entry);

	/**
	 * 탐색된 디렉토리에 대한 처리내용을 구현함.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 4. 24. 
	 * @param r
	 */
	public abstract void handle(ItemRevision r);
}
