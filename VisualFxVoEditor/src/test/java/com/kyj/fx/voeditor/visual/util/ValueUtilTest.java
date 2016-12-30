/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2015. 11. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;

import org.apache.velocity.VelocityContext;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.word.AsynchWordExecutor;
import com.kyj.fx.voeditor.visual.framework.word.HtmlTextToMimeAdapter;

/**
 * @author KYJ
 *
 */
public class ValueUtilTest {

	@Test
	public void toCVSStringTest() {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("zz1", "zz1");
		hashMap.put("zz2", "zz2");
		hashMap.put("zz3", "zz3");
		hashMap.put("zz4", "zz4");
		String cvsString = ValueUtil.toCVSString(hashMap);
		System.out.println(cvsString);
	}

	/**
	 * 파라미터로 주어진 길이보다 긴 subString을 처리하는경우 예외 발생을 테스트함.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void substringExceptionTest() {
		System.out.println("asdasd".substring(0, 30));
	}

	@Test
	public void velocityTest() {

		/********************************************************/
		/*
		 * foreach문을 활용한 sql문 반복[1]
		 *
		 * 변수 활용
		 */
		/********************************************************/
		{
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("\n");
			sb.append("#set($aaa=[1,2,3,4,5])\n");
			sb.append("\n");
			sb.append("select 1 from a\n");
			sb.append("where 1=1\n");
			sb.append("#foreach($a in $aaa)\n");
			sb.append("and a = $a\n");
			sb.append("#end\n");
			sb.toString();

			String velocityToText = ValueUtil.getVelocityToText(sb.toString(), new HashMap<>());
			System.out.println(velocityToText);
		}
		/********************************************************/
		/*
		 * foreach문을 활용한 sql문 반복[2]
		 * 상수 활용
		 */
		/********************************************************/
		{
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("select 1 from a\n");
			sb.append("where 1=1\n");
			sb.append("#foreach($a in [1..5])\n");
			sb.append("and a = $a\n");
			sb.append("#end\n");
			sb.toString();
			String velocityToText = ValueUtil.getVelocityToText(sb.toString(), new HashMap<>());
			System.out.println(velocityToText);
		}
		/********************************************************/
		/*
		 * 매크로 적용 예제.
		 *
		 */
		/********************************************************/
		{
			StringBuffer sb = new StringBuffer();
			//선언
			sb.append("#macro(test) \n");
			sb.append("	#foreach($a in [1..5]) ");
			sb.append("$a");
			sb.append("	#end \n");
			sb.append("#end \n");
			sb.append("\n");
			sb.append("\n");
			//사용
			sb.append("#test()  <br> ");
			sb.toString();
			String velocityToText = ValueUtil.getVelocityToText(sb.toString(), new HashMap<>());
			System.out.println(velocityToText);
		}
		/********************************************************/
		/*
		 * 타입 클래스를 보여줌.
		 *
		 */
		/********************************************************/
		{
			StringBuffer sb = new StringBuffer();
			sb.append("#set($string='this is a string')\n");
			sb.append("#set($bool = true)\n");
			sb.append("#set($numbe = 2)\n");
			sb.append("#set($array=[1,2,3,4,5])\n");
			sb.append("\n");
			sb.append("\n");
			sb.append("$string.class<br>\n");
			sb.append("$bool.class<br>\n");
			sb.append("$number.class<br>\n");
			sb.append("$array.class<br>\n");
			sb.toString();
			String velocityToText = ValueUtil.getVelocityToText(sb.toString(), new HashMap<>());
			System.out.println(velocityToText);
		}

		{

			System.out.println();
			/********************************************************/
			/*
			 * Velocity Context 확장 example.
			 *
			 * 예제에서는 $date변수 사용시 현재 날짜를 리턴하게 만듬.
			 */
			/********************************************************/
			StringBuffer sb = new StringBuffer();
			sb.append("$date\n");
			sb.toString();
			HashMap<String, Object> hashMap = new HashMap<>();
			//			hashMap.put("date", "someDate");
			String velocityToText = ValueUtil.getVelocityToText(sb.toString(), hashMap, false, new VelocityContext() {

				/* (non-Javadoc)
				 * @see org.apache.velocity.VelocityContext#internalGet(java.lang.String)
				 */
				@Override
				public Object internalGet(String key) {
					if ("date".equals(key))
						return new Date();
					return super.internalGet(key);
				}

				/* (non-Javadoc)
				 * @see org.apache.velocity.VelocityContext#internalContainsKey(java.lang.Object)
				 */
				@Override
				public boolean internalContainsKey(Object key) {
					return super.internalContainsKey(key);
				}

				/* (non-Javadoc)
				 * @see org.apache.velocity.VelocityContext#internalGetKeys()
				 */
				@Override
				public Object[] internalGetKeys() {
					return super.internalGetKeys();
				}

			});
			System.out.println(velocityToText);
		}

	}

	@Test
	public void toMime() throws UnsupportedEncodingException {

		Charset forName = Charset.forName("UTF-8");
		String content = ("<html> <meta charset = 'utf-8'/><body><p>hi 하이</p> </body> </html>");

		//		String mime = MimeHelper.toMime(content);

		try {
//			File file = new File("Sample.html");
//			FileUtil.writeFile(file, content, forName);

			AsynchWordExecutor executor = new AsynchWordExecutor(new HtmlTextToMimeAdapter(content));
			executor.execute();

			//			if (file.exists()) {
			//				List<String> asLis = Arrays.asList("C:\\Program Files\\Microsoft Office\\Office15\\WINWORD.exe", file.getAbsolutePath());
			//
			//				RuntimeClassUtil.simpleExec(asLis);
			//			}

			Thread.sleep(200000);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
