import org.junit.Test;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 11. 10.
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class ExceptionTest {

	public static void testMethod() throws Exception {
		try {
			System.out.println("statement scope");
			int i = 1 / 0;
		} catch (Exception e) {
			System.out.println("Exception scope");
			throw new Exception(e);
		} finally {
			System.out.println("finally scope");
		}
	}
	
	@Test
	public void test()
	{
		try {
			testMethod();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
