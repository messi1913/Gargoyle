package com.kyj.fx.voeditor.visual.util;

import java.net.SocketException;

import org.junit.Test;

public class NetworkUtilTest {

	@Test
	public void testGetRealAddress() throws SocketException {
		System.out.println(NetworkUtil.getRealAddress());
	}

	@Test
	public void testGetAllRealAddress() throws SocketException {
		System.out.println(NetworkUtil.getAllRealAddress());
	}

}
