/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service.core
 *	작성일   : 2016. 4. 14.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.events;

/**
 * 
 * 이벤트 처리와 관련된 변수 상수만 정의함.
 * 
 * 내용수정하기전에 RegistNewTrigger의 처리 코드를 먼저 확인하길바람.
 * 
 * don't fix content.
 * 
 * @author KYJ
 *
 */
public interface MakeBusinessEventConst {

	/**
	 * 이전페이지에 대한 메타정보를 정의하는 상수
	 * 
	 * @최초생성일 2016. 4. 18.
	 */
	public static final String COMMONS_TRIGGER_PREV_PAGE = "commons.trigger.prev.page";
	/**
	 * 현재페이지에 대한 메타정보를 정의하는 상수
	 * 
	 * @최초생성일 2016. 4. 18.
	 */
	public static final String COMMONS_TRIGGER_CURRENT_PAGE = "commons.trigger.current.page";
	/**
	 * 다음페이지에 대한 메타정보를 정의하는 상수.
	 * 
	 * @최초생성일 2016. 4. 18.
	 */
	public static final String COMMONS_TRIGGER_NEXT_PAGE = "commons.trigger.next.page";

	/**
	 * 사용자가 팝업을 호출할떄 입력했던 파람
	 * 
	 * @최초생성일 2016. 4. 26.
	 */
	public static final String COMMONS_USER_PARAM = "commons.user.param";

}
