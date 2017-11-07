/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.event
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

/**
 * 리소스 로드가 정상적으로 처리되면 발생.
 *
 * @author KYJ
 *
 */
public interface LoadedEvent {

	/**
	 * 리소스를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @return
	 */
	public Object getItem();

}
