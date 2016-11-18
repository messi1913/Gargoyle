/**
 *
 */
package com.kyj.fx.voeditor.visual.suppliers;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.RealtimeSearchVO;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

/**
 * @author KYJ
 *
 */
public class NaverRealtimeSrchSupplierTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(NaverRealtimeSrchSupplierTest.class);

	@Before
	public void setting() throws Exception {
		new ProxyInitializable().initialize();
	}

	@Test
	public void simple() throws MalformedURLException, Exception {

		List<RealtimeSearchVO> meta = NaverRealtimeSrchSupplier.getInstance().getMeta();
		meta.forEach(v -> {
			LOGGER.debug("Title : {} ", v.getTitle());

			v.getItems().forEach(i -> {
				LOGGER.debug("keyword : {} ,  rank : {} link : {} ", i.getKeyword(), i.getRank() , i.getLink());
			});
		});

	}
}
