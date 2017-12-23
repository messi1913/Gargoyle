/********************************
 *	프로젝트 : fxloader
 *	패키지   : com.kyj.fx.fxloader
 *	작성일   : 2017. 12. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.fxloader;

import javafx.scene.control.TableView;
import javafx.util.Builder;

/**
 * @author KYJ
 *
 */
public class GargoyleTableViewBuilder extends TableView<Object> implements Builder<TableView<?>> {

	@Override
	public TableView<?> build() {
		return this;
	}

}
