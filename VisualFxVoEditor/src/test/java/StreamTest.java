import java.util.stream.Stream;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2016. 5. 10.
 *	작성자   : KYJ
 *******************************/

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class StreamTest {

	/********************************
	 * 작성일 : 2016. 5. 10. 작성자 : KYJ
	 *
	 * skip과 limit 사용 이해하기
	 *
	 ********************************/
	@Test
	public void simple() {
		int current = 0;
		int limit = 10;

		Stream.iterate(1, n -> 1 + n).skip(current).limit(limit).forEach(System.out::print);
		current += limit;
		System.out.println();
		Stream.iterate(1, n -> 1 + n).skip(current).limit(limit).forEach(System.out::print);
		System.out.println();
		current += limit;
		Stream.iterate(1, n -> 1 + n).skip(current).limit(limit).forEach(System.out::print);
		System.out.println();
		current += limit;
		Stream.iterate(1, n -> 1 + n).skip(current).limit(limit).forEach(System.out::print);
		current += limit;

	}
}
