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
public interface MakeBusinessFrameOnCancelEvent extends MakeBusinessEventConst {


	/**
	 * Cancel 버튼을 누르면 호출된다.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 18.
	 * @param properties
	 * @return boolean 취소버튼클릭시 창을 자동으로 닫을지 유뮤.
	 */
	public boolean onCancel(Map<String, Object> properties);
}
