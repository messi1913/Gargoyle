/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2017. 2. 8.
 *	작성자   : KYJ
 *******************************/
package external;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import scala.util.Random;

/**
 * @author KYJ
 *
 */
public class Linages {

	Map<Integer, Meta> map = new HashMap<>();

	class Meta {
		int neends;
		long percent;
		int marpru;

		public Meta(int marpru, int neends, long percent) {
			this.neends = neends;
			this.percent = percent;
			this.marpru = marpru;
		}

		public int getNeends() {
			return neends;
		}

		public long getPercent() {
			return percent;
		}

		public int getMarpru() {
			return marpru;
		}

	}

	@Before
	public void setting() {

		map.put(1, new Meta(0, 1, 100));
		map.put(2, new Meta(0, 1, 100));
		map.put(3, new Meta(0, 1, 100));
		map.put(4, new Meta(1, 1, 90));
		map.put(5, new Meta(2, 2, 70));
		map.put(6, new Meta(2, 2, 60));
		map.put(7, new Meta(3, 2, 45));
		map.put(8, new Meta(3, 2, 45));
		map.put(9, new Meta(4, 2, 45));
		map.put(10, new Meta(4, 3, 45));
		map.put(11, new Meta(5, 3, 45));
		map.put(12, new Meta(5, 3, 45));
		map.put(13, new Meta(6, 3, 40));
		map.put(14, new Meta(6, 3, 40));
		map.put(15, new Meta(7, 4, 40));
		map.put(16, new Meta(7, 4, 40));
		map.put(17, new Meta(8, 4, 40));
		map.put(18, new Meta(8, 4, 35));
		map.put(19, new Meta(9, 4, 35));
		map.put(20, new Meta(9, 5, 35));
		map.put(21, new Meta(10, 5, 35));
		map.put(22, new Meta(10, 5, 35));
		map.put(23, new Meta(11, 5, 30));
		map.put(24, new Meta(11, 5, 30));
		map.put(25, new Meta(12, 6, 30));
		map.put(26, new Meta(12, 6, 30));
		map.put(27, new Meta(13, 6, 30));
		map.put(28, new Meta(13, 6, 25));
		map.put(29, new Meta(14, 6, 25));
		map.put(30, new Meta(14, 7, 25));

	}

	int currentLevel = 15;
	int iGot = 30;

	static final String RESULT = "%d -> %d 강화 %s";
	static final String SUCCESS_WORD = "성공";
	static final String FAIL_WORD = "실패";

	@Test
	public void test() {

		int iGotStrengthPageCount = 10;
		System.out.println("시작  시도수 " + iGotStrengthPageCount);
		System.out.println("현재 강화 레벨 " + currentLevel);

		for (int i = 0; i < iGotStrengthPageCount; i++) {
			boolean result = doit(currentLevel, iGot);
			String resultMsg = "";
			if (result) {
				resultMsg = String.format(RESULT, currentLevel, currentLevel + 1, SUCCESS_WORD);
				currentLevel++;
			} else {
				resultMsg = String.format(RESULT, currentLevel, currentLevel - 1, FAIL_WORD);
				currentLevel--;
			}

			System.out.println(resultMsg);
		}

		System.out.println("결과 : " + currentLevel);

	}

	public boolean doit(int level, int iGot) {
		Meta meta = map.get(level);
		long percent = 100L;
		if (level != 0) {
			percent = meta.getPercent();
		}

		Random random = new Random();
		int nextInt = random.nextInt(100);

		if (0 < nextInt && nextInt < percent)
			return true;
		return false;
	}

}
