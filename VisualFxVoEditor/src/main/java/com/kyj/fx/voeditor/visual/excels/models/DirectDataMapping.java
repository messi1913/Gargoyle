package com.kyj.fx.voeditor.visual.excels.models;

public interface DirectDataMapping<T> {

	public void doMapping(T instance, int row, int col, Object data) throws Exception;

}
