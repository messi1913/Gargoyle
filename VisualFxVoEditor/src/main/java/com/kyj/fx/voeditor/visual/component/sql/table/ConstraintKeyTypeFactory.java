/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE;

/**
 * DBMS마다 제약조건을 시스템의 유형에 맞게 정의
 *
 * @author KYJ
 *
 */
public class ConstraintKeyTypeFactory {

	private IKeyType impl;

	public IKeyType getKeyTypeImpl() {
		return impl;
	}

	public ConstraintKeyTypeFactory(IKeyType impl) {
		this.impl = impl;
	}

	public KEY_TYPE getType(String check) {
		return impl.getType(check);
	}

	public boolean isPrimaryKey(KEY_TYPE type) {
		return impl.isPrimaryKey(type);
	}

	public boolean isMultiKey(KEY_TYPE type) {
		return impl.isMultiKey(type);
	}

	public boolean isForeignKey(KEY_TYPE type) {
		return impl.isForeignKey(type);
	}
}
