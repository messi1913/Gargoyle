/********************************
 *	프로젝트 : pass-batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service.core
 *	작성일   : 2016. 4. 14.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.events;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.loader.core.RegistNewTriggerBiz;

/**
 * nextpage 기반화면에서
 *
 * 일반적으로 사용되는 생명주기 함수들이 포함됨.
 *
 * @author KYJ
 *
 */
public interface MakeBusinessCommonsEvent extends MakeBusinessFrameOnCancelEvent, MakeBusinessFrameOnFinishEvent,
		MakeBusinessFrameOnMenuClickActivateEvent, MakeBusinessFrameOnNextEvent, MakeBusinessFrameOnPrevEvent {

	static Logger LOGGER = LoggerFactory.getLogger(MakeBusinessCommonsEvent.class);

	/*
	 *
	 * MakeTriggerFrameOnFinishEvent 이벤트
	 *
	 * @inheritDoc
	 */
	@Override
	public default boolean onFinish(List<Map<String, Object>> properties) {
		return false;
	}

	/**
	 * 이전화면에서 선택한 스케줄이름과 이동되야하는 탭의 포커싱을 맞추기 위한 탭 인덱스를 리턴함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 18.
	 * @param scheduleName
	 */
	default int getIndexByScheduleName(String scheduleName) {
		String _scheduleName = scheduleName;
		if (scheduleName == null) {
			_scheduleName = RegistNewTriggerBiz.TRIGGER_SCHEDULE_ONCE;
		}
		int selectIndex = -1;
		switch (_scheduleName) {

		/* 한번만 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_ONCE:
			selectIndex = 0;
			break;

		/* 매분마다 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_EVERY_MINUTE:
			selectIndex = 1;
			break;

		/* 매시간마다 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_EVERY_HOUR:
			selectIndex = 2;
			break;

		/* 매일 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_EVERY_DAY:
			selectIndex = 3;
			break;

		/* 매주 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_EVERY_WEEK:
			selectIndex = 4;
			break;

		/* 크론(제공되지않는 스케줄링 일자.)형태로 등록하여 실행하는경우 */
		case RegistNewTriggerBiz.TRIGGER_SCHEDULE_CRON:
			selectIndex = 5;
			break;
		}

		if (selectIndex == -1) {
			/*
			 * 유효하지않는 인덱스를 선택한경우. 0번쨰 인덱스로 고친다.
			 *
			 * 만약 추가되거나 삭제되는 tab이 존재하는 경우에 발생가능한 지점.
			 */
			LOGGER.warn("use selected invalide index :: {}... add or fix indexed tab.", selectIndex);
			selectIndex = 0;
		}
		return selectIndex;
	}
}
