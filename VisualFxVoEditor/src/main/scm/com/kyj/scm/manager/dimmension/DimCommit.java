/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import com.kyj.scm.manager.core.commons.AbstractScmManager;
import com.kyj.scm.manager.core.commons.ISCMCommit;

/**
 *  Commit Operation.
 *
 * @author KYJ
 * @Deprecated 구현 지원안함.
 */
@Deprecated
class DimCommit extends AbstractDimmension implements ISCMCommit {

	public DimCommit(AbstractScmManager dimManager, Properties properties) {
		super(dimManager, properties);
	}
}
