/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.core.listener
 *	작성일   : 2016. 8. 12.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.bundle;

/**
 *
 *  LanguageInitializer.java에서 다국어에 대한 메타정보가 성공적으로 로드되는지 확인하기 위한 리스너 클래스.
 *
 * @author KYJ
 *
 */
public interface LanguageOnLoadedListener {

	/**
	 *
	 * 다국어 Property 파일이 성공적으로 로드되면 호출됨.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 12.
	 * @param event
	 */
	public void onLoaded(LoadedEvent event);

}
