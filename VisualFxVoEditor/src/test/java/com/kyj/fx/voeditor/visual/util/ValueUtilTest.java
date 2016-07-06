/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 11. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.HashMap;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class ValueUtilTest {

	@Test
	public void toCVSStringTest() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("zz1", "zz1");
		hashMap.put("zz2", "zz2");
		hashMap.put("zz3", "zz3");
		hashMap.put("zz4", "zz4");
		String cvsString = ValueUtil.toCVSString(hashMap);
		System.out.println(cvsString);
	}

	/**
	 * 파라미터로 주어진 길이보다 긴 subString을 처리하는경우 예외 발생을 테스트함.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void substringExceptionTest() {
		System.out.println("asdasd".substring(0, 30));
	}
}
