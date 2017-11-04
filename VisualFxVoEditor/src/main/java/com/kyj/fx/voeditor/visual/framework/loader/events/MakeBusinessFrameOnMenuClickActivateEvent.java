/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service
 *	작성일   : 2016. 4. 11.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.events;

import java.util.Map;

/**
 * @author KYJ
 *
 */
public interface MakeBusinessFrameOnMenuClickActivateEvent extends MakeBusinessEventConst {

	/**
	 * 페이지가 활성화되면 호출된다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 14.
	 * @param properties
	 */
	public void onActive(Map<String, Object> beforeProperties, Map<String, Object> properties);
}
