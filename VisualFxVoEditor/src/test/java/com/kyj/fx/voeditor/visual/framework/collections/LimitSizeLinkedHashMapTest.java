package com.kyj.fx.voeditor.visual.framework.collections;

import org.junit.Assert;
import org.junit.Test;

public class LimitSizeLinkedHashMapTest {

	@Test
	public void test() {
		LimitSizeLinkedHashMap<String, Object> limitSizeLinkedHashMap = new LimitSizeLinkedHashMap<>(3);

		limitSizeLinkedHashMap.put("1", 1);
		limitSizeLinkedHashMap.put("2", 1);
		limitSizeLinkedHashMap.put("3", 1);

		System.out.println(limitSizeLinkedHashMap);

		limitSizeLinkedHashMap.put("4", 1);

		System.out.println(limitSizeLinkedHashMap);
		Assert.assertEquals(3, limitSizeLinkedHashMap.size());

		limitSizeLinkedHashMap.remove("2");
		System.out.println(limitSizeLinkedHashMap);
	}

}
