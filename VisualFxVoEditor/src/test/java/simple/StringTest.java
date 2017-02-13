package simple;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * package :
 *	fileName : StringTest.java
 *	date      : 2015. 11. 2.
 *	user      : KYJ
 */

/**
 * @author KYJ
 *
 */
public class StringTest {

	private static final String[] KEYWORDS = new String[] { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto",
			"if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected",
			"public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
			"try", "void", "volatile", "while" };

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

	public StringTest() {

	}

	@Test
	public void charConcat(){
		char[] chars = new char[5];
		chars[0] = 'a';
		chars[1] = 'b';
		chars[2] = 'c';


		System.out.print(new String(chars, 0, 3));
	}
	public static void main(String[] args) {
		System.out.println(KEYWORD_PATTERN);
	}

	@Test
	public void splitTest() {
		StringBuffer sb = new StringBuffer();
		sb.append("askdmaskdmasdkasdmaskd sad\n");
		sb.append("asdasdasd\n");
		sb.append("sdssd\n");
		sb.append("dsds\n");
		sb.append("dsddsdsdsd\n");
		sb.append("asdsdsdss        sdsdsd\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("s\n");
		sb.append("dsdasdas\n");
		Stream.of(sb.toString().split("\\s+")).forEach(System.out::println);
		;

	}

	@Test
	public void findDoubleDot() throws IOException {

		String str = ValueUtil.toString(getClass().getResourceAsStream("sample.txt"));
		//		StringBuffer sb = new StringBuffer();
		//		sb.append("234423423423\"fsdfdsfsdfsd\"sedfsdfsd\n");
		//		String str = sb.toString();

		if (str.indexOf("\"") != -1) {
			List<Integer> doubleDots = new ArrayList<>();
			int idx = 0;

			while (idx != -1) {

				int nextIdx = str.indexOf("\"", idx);

				if (nextIdx != -1) {

					/*
					 * ignore index check. character.
					 *
					 * 실제 텍스트 \ 기호가 포함되는 경우는 변환처리대상에서 제외.
					 *
					 * 텍스트에 \" 없이 단순히 "(double dot) 만 포함되는경우는
					 *
					 * \를 포함시킴.
					 */
					if (str.charAt(nextIdx - 1) != '\\') {
						doubleDots.add(nextIdx);
					}
				}

				if (nextIdx == -1)
					break;

				idx = nextIdx + 1;

			}

			StringBuffer sb = new StringBuffer();
			int arrayIdx = doubleDots.size();
			int stringIdx = 0;

			for (int i = 0; i < arrayIdx; i++) {
				int splitIndex = doubleDots.get(i).intValue();
				sb.append(str.substring(stringIdx, splitIndex)).append("\"");
				stringIdx = splitIndex + 1;
			}
			sb.append(str.substring(stringIdx));
			doubleDots.forEach(System.out::println);

			System.out.println(sb.toString());
		}

	}



	@Test
	public void utf() throws Exception {
//		System.out.println(new String("<".getBytes("UTF-8")));


		StringBuffer sb = new StringBuffer();
		sb.append("hello world zzzz\n");
		sb.append("\n");
		sb.append("sssss \"나는 그랬었다.\" ㅋㅋㅋㅋㅋ \n");
		sb.append("\n");
		sb.append("나닛 ? \n");
		sb.toString();

		String updateStatement = "update tbd_sys_msg_glob set msg_cont  =  '" +sb.toString()+ "' where msg_id = 'MSG_C_900046' " ;
		DbUtil.update(updateStatement);

	}

	@Test
	public void isDmlTest() {

		Assert.assertEquals(DbUtil.isDml("SELECT * FROM TBB"), false);
		Assert.assertEquals(DbUtil.isDml("insert into TBB values ('a')"), true);
		Assert.assertEquals(DbUtil.isDml("create table tbm_sm_user "), true);
		Assert.assertEquals(true, DbUtil.isDml("CREATE TABLE TBM_SM_USER "));
		Assert.assertEquals(DbUtil.isDml("DELETE FROM TBMMM"), true);
		Assert.assertEquals(DbUtil.isDml("UPDATE TBMMM SET SS = 'A' WHERE SSZZ = 'A'"), true);

	}
}
