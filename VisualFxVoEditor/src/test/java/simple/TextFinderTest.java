package simple;
import org.junit.Assert;
import org.junit.Test;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 12. 7.
 *	프로젝트 : Gagoyle
 *	작성자   : KYJ
 *******************************/

/**
 * 문자 검색 기능을 만들기 위한 기본 테스트 코드 작성
 * 
 * @author KYJ
 *
 */
public class TextFinderTest {

	String baseText = "ABCDEFGHIJSSDMZZZA23FG";

	/**
	 * 가장 처음 일치되는 1건을 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 7.
	 */
	@Test
	public void findOne() {
		String expect = "FG";
		int indexOf = baseText.indexOf(expect, 0);

		CharSequence result = baseText.subSequence(indexOf, indexOf + expect.length());

		System.out.println("expect : " + expect);
		System.out.println("result : " + result);
		Assert.assertEquals(expect, result);
	}

	/**
	 * 일치하지않는 조건인 경우
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 7.
	 */
	@Test
	public void findOneNotMatch() {
		String expect = "F1G";
		int indexOf = baseText.indexOf(expect, 0);
		if (indexOf > -1) {
			CharSequence result = baseText.subSequence(indexOf, indexOf + expect.length());
			System.out.println("expect : " + expect);
			System.out.println("result : " + result);
			Assert.assertNotEquals(expect, result);
		} else {
			System.out.println("unMatch");
		}

	}

	/**
	 * 일치되는 수만큼 반환 find all
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 7.
	 */
	@Test
	public void findAll() {
		String expect = "FG";
		int startIdx = 0;
		int indexOf = baseText.indexOf(expect, startIdx);

		while (indexOf > -1) {
			int endIndex = indexOf + expect.length();
			CharSequence result = baseText.subSequence(indexOf, endIndex);
			System.out.println("expect : " + expect);
			System.out.println("result : " + result);
			Assert.assertEquals(expect, result);
			startIdx = endIndex;
			indexOf = baseText.indexOf(expect, startIdx);
		}
	}
}
