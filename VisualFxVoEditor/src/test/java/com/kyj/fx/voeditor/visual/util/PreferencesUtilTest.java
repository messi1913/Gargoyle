/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 12. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.prefs.Preferences;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.word.AsynchWordExecutor;

/**
 * @author KYJ
 *
 */
public class PreferencesUtilTest {

	@Test
	public void getMsOfficeTest() {
		Preferences msOffice = PreferencesUtil.getMsOffice();
		Assert.assertNotNull(msOffice);
	}

	@Test
	public void get() {
		try {
			Map<String, String> readStringValues = WinRegistry.readStringValues(WinRegistry.HKEY_CURRENT_USER, "PROGRAMDIR");
			System.out.println(readStringValues);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 레지스트리에서 MSWORD의 경로를 얻어온다.
	 *
	 * @return
	 * @throws Exception
	 */
	@Test
	public void getMsWordDirByRegedit() throws Exception {

		List<String> command = new ArrayList<String>();

		command.add("REG");
		command.add("QUERY");
		command.add("HKCU\\Software\\Microsoft\\Office\\14.0\\Word\\Options");
		command.add("/v");
		command.add("PROGRAMDIR");

		BiConsumer<Integer, StringBuffer> convert = (code, buf) -> {
			if (code == 0) {
				System.out.println("exit Code : " + code);
				String str = buf.toString();
				String matchingStr = "REG_SZ";
				int indexOf = str.indexOf(matchingStr);
				if (indexOf != -1) {
					str = str.substring(indexOf + 6).trim() + File.separator + "WINWORD.exe";
					System.out.println("레지스트리에서 검색된 MS WORD 경로 : " + str);
				}

				System.out.println(buf.toString());
			}

		};
		RuntimeClassUtil.exeAsynchLazy(command, convert);

		Thread.sleep(5000);
	}
}
