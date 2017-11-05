/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service.biz
 *	작성일   : 2016. 4. 18.
 *	프로젝트 : PASS 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.loader.events.MakeBusinessEventConst;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.IdGenUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 정의한 스케줄러를 데이터베이스에 저장하기 위한 비즈니스 처리를 정의한다.
 *
 * @author KYJ
 *
 */
public class RegistNewTriggerBiz implements BusinessRegistable {

	private static Logger LOGGER = LoggerFactory.getLogger(RegistNewTriggerBiz.class);

	/* #################### 초기 상수(프로파일 상수) ################# */
	public static final String KEY_PACKAGE_NAME = "packageName";
	public static final String KEY_CLASS_NAME = "className";
	public static final String KEY_ALIAS = "alias";
	public static final String KEY_DESCRIPTION = "description";

	/* #################### 프로파일 상수 ################# */
	public static final String KEY_TRIGGER_SYSTEM_CODE = "triggerSystemCode";
	public static final String KEY_TRIGGER_NAME = "triggerName";
	public static final String KEY_TRIGGER_DESC = "triggerDesc";

	/* #################### 스케줄 상수 ################# */
	public static final String KEY_TRIGGER_SCHEDULE = "tigger.schedule";

	public static final String TRIGGER_SCHEDULE_ONCE = "onece";
	public static final String TRIGGER_SCHEDULE_EVERY_MINUTE = "minute";
	public static final String TRIGGER_SCHEDULE_EVERY_HOUR = "hour";
	public static final String TRIGGER_SCHEDULE_EVERY_DAY = "day";
	public static final String TRIGGER_SCHEDULE_EVERY_WEEK = "week";
	public static final String TRIGGER_SCHEDULE_CRON = "cron";

	/* #################### 디테일 상수 ################# */
	public static final String TRIGGER_SCHEDULE_DETAIL_VALUE = "schedule.detail.value";

	/* #################### 데이터베이스 처리상수 ################# */
	public static final String DATABASE_ID = "id";
	public static final String DATABASE_PROJECT_CODE = "projectCode";
	public static final String DATABASE_ALIAS = "alias";
	public static final String DATABASE_DESCRIPTION = "description";

	/*
	 * 데이터베이스에 등록처리.
	 *
	 * @inheritDoc
	 */
	public boolean regist(List<Map<String, Object>> properties) {
		Map<String, Object> hashMap = new HashMap<String, Object>();

		/* 개발자가 등록한 화면에서 정의한 내용들을 하나의 Map으로 임시저장. */
		for (Map<String, Object> m : properties) {
			hashMap.putAll(m);
		}

		/************ [시작]이벤트 관련 상수값은 제거함. **********/
		Field[] declaredFields = MakeBusinessEventConst.class.getDeclaredFields();

		for (Field c : declaredFields) {
			int modifiers = c.getModifiers();

			// public static 함수만 이벤트 공통함수라 정의.
			if ((Modifier.STATIC & modifiers) == Modifier.STATIC && ((Modifier.PUBLIC & modifiers) == Modifier.PUBLIC)) {
				try {
					//
					Object object = c.get(MakeBusinessEventConst.class);

					if (ValueUtil.isNotEmpty(object))
						hashMap.remove(object.toString());
				} catch (Exception e) {
					/*
					 * 아래는 발생가능성이 없는 예외임 만약 발생한경우는 이벤트 상수의 위치를 수정한 케이스임.
					 */
					LOGGER.error("unexpected occured error.  \n {}", ValueUtil.toString(e));
				}
			}
		}
		/************ [끝]이벤트 관련 상수값은 제거함. **********/

		/************ [시작]추가적인 개발자 정의 상수를 정의 **********/

		/************ [끝]추가적인 개발자 정의 상수를 정의. **********/

		/* 확인 */
		LOGGER.debug(hashMap.toString());

		// 데이터베이스 등록
		int transactionedScope = 0;
		try {
			transactionedScope = DbUtil.getTransactionedScope(hashMap, (map, template) -> {
				boolean isUpdateMode = false;

				String id = IdGenUtil.generate();
				String checkId = (String) hashMap.get(DATABASE_ID);
				if (null != checkId && !checkId.isEmpty()) {
					id = checkId;
					isUpdateMode = true;
				}

				Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();

				List<HashMap<String, Object>> insertList = new ArrayList<HashMap<String, Object>>();
				while (iterator.hasNext()) {
					Entry<String, Object> next = iterator.next();

					HashMap<String, Object> paramMap = new HashMap<String, Object>(3);
					paramMap.put(DATABASE_ID, id);
					paramMap.put("key", next.getKey());
					paramMap.put("value", next.getValue());
					insertList.add(paramMap);
				}
				@SuppressWarnings("unchecked")
				Map<String, Object>[] array = insertList.stream().toArray(HashMap[]::new);

				// ******** batch_schdule 테이블 저장 처리 **********
				HashMap<String, Object> scMap = new HashMap<String, Object>(hashMap);
				scMap.put(DATABASE_ID, id);
				scMap.put(DATABASE_PROJECT_CODE, hashMap.get(KEY_TRIGGER_SYSTEM_CODE));
				scMap.put(DATABASE_ALIAS, hashMap.get(KEY_TRIGGER_NAME));
				scMap.put(DATABASE_DESCRIPTION, hashMap.get(KEY_TRIGGER_DESC));

				if (isUpdateMode) {
					StringBuffer sb = new StringBuffer();
					sb.append("update batch_schedule \n");
					sb.append("set project_code = :projectCode, \n");
					sb.append("package_name = :packageName, \n");
					sb.append("class_name = :className, \n");
					sb.append("description = :description \n");
					sb.append("where id = :id \n");
					template.update(sb.toString(), scMap);

					// ******** batch_schedule_trigger 테이블 저장 처리 ********
					template.update("delete from batch_schedule_trigger where id = :id", scMap);
					template.batchUpdate("insert into batch_schedule_trigger(id, key,value) values (:id, :key, :value)", array);

				} else {
					StringBuffer sb = new StringBuffer();
					sb.append("insert into batch_schedule \n");
					sb.append("(id, project_code, package_name, class_name, alias, description)\n");
					sb.append("values\n");
					sb.append("(:id,:projectCode,:packageName,:className,:alias,:description)\n");
					template.update(sb.toString(), scMap);

					// ******** batch_schedule_trigger 테이블 저장 처리 ********
					template.batchUpdate("insert into batch_schedule_trigger(id, key,value) values (:id, :key, :value)", array);
				}

				return 1;
			}, ex -> LOGGER.error(ValueUtil.toString(ex)));
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			transactionedScope = -1;
		}

		if (transactionedScope == -1)
			return false;
		return true;
	}

	@Override
	public List<RegistItem> registPages() {
		
		
		return null;
	}
}
