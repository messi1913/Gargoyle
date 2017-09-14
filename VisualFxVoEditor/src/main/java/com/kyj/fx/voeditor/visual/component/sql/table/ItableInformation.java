/**
 * package : com.kyj.fx.voeditor.visual.component.table
 *	fileName : ItableInformation.java
 *	date      : 2016. 1. 1.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

/**
 * 테이블 정보 보기를 구현하는 UI는 구현해주어야할 부분을 정의.
 *
 * @author KYJ
 *
 */
public interface ItableInformation {

	/**
	 * 프레임을 구성하는 뷰를 구현해야함. 프레임을 구성하는 뷰안에 데이터베이스 접근정보 및 테이블 스키마 정보를 포함. 그 정보를
	 * 리턴받아서 처리하기 위해서임.
	 *
	 * @param tableInformationFrameView
	 */
	public void setParentFrame(TableInformationFrameView tableInformationFrameView);

	/**
	 * 초기화 처리
	 */
	public void init() throws Exception;

	/**
	 * UI 아이템 항목을 초기화. 청소.
	 *
	 * UI의 기능을 지우는 항목이 아님.
	 *
	 * 예를들면 그리드의 데이터를 지운다던지. 텍스트의 내용을 비우는 기능을한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @throws Exception
	 */
	public void clear() throws Exception;

	/**
	 * 사용된 jdbc Driver를 문자열로 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 5.
	 * @return
	 */
	public String getDbmsDriver();
}
