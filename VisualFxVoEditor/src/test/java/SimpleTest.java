/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2016. 6. 23.
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class SimpleTest {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param args
	 */
	public static void main(String[] args) {

		{
			int a = 40;
			int b = 60;
			int[] get5BitRangeSum = get5BitRangeSum(a, b);
			System.out.printf("#1 %d %d\n", get5BitRangeSum[0], get5BitRangeSum[1]);
		}

		{
			int a = 999;
			int b = 1000;
			int[] get5BitRangeSum = get5BitRangeSum(a, b);
			System.out.printf("#2 %d %d\n", get5BitRangeSum[0], get5BitRangeSum[1]);
		}

		{
			int a = 10000;
			int b = 10000;
			int[] get5BitRangeSum = get5BitRangeSum(a, b);
			System.out.printf("#3 %d %d\n", get5BitRangeSum[0], get5BitRangeSum[1]);
		}

		{
			int a = 252415;
			int b = 524535;
			int[] get5BitRangeSum = get5BitRangeSum(a, b);
			System.out.printf("#4 %d %d\n", get5BitRangeSum[0], get5BitRangeSum[1]);
		}

	}

	public static int[] get5BitRangeSum(int start, int end) {

		// 두수사이 5비트 합
		int _5bitSum = 0;

		// 처음 5비트 수
		int first5bitNum = 0;
		int[] result = new int[2];

		for (int i = start; i <= end; i++) {
			if (is5bit(i)) {

				// 처음 5비트
				if (first5bitNum == 0)
					first5bitNum = i;

				_5bitSum += i;

			}
		}

		result[0] = _5bitSum;
		result[1] = first5bitNum;

		return result;
	}

	/**
	 * 5bit수 ?
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 23.
	 * @param num
	 * @return
	 */
	public static boolean is5bit(int num) {
		int MAX = 5;
		String binaryString = Integer.toBinaryString(num);

		int counting = 0;
		for (int i = 0; i < binaryString.length(); i++) {
			if (binaryString.charAt(i) == '1') {
				counting++;
			}
			if (counting > MAX)
				return false;
		}
		if (counting != MAX)
			return false;

		return true;

	}

}
