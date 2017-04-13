/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.IDiffCommand;

/***************************
 *
 * Diff 구현
 *
 * 
 * 구현 지원안함.
 * @author KYJ
 *
 ***************************/
@Deprecated
public class DimDiff extends AbstractDimmension implements IDiffCommand<String, String, String> {

	public DimDiff(AbstractScmManager dimManager, Properties properties) {
		super(dimManager, properties);
	}

	@Override
	public String diff(String t, String f) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
