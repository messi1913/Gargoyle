package simple;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.DbUtil;

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

	private static final String[] KEYWORDS = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto",
			"if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected",
			"public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
			"try", "void", "volatile", "while"};

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

	public StringTest() {

	}

	public static void main(String[] args) {
		System.out.println(KEYWORD_PATTERN);
	}


	@Test
	public void utf() throws UnsupportedEncodingException{
		System.out.println(new String("<".getBytes("UTF-8")));
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
