/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.file
 *	작성일   : 2016. 1. 27.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.util.List;

/**
 * @author KYJ
 *
 */
public interface IReadModel<T, K> {

	public List<K> readLines(T t) throws Exception;
}
