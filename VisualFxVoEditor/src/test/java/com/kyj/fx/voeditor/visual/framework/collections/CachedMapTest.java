/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.collections
 *	작성일   : 2016. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.collections;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class CachedMapTest {

	@Test
	public void simple() throws InterruptedException {
		CachedMap<Object, Object> cachedMap = new CachedMap<>(1000);

		cachedMap.put("sample", "zz");
		cachedMap.put("sample22", "zz");

		System.out.println(cachedMap.get("sample"));

		Thread.sleep(1500);

		System.out.println(cachedMap.get("sample"));
		System.out.println(cachedMap.get("sample22"));

		HashedMap hashedMap = new HashedMap();
		hashedMap.put("sample", "zz");
		hashedMap.put("sample22", "zz");

		cachedMap.putAll(hashedMap);

		System.out.println(cachedMap);
		Thread.sleep(1500);

		System.out.println(cachedMap);

	}

	@Test
	public void miltiThread() throws InterruptedException {

		for (int i = 0; i < 10; i++) {
			final int inn = i;
			Thread thread = new Thread(() -> {

				String name = Thread.currentThread().getName();
				CachedMap<Object, Object> cachedMap = new CachedMap<>(1000);

				cachedMap.put("sample", "zz" + inn);
				cachedMap.put("sample22", "zz" + inn);

				System.out.println(name + " " + cachedMap.get("sample"));

				try {
					Thread.sleep(1500);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println(name + " " + cachedMap.get("sample"));
				System.out.println(name + " " + cachedMap.get("sample22"));

				HashedMap hashedMap = new HashedMap();
				hashedMap.put("sample", "zz" + inn);
				hashedMap.put("sample22", "zz" + inn);

				cachedMap.putAll(hashedMap);

				System.out.println(name + " " + cachedMap);
				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println(name + " " + cachedMap);
			} , "Name" + i);

			thread.start();
		}

		Thread.currentThread().sleep(5000);
	}

	@Test
	public void miltiThread2() throws InterruptedException {

		CachedMap<Object, Object> cachedMap = new CachedMap<>(1000);
		for (int i = 0; i < 10; i++) {
			final int inn = i;
			Thread thread = new Thread(() -> {

				String name = Thread.currentThread().getName();

//				cachedMap.put(name, "zz" + inn);
				cachedMap.put(name, "zz" + inn);
				cachedMap.put(name, "zz" + inn);
				cachedMap.put(name, "zz" + inn);
				cachedMap.put(name, "zz" + inn);
				cachedMap.put(name, "zz" + inn);

				System.out.println(name + " " + cachedMap.get(name));

				try {
					Thread.sleep(1500);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				System.out.println(name + " " + cachedMap.get(name));


				HashedMap hashedMap = new HashedMap();
				hashedMap.put(name, "zz" + inn);

				cachedMap.putAll(hashedMap);

				System.out.println(name + " " + cachedMap);
				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					e.printStackTrace();
				}

				System.out.println(name + " " + cachedMap);
			} , "Name" + i);

			thread.start();
		}

		System.out.println(cachedMap.isEmpty());
		Thread.currentThread().sleep(5000);
		System.out.println(cachedMap.isEmpty());
	}
}
