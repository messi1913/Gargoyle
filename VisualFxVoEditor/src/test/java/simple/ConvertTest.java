package simple;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 11. 26.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class ConvertTest {

	@Test
	public void test() throws UnsupportedEncodingException {
		String str = "%3B0%23%25uC5F0%25uB9C8%2520%25uBD88%25uB7C9%3B3%23%25uC2A4%25uD06C%25uB77C%25uCE58%3B3%23%25uD30C%25uC190%3B2";
		byte[] bytes = str.getBytes("UTF-8");
		System.out.println(new String(bytes,"EUC-KR"));
	}
}
