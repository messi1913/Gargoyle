/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 2. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

/**
 * @author KYJ
 *
 */
@GagoyleInitializable
public class ProxyInitializable implements Initializable {

	private static Logger LOGGER = LoggerFactory.getLogger(ProxyInitializable.class);

	@Override
	public void initialize() throws Exception {
		LOGGER.debug("Proxy Settings..... ");
		String useProxyYn = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.USE_PROXY_YN);

		LOGGER.debug("Proxy Settings : [" + useProxyYn + "]");
		if ("Y".equals(useProxyYn)) {
			String httpHost = ResourceLoader.getInstance().get(ResourceLoader.HTTP_PROXY_HOST);
			String httpPort = ResourceLoader.getInstance().get(ResourceLoader.HTTP_PROXY_PORT);
			String httpsHost = ResourceLoader.getInstance().get(ResourceLoader.HTTPS_PROXY_HOST);
			String httpsPort = ResourceLoader.getInstance().get(ResourceLoader.HTTPS_PROXY_PORT);

			LOGGER.debug("http host :{} port :{} , https host : {}, port : {}", httpHost, httpPort, httpsHost, httpsPort);

			System.setProperty(ResourceLoader.HTTP_PROXY_HOST, httpHost);
			System.setProperty(ResourceLoader.HTTP_PROXY_PORT, httpPort);
			System.setProperty(ResourceLoader.HTTPS_PROXY_HOST, httpsHost);
			System.setProperty(ResourceLoader.HTTPS_PROXY_PORT, httpsPort);
		}

	}

}
