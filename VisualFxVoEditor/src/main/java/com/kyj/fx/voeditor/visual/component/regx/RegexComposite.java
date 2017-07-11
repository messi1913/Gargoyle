/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.regx
 *	작성일   : 2017. 4. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.regx;

import java.io.IOException;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class RegexComposite {

	public RegexComposite() throws IOException {

		String str = ValueUtil
				.toString(RegexComposite.class.getResourceAsStream("[HorribleSubs] Dragon Ball Super - 84 [480p]_track3.ass"));

		String replaceAll = str.replaceAll(
				"Dialogue: [0-9]{1},[0-9]{1}:[0-9]{2}:[0-9]{2}.[0-9]{2},[0-9]{1}:[0-9]{2}:[0-9]{2}.[0-9]{2},[\\w]{1,},,0000,0000,0000,,",
				"");

		System.out.println(replaceAll);

	}

	public static void main(String[] args) throws Exception {
		new RegexComposite();
	}
}
