/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.annotation
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import com.kyj.fx.voeditor.visual.framework.InstanceTypes;;

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
@Retention(RetentionPolicy.RUNTIME)
public @interface FXMLController {

	/********************************
	 * 작성일 : 2016. 5. 21. 작성자 : KYJ
	 *
	 * define fxml file.
	 * 반드시 기술할 사항.
	 *
	 * @return
	 ********************************/
	String value();

	/********************************
	 * 작성일 : 2016. 5. 22. 작성자 : KYJ
	 *
	 * fxml형식을 root로 지정한경우 true 디폴트는 false
	 *
	 * @return
	 ********************************/
	boolean isSelfController() default false;

	/********************************
	 * 작성일 :  2016. 5. 22. 작성자 : KYJ
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
