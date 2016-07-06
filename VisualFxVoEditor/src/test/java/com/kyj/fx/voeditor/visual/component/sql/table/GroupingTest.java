/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class GroupingTest {

	/**
	 * Primary Key, Foregin Key별로 데이터를 그룹화시키는 예제
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 */
	@Test
	public void groupingTest() {
		List<TableIndexNode> items = new ArrayList<>();
		items.add(new TableIndexNode("PRIMARY KEY", "PK1"));
		items.add(new TableIndexNode("PRIMARY KEY", "PK2"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX1"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX2"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX3"));

		Map<String, List<TableIndexNode>> collect = items.stream().collect(Collectors.groupingBy(TableIndexNode::getType));
		System.out.println(collect);

	}

	/**
	 * 부모노드를 추가하는 트리노드 생성
	 *
	 * 예를들면 PRIMARY KEY 혹은 FOREIGN KEY 노드 유형을 만나면 그 부모노드를 추가한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 */
	@Test
	public void addItemAndgroupingTest() {
		List<TableIndexNode> items = new ArrayList<>();
		items.add(new TableIndexNode("PRIMARY KEY", "PK1"));
		items.add(new TableIndexNode("PRIMARY KEY", "PK2"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX1"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX2"));
		items.add(new TableIndexNode("FOREIGN KEY", "FX3"));

		List<TableIndexNode> collect = items.stream().collect(new Supplier<List<TableIndexNode>>() {

			@Override
			public List<TableIndexNode> get() {
				List<TableIndexNode> arrayList = new ArrayList<>();
				arrayList.add(new TableIndexNode("root", "root"));
				return arrayList;
			}

		}, (collections, next) -> {
			// root
			TableIndexNode tableIndexNode = collections.get(0);
			List<TableIndexNode> childrens = tableIndexNode.getChildrens();

			String type = next.getType();

			// root밑 부모 추가.
			if (childrens.isEmpty() || !childrens.get(childrens.size() - 1).getType().equals(type)) {
				TableIndexNode e = new TableIndexNode(type, type);
				e.getChildrens().add(next);
				childrens.add(e);

			} else {
				childrens.get(childrens.size() - 1).getChildrens().add(next);
			}

		} , (a, b) -> {
			/* 아래함수는 동작하지않는데 일단 지켜보자. */
			a.addAll(b);
		});

		collect.forEach(System.out::println);

	}
}
