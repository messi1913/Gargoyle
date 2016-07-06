/**
 * KYJ
 * 2015. 10. 11.
 */
package com.kyj.fx.voeditor.core;

/**
 * 기본모델
 * 
 * @author KYJ
 *
 */
public interface IExtractModel<V, T> {

	public void extract(V vo, T t) throws Exception;
}
