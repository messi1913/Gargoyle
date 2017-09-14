/**
 * package : com.kyj.fx.voeditor.visual.component.sql.functions
 *	fileName : SaveSQLConsumer.java
 *	date      : 2015. 11. 8.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.util.function.BiFunction;

/**
 * SQL문을 저장처리한다.
 *
 * @author KYJ
 *
 */
public abstract class SaveSQLFunction<T> implements BiFunction<T, String, Boolean> {

	/**
	 * 저장처리하는 로직을 기술한다.
	 */
	public abstract Boolean apply(T t, String u);

}
