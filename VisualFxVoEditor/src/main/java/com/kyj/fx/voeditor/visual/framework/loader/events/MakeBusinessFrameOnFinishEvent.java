/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service
 *	작성일   : 2016. 4. 11.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.events;

import java.util.List;
import java.util.Map;

/**
 * 각 트리거 화면마다 트리거 처리가 끝나는경우 호출된다.
 *
 * @author KYJ
 *
 */
public interface MakeBusinessFrameOnFinishEvent extends MakeBusinessEventConst {

	/**
	 * finish버튼이 클릭되면 호출된다.
	 *
	 * @작성자 : KYJ	
	 * @작성일 : 2016. 4. 25.
	 * @param properties
	 * @return 성공적으로 끝나면 true를 리턴
	 */
	public boolean onFinish(List<Map<String, Object>> properties);
}
