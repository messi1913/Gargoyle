/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 2. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.NetworkUtil;

/**
 * @author KYJ
 *
 */
@GagoyleInitializable
public class ProxyInitializable implements Initializable {

	private static Logger LOGGER = LoggerFactory.getLogger(ProxyInitializable.class);

	@Override
	public void initialize() throws Exception {

		if (NetworkUtil.isUseProxy()) {
			LOGGER.debug(" Use Proxy Settings");
			String httpHost = getHttpHost();
			String httpPort = getHttpProxyPort();
			String httpsHost = getHttpsProxyHost();
			String httpsPort = getHttpsProxyPort();

			LOGGER.debug("http host :{} port :{} , https host : {}, port : {}", httpHost, httpPort, httpsHost, httpsPort);

			System.setProperty(ResourceLoader.HTTP_PROXY_HOST, httpHost);
			System.setProperty(ResourceLoader.HTTP_PROXY_PORT, httpPort);
			System.setProperty(ResourceLoader.HTTPS_PROXY_HOST, httpsHost);
			System.setProperty(ResourceLoader.HTTPS_PROXY_PORT, httpsPort);
		} else {
			LOGGER.debug("Not Use Proxy Settings");
		}

	}

	/**
	 * htpps 프록시 포트 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 */
	public static final String getHttpsProxyPort() {
		return NetworkUtil.getHttpsProxyPort();
	}

	/**
	 * https 프록시 호스트 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 */
	public static final String getHttpsProxyHost() {
		return NetworkUtil.getHttpsProxyHost();
	}

	/**
	 * Proxy port 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 */
	public static final String getHttpProxyPort() {
		return NetworkUtil.getHttpProxyPort();
	}

	/**
	 * Proxy Host 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 */
	public static final String getHttpHost() {
		return NetworkUtil.getHttpHost();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 17.
	 * @return
	 */
	public static final HttpHost getProxyHost() {
		return NetworkUtil.getProxyHost();
	}

}
