/**
 * 
 */
package com.kyj.fx.voeditor.visual.example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hong
 *
 */
public class FunctionalProgramingExam {

	public static void main(String[] args) {

		int[] arr = new int[] { 1, 3, 5, 2, 1, 5, 8, 9, 10, 22 };
		
		String str = "짝수";
		Filter filter = null;
		if ("짝수".equals(str)) {
			filter = new JJacSu();
		} else {
			filter = new Filter3Lower();
		}
		List<Integer> print = print(arr, filter);

		System.out.println(print);
	}
	
	
	

	public static List<Integer> print(int[] arr, Filter filter) {

		List<Integer> arrayList = new ArrayList<>();
		for (int i : arr) {
			if (filter.doFilter(i) != -1) {
				arrayList.add(i);
			}

		}
		return arrayList;
	}
	//
	// public static List<Integer> print3lower(int[] arr) {
	//
	// List<Integer> arrayList = new ArrayList<>();
	// for (int i : arr) {
	// if (i < 3) {
	// System.out.println(i);
	// arrayList.add(i);
	// }
	// }
	// return arrayList;
	// }

}
