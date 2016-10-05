/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.model
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import com.kyj.fx.voeditor.visual.component.config.item.node.AbstractRunItem;
import com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem;

/**
 * 실행가능한 아이템에 대한 정의 클래스들을 로딩
 * @author KYJ
 *
 */
public class RunItemCatchModel {

	private Map<String, AbstractRunItem> cache = new HashMap<>();

	/**
	 * construct
	 */
	public RunItemCatchModel() {

		ServiceLoader<AbstractRunItem> languageLoader = ServiceLoader.load(AbstractRunItem.class);
		Iterator<AbstractRunItem> iterator = languageLoader.iterator();
		while (iterator.hasNext()) {

			AbstractRunItem next = iterator.next();
			String name = next.getName();
			cache.put(name, next);

		}
	}

	/**
	 * return all items
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @return
	 */
	public Collection<AbstractRunItem> getAllItems() {
		return cache.values();
	}

	/**
	 * get Item by name
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 5.
	 * @param name
	 * @return
	 */
	public IRunableItem getItem(String name) {
		return this.cache.get(name);
	}
}
