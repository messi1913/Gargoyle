/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2017. 5. 24.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KYJ
 *
 */
@GagoyleInitializable
public class HostNameVertifierInitializer implements Initializable {

	private static Logger LOGGER = LoggerFactory.getLogger(HostNameVertifierInitializer.class);

	@Override
	public void initialize() throws Exception {
		LOGGER.debug(getClass().getName() + "  initialize.");
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				LOGGER.debug(arg0);
				return true;
			}
		});

	}

}
