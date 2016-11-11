/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.main.scanning
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.scanning;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.kyj.fx.voeditor.visual.main.initalize.GagoyleInitializable;

/**
 * 리소스 스캐너
 *
 * @author KYJ
 *
 */
public class ResourceScanner {

	private static final String PACKAGE_PREFIX = "com.kyj";
	private static ResourceScanner scanner;
	private static Reflections reflections;

	/**
	 * 객체로딩
	 *
	 * @return
	 */
	public static ResourceScanner getInstance() {
		if (scanner == null) {
			scanner = new ResourceScanner();
		}
		return scanner;
	}

	/**
	 * 가고일 프로젝트만 리소스 로딩처리한다.
	 *
	 * 객체생성을 방지하기위해 private로 선언함.
	 *
	 * getInstance()함수를 통해 접근할것
	 */
	protected ResourceScanner() {
		reflections = new Reflections(
				new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(PACKAGE_PREFIX, ClassLoader.getSystemClassLoader())));
	}

	/**
	 * GagoyleInitializable어노테이션이 붙은항목 클래스를 찾으며 찾은 항목은 Initialable Consumer를 통해 작업처리를한다.
	 *
	 * @param consumer
	 */
	public void initialize(Consumer<Class<?>> consumer) {
		initialize(GagoyleInitializable.class, consumer);
	}

	private void initialize(Class<? extends Annotation> annotation, Consumer<Class<?>> consumer) {
		Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation, true);
		types.parallelStream().forEach(consumer);
	}

	/**
	 * GagoyleInitializable어노테이션이 붙은항목 클래스를 찾으며 찾은 항목은 Consumer를 통해 작업처리를한다.
	 *
	 * @param consume
	 */
	public void initialize(Annotation annotation, Consumer<Class<?>> consume) {
		Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation);
		types.parallelStream().forEach(consume);
	}
}
