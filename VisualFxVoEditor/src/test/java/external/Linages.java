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
		int adena;

		public Meta(int marpru, int neends, long percent, int adena) {
			this.neends = neends;
			this.percent = percent;
			this.marpru = marpru;
			this.adena = adena;
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

		/**
		 * @return the adena
		 */
		public final int getAdena() {
			return adena;
		}

	}

	@Before
	public void setting() {

		map.put(1, new Meta(0, 1, 100, 3400));
		map.put(2, new Meta(0, 1, 100, 3400));
		map.put(3, new Meta(0, 1, 100, 3400));
		map.put(4, new Meta(1, 1, 90, 3400));
		map.put(5, new Meta(2, 2, 70, 16700));
		map.put(6, new Meta(2, 2, 60, 16700));
		map.put(7, new Meta(3, 2, 45, 16700));
		map.put(8, new Meta(3, 2, 45, 16700));
		map.put(9, new Meta(4, 2, 45, 16700));
		map.put(10, new Meta(4, 3, 45, 33400));
		map.put(11, new Meta(5, 3, 45, 33400));
		map.put(12, new Meta(5, 3, 45, 33400));
		map.put(13, new Meta(6, 3, 40, 33400));
		map.put(14, new Meta(6, 3, 40, 33400));
		map.put(15, new Meta(7, 4, 40, 33400));
		map.put(16, new Meta(7, 4, 40, 66700));
		map.put(17, new Meta(8, 4, 40, 66700));
		map.put(18, new Meta(8, 4, 35, 66700));
		map.put(19, new Meta(9, 4, 35, 66700));
		map.put(20, new Meta(9, 5, 35, 66700));
		map.put(21, new Meta(10, 5, 35, 66700));
		map.put(22, new Meta(10, 5, 35, 66700));
		map.put(23, new Meta(11, 5, 30, 66700));
		map.put(24, new Meta(11, 5, 30, 66700));
		map.put(25, new Meta(12, 6, 30, 66700));
		map.put(26, new Meta(12, 6, 30, 100000));
		map.put(27, new Meta(13, 6, 30, 100000));
		map.put(28, new Meta(13, 6, 25, 100000));
		map.put(29, new Meta(14, 6, 25, 100000));
		map.put(30, new Meta(14, 7, 25, 100000));

	}

	int currentLevel = 21;

	static final String RESULT = "확률 : %d%% \t %d -> %d \t 강화 %s (남은 줌서 %d)\t(%d장 소모) \t(남은 아데나 %,3d) \t(%,3d 아데나 소모)";
	static final String SUCCESS_WORD = "성공";
	static final String FAIL_WORD = "실패";

	@Test
	public void test() {
		//보유 강화 주문서수
		int iGot = 100;
		//보유 아데나
		int iGotAdena = 1000000000;
		//강화에 소모된 누적 아데나
		int accumulateAdena = 0;
		//		int iGotStrengthPageCount = 10;
		System.out.println("가지고있는 주문서수 " + iGot);
		System.out.println(String.format("보유 아데나 %,3d", iGotAdena));
		System.out.println("현재 강화 레벨 " + currentLevel);

		System.out.println("############################################################################");
		//		for (int i = 0; i < iGotStrengthPageCount; i++) {
		while (true) {
			boolean result = doit(currentLevel);
			int needs = getNeeds(currentLevel);
			int adena = getAdena(currentLevel);
			long percent = getPercent(currentLevel);
			iGot = iGot - needs;
			iGotAdena = iGotAdena - adena;
			accumulateAdena += adena;
			if (iGotAdena <= 0) {
				System.out.println("아데나가 모자랍니다.");
				break;
			}
			if (needs <= 0 || iGot <= 0)
				break;
			String resultMsg = "";
			if (result) {
				resultMsg = String.format(RESULT, percent, currentLevel, currentLevel + 1, SUCCESS_WORD, iGot, needs, iGotAdena,
						accumulateAdena);
				currentLevel++;
			} else {
				resultMsg = String.format(RESULT, percent, currentLevel, currentLevel - 1, FAIL_WORD, iGot, needs, iGotAdena,
						accumulateAdena);
				currentLevel--;
			}

			//			System.out.println("가지고있는 주문서수 " + iGot);
			System.out.println(resultMsg);
		}
		System.out.println("############################################################################");
		System.out.println(String.format("결과 : %d 강", currentLevel));

	}

	public int getAdena(int level) {
		Meta meta = map.get(level);
		return meta.getAdena();
	}

	public int getNeeds(int level) {
		Meta meta = map.get(level);
		return meta.getNeends();
	}

	public long getPercent(int level) {
		Meta meta = map.get(level);
		return meta.getPercent();
	}

	public boolean doit(int level) {
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
