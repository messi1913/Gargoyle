/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.dimmension
 *	작성일   : 2017. 4. 13.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.dimmension;

import java.util.Properties;

import com.kyj.scm.manager.core.commons.ISCMCommit;

/**
 *
 * Project 복사.
 *
 * Copy의 경우 Revision 정보까지 복사하고자하는 경우 사용.
 *
 * @author KYJ
 * @Deprecated 지원안함.
 */
@Deprecated
public class DimCopy extends AbstractDimmension implements ISCMCommit {

	/**
	 * @param javaSVNManager
	 * @param properties
	 */
	public DimCopy(DimmensionManager javaSVNManager, Properties properties) {
		super(javaSVNManager, properties);
	}

}
