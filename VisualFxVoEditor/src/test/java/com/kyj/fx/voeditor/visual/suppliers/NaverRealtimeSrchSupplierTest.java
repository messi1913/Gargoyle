/**
 * 
 */
package com.kyj.fx.voeditor.visual.suppliers;

import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

/**
 * @author KYJ
 *
 */
public class NaverRealtimeSrchSupplierTest {

	@Before
	public void setting() throws Exception {
		new ProxyInitializable().initialize();
	}

	@Test
	public void simple() {

		System.out.println(NaverRealtimeSrchSupplier.getInstance().get());
	}
}
