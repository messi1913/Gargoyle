/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.reg.service.biz
 *	작성일   : 2016. 4. 18.
 *	프로젝트 : PASS 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.loader.core;

import java.util.List;
import java.util.Map;

/**
 * 
 * 스케줄링을 시스템에 등록하기 위한 함수를 정의한다.
 * 
 * 
 * @author KYJ
 *
 */
public interface BusinessRegistable {

	/**
	 * 시스템 등록하기위한 함수내용을 기술함.
	 * 
	 * 시스템등록에 성공하면 true, 그렇지않으면 false를 리턴.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 18.
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	public boolean regist(List<Map<String, Object>> properties) throws Exception;

	public List<RegistItem> registPages();

}
