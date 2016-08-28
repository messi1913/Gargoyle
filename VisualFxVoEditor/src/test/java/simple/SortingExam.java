package simple;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2016. 6. 16.
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class SortingExam {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 16.
	 * @param args
	 */
	public static void main(String[] args) {
		List<Integer> asList = Arrays.asList(1, 5, 3, 7, -1);

		asList.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {

				int compare = Integer.compare(o1, o2);

				return compare;
			}
		});

		System.out.println(asList);
	}

}
