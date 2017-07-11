/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.momory
 *	작성일   : 2016. 12. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.momory;

import static org.junit.Assert.*;

import java.util.Enumeration;

import org.junit.Before;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class ResourceLoaderDbPropertiesTest {

	ResourceLoader prop;

	@Before
	public void settings() {
		prop = ResourceLoader.getInstance();
	}

	@Test
	public void printAll() {
		Enumeration<Object> keySet = prop.keySet();
		while (keySet.hasMoreElements()) {
			Object k = keySet.nextElement();
			String v = prop.get(k.toString());

			System.out.println(String.format("%s - %s", k, v));
		}
	}

}
