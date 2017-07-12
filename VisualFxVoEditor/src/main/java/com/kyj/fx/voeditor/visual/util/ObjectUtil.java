/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2017. 3. 31.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author KYJ
 *
 */
public class ObjectUtil {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 31. 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(Object obj) {
		return toMap(obj, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 3. 31. 
	 * @param obj
	 * @param filter
	 * @return
	 * @throws IntrospectionException 
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(Object t, Predicate<String> fieldNameFilter) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(t.getClass());
		} catch (IntrospectionException e1) {
			throw new RuntimeException(e1);
		}

		Map<String, Object> hashMap = new HashMap<String, Object>();
		// Iterate over all the attributes
		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

			// write메소드와 read메소드가 존재할때만.
			Method writeMethod = descriptor.getWriteMethod();
			Method readMethod = descriptor.getReadMethod();
			if (ValueUtil.isEmpty(writeMethod) || ValueUtil.isEmpty(readMethod)) {
				continue;
			}
			// Class<?> returnType = readMethod.getReturnType();

			String methodName = ValueUtil.getSimpleMethodName(readMethod.getName());

			if (fieldNameFilter != null) {
				if (fieldNameFilter.test(methodName))
					continue;
			}

			Object originalValue = null;
			try {
				originalValue = readMethod.invoke(t);
			} catch (Exception e) {
			}

			if (ValueUtil.isNotEmpty(originalValue)) {
				hashMap.put(methodName, originalValue);
			} else {
				hashMap.put(methodName, null);
			}
		}
		return hashMap;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 5. 
	 * @param value
	 * @param name
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getDeclaredFieldValue(Object value, String name)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = value.getClass().getDeclaredField(name);
		if (field == null)
			return null;
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		return field.get(value);
	}
}
