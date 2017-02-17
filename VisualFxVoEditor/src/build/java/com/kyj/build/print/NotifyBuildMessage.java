/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.build.print
 *	작성일   : 2017. 2. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.build.print;

import java.util.Iterator;
import java.util.Map.Entry;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

/**
 * @author KYJ
 *
 */
public class NotifyBuildMessage {

	public static void main(String[] args) {

		if (args == null || args.length == 0) {
			System.out.println("Notify.");
			return;
		}

		String version = args[0];
		System.out.println(String.format("Gargoyle %s is success!", version));

		//Database Write

		Iterator<Entry<Object, Object>> iterator = ResourceLoader.getInstance().getEntry().iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			System.out.println(String.format("%s %s", next.getKey(), next.getValue()));
		}

		ResourceLoader.getInstance().put("write.exam", "test val");

		System.out.println(ResourceLoader.getInstance().get("write.exam"));

		//		ValueUtil.toString(new RuntimeException("zz"));
	}
}
