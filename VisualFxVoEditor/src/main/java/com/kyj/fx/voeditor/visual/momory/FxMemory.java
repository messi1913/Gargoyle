/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2016. 5. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javafx.scene.Node;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class FxMemory {

	private static Map<String, Node> memory = new ConcurrentHashMap<>();

	public static Node put(String key, Node value) {
		return memory.put(key, value);
	}

	public static Node get(String key) {
		return memory.get(key);
	}
}
