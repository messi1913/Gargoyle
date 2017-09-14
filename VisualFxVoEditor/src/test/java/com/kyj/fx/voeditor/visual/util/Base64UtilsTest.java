package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import org.junit.Test;

public class Base64UtilsTest {

	@Test
	public void test() throws IOException {
		
		File file = new File("C:\\Users\\KYJ\\Pictures\\downloadfile-171.jpg" );
		
		
		System.out.println(file.exists());
		
		byte[] byteArray = FileUtil.toByteArray(file);
		
		
		String encodeToString = Base64.getEncoder().encodeToString(byteArray);
		
		System.out.println(encodeToString);
		
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		sb.append("\n");
		sb.append("<html>\n");
		sb.append("<body>\n");
		sb.append("<img src=\"data:img/jpg;base64,%s\"/>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		sb.toString();
		
		String format = String.format(sb.toString(), encodeToString);
		System.out.println(format);
		
	}

}
