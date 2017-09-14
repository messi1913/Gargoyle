/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : SaveSQLConsumer.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.util.function.BiFunction;

import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;

/**
 * SQL문을 로드처리한다.
 *
 * @author KYJ
 *
 */
public abstract class LoadSQLFunction<T> implements BiFunction<T, LoadFileOptionHandler, String> {

	/**
	 * 파일로부터 SQL문을 로드하는 로직을 기술한다.
	 */
	public abstract String apply(T t, LoadFileOptionHandler option);

}
