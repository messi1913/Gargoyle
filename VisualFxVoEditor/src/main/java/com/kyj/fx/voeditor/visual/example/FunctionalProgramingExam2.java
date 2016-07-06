/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Hong
 *
 */
public class FunctionalProgramingExam2 {

	public static void main(String[] args) {

		List<Integer> asList = Arrays.asList(1, 3, 5, 2, 1, 5, 8, 9, 10, 22);

		Stream<Integer> of = asList.parallelStream();

		Optional<CodeDVO> max = of.filter(new Predicate<Integer>() {
			@Override
			public boolean test(Integer arg0) {
				if (arg0 % 2 == 0)
					return true;
				return false;
			}
		}).map(new Function<Integer, CodeDVO>() {
			@Override
			public CodeDVO apply(Integer arg0) {
				return new CodeDVO(arg0.toString(), arg0.toString());
			}
		}).max(new Comparator<CodeDVO>() {
			@Override
			public int compare(CodeDVO arg0, CodeDVO arg1) {
				return arg0.getCode().length() > arg1.getCode().length() ? arg0.getCode().length()
						: arg1.getCode().length();
			}
		});

		// of.forEach(new Consumer<Integer>() {
		//
		// @Override
		// public void accept(Integer param) {
		// System.out.print(param);
		// }
		// });
		//
		// of.forEach(param -> {
		// System.out.print(param);
		// });
		//
		// of.forEach(param -> System.out.print(param));
		//
		// of.forEach(System.out::print);
	}

}
