/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.annotation
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/***************************
 *
 * Javafx initialize 처리후 수행될 후처리를 기술함.
 *
 * @author KYJ
 *
 ***************************/
@Retention(RetentionPolicy.RUNTIME)
public @interface FxPostInitialize {

}
