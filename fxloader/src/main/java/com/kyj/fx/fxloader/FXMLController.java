/********************************
 *	프로젝트 : fxloader
 *	패키지   : com.kyj.fx.fxloader
 *	작성일   : 2017. 11. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
/***************************
 * 컨트롤러 클래스에 아래 어노테이션을 명시하고 같은 패키지레벨에 FXML파일의 이름을 지정해주면 로드함.
 *
 * 아래 어노테이션으로 지정된 파일을 로드하기 위해선 FxUtil.load함수를 참고할것.
 *
 *
 *
 * @author KYJ
 *
 ***************************/
public @interface FXMLController {

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * define fxml file. 반드시 기술할 사항.
	 *
	 * @return
	 ********************************/
	String value();

	/**
	 * css 파일이 존재한다면 위치기록
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 16.
	 * @return
	 */
	String css() default "";

	/********************************
	 * 작성일 : 2016. 5. 22. 작성자 : KYJ
	 *
	 * fxml형식을 root로 지정한경우 true 디폴트는 false
	 *
	 * @return
	 ********************************/
	boolean isSelfController() default false;

	/********************************
	 * 작성일 : 2016. 5. 22. 작성자 : KYJ
	 *
	 * 생성유형을 정의함. 기본값은 새로생성. </br>
	 *
	 * 싱글톤유형을 지정한경우는 사용시 주의가 필요. </br>
	 *
	 * 자세한 내용은 InstanceTypes 주석을 참조할것 </br>
	 *
	 * @return
	 ********************************/
	InstanceTypes instanceType() default InstanceTypes.RequireNew;

}
