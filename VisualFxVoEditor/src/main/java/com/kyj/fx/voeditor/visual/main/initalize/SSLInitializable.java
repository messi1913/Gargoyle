/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2016. 2. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * SSL 통신을 위한 세팅
 *
 * @author KYJ
 *
 */
@GagoyleInitializable
public class SSLInitializable implements Initializable {

	private static Logger LOGGER = LoggerFactory.getLogger(SSLInitializable.class);

	@Override
	public void initialize() throws Exception {
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
			SSLContext.setDefault(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * SSL 통신 인증
	 *
	 * @author KYJ
	 *
	 */
	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// LOGGER.debug("######################");
			// LOGGER.debug("checkClientTrusted");
			LOGGER.debug(arg1);
			// LOGGER.debug("######################");
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			// LOGGER.debug("########################################################################################");
			// LOGGER.debug("checkServerTrusted");
			// LOGGER.debug(arg1);

			boolean present = Stream.of(arg0).filter(v -> {

				switch (v.getSigAlgName()) {
				case "SHA256withRSA":
					return true;
				case "SHA384withECDSA":
					return true;
				case "SHA384withRSA":
					return true;
				}

				return false;
			}).findFirst().isPresent();

			if (!present) {
				LOGGER.debug("Can't not found Truested Algorisms ");
				Stream.of(arg0).forEach(v -> LOGGER.warn(v.getSigAlgName()));
				throw new CertificateException();
			}

			// LOGGER.debug("########################################################################################");
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
