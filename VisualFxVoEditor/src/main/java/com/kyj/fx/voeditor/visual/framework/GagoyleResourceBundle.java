/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 6. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.PropertyResourceBundle;

/***************************
 *
 * 키에 해당하는 값이 없는경우 key를 리턴함.
 *
 * @author KYJ
 *
 ***************************/
public class GagoyleResourceBundle extends PropertyResourceBundle {

	/**
	 * @param stream
	 * @throws IOException
	 */
	public GagoyleResourceBundle(InputStream stream) throws IOException {
		super(stream);
	}

	/**
	 * @param reader
	 * @throws IOException
	 */
	public GagoyleResourceBundle(Reader reader) throws IOException {
		super(reader);
	}

	/***********************************************************************************/
	/* 이벤트 구현 */

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	@Override
	public Object handleGetObject(String key) {

		// 값이 없는경우 키를 리턴.
		if (!super.keySet().contains(key))
			return key;

		return super.handleGetObject(key);
	}

	@Override
	public boolean containsKey(String key) {
		// 값은 무조건 true로 지정.
		return true;
	}

	/***********************************************************************************/
}
