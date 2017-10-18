package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class PPTUtilTest {

	@Test
	public void test() throws FileNotFoundException, IOException {
//		{
//			File parent = new File("C:\\Users\\KYJ\\Documents\\LMS\\IT Change management");
//			PPTUtil.createXMLSlideShowFileSimpleImages(new File(parent, "IT Change management.ppt"), parent.listFiles());
//		}
		{
			File parent = new File("C:\\Users\\KYJ\\Documents\\LMS\\IT Change management");
			File pptFile = new File(parent, "IT Change management.ppt");
			PPTUtil.createHSLFSlideShowFileSimpleImages(pptFile, parent.listFiles());
			
			
			FileUtil.openFile(pptFile);
		}
	}

}
