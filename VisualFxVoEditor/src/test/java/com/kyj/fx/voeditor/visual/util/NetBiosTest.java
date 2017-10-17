/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 9. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.net.MalformedURLException;
import java.util.Date;

import org.junit.Test;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

/**
 * @author KYJ
 *
 */
public class NetBiosTest {

	@Test
	public void test() {

	}

	// String url = "smb://{Id}:{Password}@{Address}/{Folder}/{File}"; // Ex:
	// smb://guest@112.171.103.241/Share/test.txt
	// final static String _FILE_SERVER_BASE_URL =
	// "smb://접속ID:접속비번@넷바이오스IP/접속할디렉토리/";
	final static String _FILE_SERVER_BASE_URL = "smb://mesman:mesman_dev1@192.168.20.64/Log2/";
//	final static String _FILE_SERVER_BASE_URL = "smb://sbiologics\\kyjun.kim:Samsung\\@@182.198.160.100/";


	public static void main(String[] args) throws Exception {
		try {
			SmbFile smbFile = new SmbFile(_FILE_SERVER_BASE_URL);

			SmbFile[] listFiles = listDirs(smbFile);
			for (SmbFile name : listFiles) {
				
//				System.out.println(name.getHeaderFields());
				
//				if(DateUtil.getDateAsStr(new Date(), DateUtil.SYSTEM_DATEFORMAT_YYYYMMDD).equals(name))
					System.out.println(name.getName());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	public static SmbFile[] listDirs(String url) throws Exception {
		return listDirs(new SmbFile(url));
	}

	/**
	 * 디렉토리 목록 조회
	 * 
	 * @param sbmFile
	 * @return
	 * @throws Exception
	 */
	public static SmbFile[] listDirs(SmbFile sbmFile) throws Exception {
		return sbmFile.listFiles(new SmbFileFilter() {
			public boolean accept(SmbFile arg0) throws SmbException {
				return arg0.isDirectory();
			}
		});
	}

	public static SmbFile[] listDirs(SmbFile parent, String fileName) throws Exception {
		return parent.listFiles(new SmbFileFilter() {
			public boolean accept(SmbFile arg0) throws SmbException {
				if (fileName == null)
					return arg0.isDirectory();
				return arg0.isDirectory() && arg0.getName().equals(fileName);
			}
		});
	}

	/**
	 * 디렉토리 목록 조회
	 * 
	 * @param sbmFile
	 * @return
	 * @throws Exception
	 */
	public static SmbFile[] listFile(SmbFile sbmFile) throws Exception {
		return sbmFile.listFiles(new SmbFileFilter() {
			public boolean accept(SmbFile arg0) throws SmbException {
				return arg0.isFile();
			}
		});
	}
}
