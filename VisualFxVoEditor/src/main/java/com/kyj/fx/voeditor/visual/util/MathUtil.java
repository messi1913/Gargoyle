/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 5. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class MathUtil {

	/********************************
	 * 작성일 : 2016. 5. 31. 작성자 : KYJ
	 *
	 * 최대공약수
	 *
	 * @param _a
	 * @param _b
	 * @return
	 ********************************/
	public static long gcd(long _a, long _b) {
		long a = _a;
		long b = _b;
		while (b != 0) {
			long temp = a % b;
			a = b;
			b = temp;
		}
		return Math.abs(a);
	}

	/********************************
	 * 작성일 : 2016. 5. 31. 작성자 : KYJ
	 *
	 * 최소공배수
	 *
	 * @param a
	 * @param b
	 * @return
	 ********************************/
	public static long lcm(long a, long b) {
		long gcd_value = gcd(a, b);

		if (gcd_value == 0)
			return 0; // 인수가 둘다 0일 때의 에러 처리

		return Math.abs((a * b) / gcd_value);
	}

	/********************************
	 * 작성일 : 2016. 5. 31. 작성자 : KYJ
	 *
	 * 시퀀스하는 두 숫자 사이의 연속된 값의 합을 리턴. </br>
	 *
	 * ex) a 가 2이고 b가 5일때
	 *
	 * 2,3,4,5의 합을 리턴
	 *
	 * @param a
	 * @param b
	 * @return
	 ********************************/
	public static int sumLange(int _a, int _b) {
		int a = _a;
		int b = _b;

		if (_a > _b) {
			b = _a;
			a = _b;
		}

		return (a + b) * (b - a + 1) / 2;
	}

	public static void main(String[] args) {

		System.out.println(gcd(10, 4));
		System.out.println(lcm(10, 4));
		System.out.println(sumLange(5, 2));
	}
}
