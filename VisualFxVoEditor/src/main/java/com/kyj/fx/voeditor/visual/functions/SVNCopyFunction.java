/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.functions
 *	작성일   : 2017. 12. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.functions;

import java.util.function.Function;

import com.kyj.fx.voeditor.visual.component.scm.SVNItem;

/**
 * @author KYJ
 *
 */
public abstract class SVNCopyFunction<T extends SVNItem, R> implements Function<T, R> {

	/*
	 * File Copy 기능 구현 <br/>
	 *
	 */
	public abstract R apply(T t);

}
