/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service
 *	작성일   : 2016. 10. 10.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.events;

import com.kyj.fx.voeditor.visual.framework.loader.ui.MakeBusinessFrameComposite;

/**
 *
 * 트리거 등록이 성공적으로 끝나는 경우 호출된다.
 * @author KYJ
 *
 */
public interface MakeBusinessFrameOnRegisterSuccessEvent extends MakeBusinessEventConst {

	/**
	 * 트리거 등록이 성공적으로 처리가 되었을때 발생하는 이벤트
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 10.
	 * @return
	 */
	public void onSuccess(MakeBusinessFrameComposite object);

}
