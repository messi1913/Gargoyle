/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2017. 4. 13.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

import java.util.Properties;

/**
 * @author KYJ
 *
 */
public abstract class AbstractScm {

	private Properties properties;

	public AbstractScm(Properties properties) {
		this.properties = properties;
		init(properties);
	}

	/**
	 * 초기화 처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 2.
	 * @param properties
	 */
	public abstract void init(Properties properties);

	/**
	 * 현재 객체에 설정된 Properties를 리턴한다.
	 * 원본 데이터가 변경되자않게하기위해
	 * 객체를 새로 생성후 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 9.
	 * @param file
	 * @return
	 */
	public Properties getProperties() {
		return this.properties;
	}
}
