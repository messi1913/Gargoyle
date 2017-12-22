/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.annotation
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***************************
 *
 * Javafx initialize 처리후 수행될 후처리를 기술함. <br>
 *
 * <code>Platform.runLater() </code>함수로 호출되기때문에 지연호출된다는 점을 인식할것. <br>
 * @author KYJ
 *
 ***************************/
@Retention(RetentionPolicy.RUNTIME)
public @interface FxPostInitialize {

}
