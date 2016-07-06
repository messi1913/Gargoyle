/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.Collections;
import java.util.List;

/**
 * SCMITEM 정의
 *
 * @author KYJ
 *
 */
public class SVNRootItem extends SVNItem {

	public SVNRootItem() {
		super(null);
	}

	@Override
	public List<SVNItem> getChildrens() {
		return Collections.emptyList();
	}

}
