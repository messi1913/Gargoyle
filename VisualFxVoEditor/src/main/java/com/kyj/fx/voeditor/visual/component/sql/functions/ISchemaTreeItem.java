/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.functions
 *	작성일   : 2015. 11. 6.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.util.function.BiFunction;

import javafx.scene.control.TreeItem;

/**
 * @author KYJ
 *
 */
public interface ISchemaTreeItem<T, K> extends BiFunction<T, ConnectionSupplier, TreeItem<K>> {

	/*
	 * 스키마 트리에 적용할 아이템을 반환한다. (non-Javadoc)
	 * 
	 * @see java.util.function.Function#apply(java.lang.Object)
	 */
	TreeItem<K> apply(T t, ConnectionSupplier conSupplier);
}
