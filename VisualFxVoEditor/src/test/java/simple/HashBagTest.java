/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : simple
 *	작성일   : 2016. 10. 18.
 *	작성자   : KYJ
 *******************************/
package simple;

import org.apache.commons.collections.bag.HashBag;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class HashBagTest {

	@Test
	public void test() {
		HashBag hashBag = new HashBag();
		hashBag.add("zzz");
		hashBag.add("zzz");
		hashBag.add("zzz");
		hashBag.add("zzz1");

		System.out.println(hashBag.getCount("zzz"));

		hashBag.forEach(System.out::println);

	}

}
