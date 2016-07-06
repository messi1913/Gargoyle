/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.loder;

import java.util.List;

import com.kyj.fx.voeditor.visual.component.scm.SVNItem;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

/**
 * SCM
 *
 * @author KYJ
 *
 */
public interface SCMInitLoader {

	public static final String REPOSITORIES = ResourceLoader.SVN_REPOSITORIES;

	<T extends SVNItem> List<T> load();
}
