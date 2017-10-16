package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class PPTUtilTest {

	@Test
	public void test() throws FileNotFoundException, IOException {
		File parent = new File("C:\\Users\\KYJ\\Documents\\LMS\\IT Change management");
		PPTUtil.createFileSimpleImages(new File(parent, "IT Change management.ppt"), parent.listFiles());
	}

}
