/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 4. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Null 혹은 콜렉션에서 빈값유형을 체크한후 Scope처리를 지원하는 유틸리티
 * 
 * @author KYJ
 *
 */
public class NullExpresion {

	private NullExpresion() {
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 7.
	 * @param o
	 * @param consumer
	 */
	public static <T> void ifNullDo(T o, Consumer<T> consumer) {
		ifNullDo(o, consumer, (nullvalue) -> {
		});
	}

	public static <T> void ifNullDo(T o, Consumer<T> consumer, Consumer<Void> other) {
		ifNotNullDo(o, t -> t == null, consumer, other);
	}

	public static <T> void ifNotNullDo(T o, Consumer<T> consumer) {
		ifNotNullDo(o, consumer, (nullvalue) -> {
		});
	}

	public static <T> void ifNotNullDo(T o, Consumer<T> consumer, Consumer<Void> other) {
		ifNotNullDo(o, t -> t != null, consumer, other);
	}

	/**
	 * List가 null이거나 empty인경우 수행.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 * @param o
	 * @param filter
	 * @param consumer
	 * @param other
	 */
	public static <T extends List<?>> void ifListEmptyDo(T o, Consumer<T> consumer, Consumer<Void> other) {
		ifNotNullDo(o, t -> (t == null || t.isEmpty()), consumer, other);
	}

	public static <T extends List<?>> void ifListNotEmptyDo(T o, Consumer<T> consumer) {
		ifNotNullDo(o, t -> ValueUtil.isNotEmpty(t), consumer, t -> {
		});
	}

	public static <T extends List<?>> void ifListEmptyDo(T o, Consumer<T> consumer) {
		ifNotNullDo(o, t -> (t == null || t.isEmpty()), consumer, t -> {
		});
	}

	public static <T> void ifNotNullDo(T o, Predicate<T> filter, Consumer<T> consumer, Consumer<Void> other) {

		if (filter.test(o)) {
			consumer.accept(o);
		} else {
			other.accept(null);
		}

	}

}
