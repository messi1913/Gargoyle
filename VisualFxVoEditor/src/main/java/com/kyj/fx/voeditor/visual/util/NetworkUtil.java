/********************************
 *	프로젝트 : sos-server
 *	패키지    : com.samsung.sds.sos.server.util
 *	작성일    : 2016. 6. 10.
 *	프로젝트 : O-PERA 프로젝트
 *	작성자    : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.function.Function;

/**
 * @author KYJ
 *
 */
public class NetworkUtil {

	/**
	 * host address정보로 변환해주는 펑션인터페이스
	 *
	 * @최초생성일 2016. 6. 10.
	 */
	private static Function<InetAddress, String> hostAddressConverter = new Function<InetAddress, String>() {

		@Override
		public String apply(InetAddress t) {
			return t.getHostAddress();
		}
	};

	/**
	 * InetAddress클래스로부터 조건에 일치하는 정보를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 10.
	 * @param convertValue
	 * @return
	 * @throws SocketException
	 */
	private static String getAddressInfo(Function<InetAddress, String> convertValue) throws SocketException {

		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface nextElement = networkInterfaces.nextElement();

			if (nextElement.isVirtual())
				continue;

			if (nextElement.isLoopback())
				continue;

			if (!nextElement.isUp())
				continue;

			Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress netAddress = inetAddresses.nextElement();

				if (!netAddress.isLoopbackAddress() && !netAddress.isLinkLocalAddress() && netAddress.isSiteLocalAddress()) {
					String hostAddress = netAddress.getHostAddress();
					if (hostAddress.startsWith("192.168"))
						continue;
					return convertValue.apply(netAddress);
				}
			}
		}

		return "";

	}

	/**
	 * 장비 IP 주소리턴.
	 *
	 * 127.0 으로 시작하거나 192.168로 시작하는 IP주소 대상아님.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 10.
	 * @return
	 * @throws SocketException
	 */
	public static String getRealAddress() throws SocketException {
		return getAddressInfo(hostAddressConverter);
	}
}
