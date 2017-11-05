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
public interface MakeBusinessFrameOnNextEvent extends MakeBusinessEventConst {

	/**
	 * next페이지로 넘기기 전에 호출되는 이벤트
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 14.
	 * @param properties
	 * @return booean 다음 페이지로 넘길지 여부를 지정함.
	 */
	public boolean onBeforeNext(Map<String, Object> properties);
}
