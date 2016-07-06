/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 3. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class PivotTableViewExam extends Application {

	// Header header = new Header(new Header("week", "week"), "vocStatusNm",
	// "vocStatusNm");

	// Header header = new Header(Arrays.asList(new Header("vocStatusNm",
	// "vocStatusNm")), "week", "week");
	// GroupingSet headerData = new GroupingSet("cnt", "sysCode");

	Object get(Object obj, String field) throws Exception {

		Method m = obj.getClass().getDeclaredMethod("get".concat(ValueUtil.getIndexUppercase(field, 0)));
		if (m != null) {
			m.setAccessible(true);
			return m.invoke(obj);
		}
		return "";
	}

	List<Map<String, Object>> groupBy(Header header, List<VocStA001DVO> arrayList) {
		String valueField = header.groupingSet.getValue();
		String groups = header.getGroupingSet().getGroups();
		System.out.println(header.getCategoryName());
		Set<String> categoryNameSet = header.getCategoryNameSet();
		String categoryName = header.getCategoryName();
		System.out.println(categoryNameSet);

		Map<String, List<VocStA001DVO>> collect = arrayList.stream()
				.collect(Collectors.groupingBy(VocStA001DVO::getSysCode, Collectors.toList()));

		return collect.entrySet().stream().map(ent -> {

			Map<String, Object> hashMap = new LinkedHashMap<String, Object>();
			for (VocStA001DVO value : ent.getValue()) {

				try {
					hashMap.put(groups, get(value, groups));
					String key = "";
					for (String set : categoryNameSet) {
						key = key.concat(get(value, set).toString()).concat("_");
					}

					hashMap.put(key, get(value, valueField));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return hashMap;
		}).collect(Collectors.toList());

	}

	/**
	 * 계층구조 테이블 컬럼 생성
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 18.
	 * @param header
	 * @param arrayList
	 * @return
	 */
	List<TableColumn<Map<String, Object>, Object>> groupByColumn(Header header, List<VocStA001DVO> arrayList) {

		Map<String, Map<String, List<VocStA001DVO>>> collect = arrayList.stream()
				.collect(Collectors.groupingBy(VocStA001DVO::getWeek, Collectors.groupingBy(VocStA001DVO::getVocStatusNm)));

		String groups = header.getGroupingSet().getGroups();
		List<TableColumn<Map<String, Object>, Object>> columns = FXCollections.observableArrayList();
		// Group
		{
			TableColumn<Map<String, Object>, Object> tableColumn = new TableColumn<Map<String, Object>, Object>();
			tableColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().get(groups)));
			columns.add(tableColumn);
		}
		// Dynamic..
		{
			columns.addAll(collect.entrySet().stream().map(ent -> {
				Map<String, List<VocStA001DVO>> value = ent.getValue();
				TableColumn<Map<String, Object>, Object> tb = new TableColumn<>(ent.getKey());
				List<TableColumn<Map<String, Object>, Object>> _subColumns = value.entrySet().stream().map(_ent -> {
					TableColumn<Map<String, Object>, Object> tableColumn = new TableColumn<>(_ent.getKey());
					tableColumn.setCellValueFactory(
							param -> new SimpleObjectProperty<>(param.getValue().get(ent.getKey() + "_" + _ent.getKey() + "_")));
					return tableColumn;
				}).collect(Collectors.toList());
				tb.getColumns().addAll(_subColumns);
				return tb;

			}).collect(Collectors.toList()));
		}

		return columns;
	}

	@Deprecated
	TableColumn<Map<String, Object>, Object> createColumn(String column) {
		TableColumn<Map<String, Object>, Object> tableColumn = new TableColumn<>(column);
		tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map<String, Object>, Object>, ObservableValue<Object>>() {
			@Override
			public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>, Object> param) {
				return new SimpleObjectProperty<>(param.getValue().get(column));
			}
		});

		return tableColumn;
	}

	// List<TableColumn<Map<String, Object>, Object>>
	// createColumns(List<Map<String, Object>> collect, Collection<String>
	// columns) {
	//
	// TableColumn<Map<String, Object>, Object> tableColumn = new
	// TableColumn<>(column);
	// tableColumn.setCellValueFactory(new
	// Callback<TableColumn.CellDataFeatures<Map<String, Object>, Object>,
	// ObservableValue<Object>>() {
	//
	// @Override
	// public ObservableValue<Object> call(CellDataFeatures<Map<String, Object>,
	// Object> param) {
	// return new SimpleObjectProperty<>(column);
	// }
	// });
	//
	// return tableColumn;
	// }

	@Deprecated
	List<TableColumn<VocStA001DVO, String>> createTableColumns(Header header, List<VocStA001DVO> arrayList) {

		GroupingSet groupingSet = header.getGroupingSet();
		String groups = groupingSet.getGroups();
		List<TableColumn<VocStA001DVO, String>> collect = Stream.of(groups).map(str -> {
			TableColumn<VocStA001DVO, String> c = new TableColumn<>();
			c.setText(str);
			return c;
		}).collect(Collectors.toList());

		collect.addAll(arrayList.stream().map(v -> v.getWeek()).distinct().map(str -> {
			TableColumn<VocStA001DVO, String> createTableColumn = createTableColumn(header, arrayList);
			createTableColumn.setText(str);
			return createTableColumn;
		}).collect(Collectors.toList()));

		return collect;
	}

	@Deprecated
	TableColumn<VocStA001DVO, String> createTableColumn(Header header, List<VocStA001DVO> arrayList) {

		List<Header> childrens = header.getChildrens();
		String name = header.getName();
		String propertyName = header.getPropertyName();

		TableColumn<VocStA001DVO, String> tableColumn = new TableColumn<VocStA001DVO, String>();
		tableColumn.setText(name);
		tableColumn.setId(propertyName);

		if (childrens != null) {
			List<TableColumn<VocStA001DVO, String>> columns = FXCollections.observableArrayList();

			for (Header h : childrens) {
				List<TableColumn<VocStA001DVO, String>> collect = arrayList.stream().map(vo -> vo.getVocStatusNm()).distinct().map(str -> {
					TableColumn<VocStA001DVO, String> column = new TableColumn<VocStA001DVO, String>();
					column.setText(str);
					// column.setCellValueFactory(new
					// PropertyValueFactory<>(h.getPropertyName()));
					return column;
				}).collect(Collectors.toList());

				columns.addAll(collect);
			}
			tableColumn.getColumns().addAll(columns);
		}
		return tableColumn;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		// TableView<VocStA001DVO> root = new TableView<VocStA001DVO>();

		TableView<Map<String, Object>> root = new TableView<>();

		root.setOnMouseClicked(event -> {
			System.out.println(root.getSelectionModel().getSelectedItem());
		});

		// 샘플데이터
		List<VocStA001DVO> arrayList = new ArrayList<VocStA001DVO>();
		arrayList.add(getSample("HQ", "11", "등록", "2016W1", 6));
		arrayList.add(getSample("HQ", "12", "로컬접수", "2016W1", 1));
		arrayList.add(getSample("HQ", "13", "HQ접수", "2016W1", 0));
		arrayList.add(getSample("HQ", "14", "PI리더", "2016W1", 4));
		arrayList.add(getSample("HQ", "15", "PI실무", "2016W1", 6));

		arrayList.add(getSample("HQ", "11", "등록", "2016W2", 23));
		arrayList.add(getSample("HQ", "12", "로컬접수", "2016W2", 32));
		arrayList.add(getSample("HQ", "13", "HQ접수", "2016W2", 1));
		arrayList.add(getSample("HQ", "14", "PI리더", "2016W2", 5));
		arrayList.add(getSample("HQ", "15", "PI실무", "2016W2", 9));

		arrayList.add(getSample("HQ", "11", "등록", "2016W3", 50));
		arrayList.add(getSample("HQ", "12", "로컬접수", "2016W3", 1));
		arrayList.add(getSample("HQ", "13", "HQ접수", "2016W3", 3));
		arrayList.add(getSample("HQ", "14", "PI리더", "2016W3", 1));
		arrayList.add(getSample("HQ", "15", "PI실무", "2016W3", 8));

		arrayList.add(getSample("SEDA", "11", "등록", "2016W1", 12));
		arrayList.add(getSample("SEDA", "12", "로컬접수", "2016W1", 2));
		arrayList.add(getSample("SEDA", "13", "HQ접수", "2016W1", 1));
		arrayList.add(getSample("SEDA", "14", "PI리더", "2016W1", 8));
		arrayList.add(getSample("SEDA", "15", "PI실무", "2016W1", 12));

		arrayList.add(getSample("SEDA", "11", "등록", "2016W2", 46));
		arrayList.add(getSample("SEDA", "12", "로컬접수", "2016W2", 64));
		arrayList.add(getSample("SEDA", "13", "HQ접수", "2016W2", 2));
		arrayList.add(getSample("SEDA", "14", "PI리더", "2016W2", 10));
		arrayList.add(getSample("SEDA", "15", "PI실무", "2016W2", 18));

		arrayList.add(getSample("SEDA", "11", "등록", "2016W3", 100));
		arrayList.add(getSample("SEDA", "12", "로컬접수", "2016W3", 2));
		arrayList.add(getSample("SEDA", "13", "HQ접수", "2016W3", 6));
		arrayList.add(getSample("SEDA", "14", "PI리더", "2016W3", 2));
		arrayList.add(getSample("SEDA", "15", "PI실무", "2016W3", 16));

		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		Header header = new Header(Arrays.asList(new Header("vocStatusNm", "vocStatusNm")), "week", "week");
		GroupingSet headerData = new GroupingSet("cnt", "sysCode");
		header.setGroupingSet(headerData);

		List<Map<String, Object>> groupBy = groupBy(header, arrayList);
		List<TableColumn<Map<String, Object>, Object>> groupByColumns = groupByColumn(header, arrayList);
		// List<TableColumn<Map<String, Object>, Object>> collect =
		// groupBy.get(0).keySet().stream().map(k -> createColumn(k))
		// .collect(Collectors.toList());

		root.getColumns().addAll(groupByColumns);
		root.getItems().addAll(groupBy);

		// List<TableColumn<VocStA001DVO, String>> columns =
		// createTableColumns(header, arrayList);
		// root.getColumns().addAll(columns);

		// root.getItems().addAll(arrayList);

	}

	/**
	 * 빈을 Map으로 변환한다. 기본형 데이터만 Map으로 변환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 12.
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<String, Object> toMap(final T t) {
		return toMap(t, true);
	}

	/**
	 * 빈을 Map으로 변환한다. 기본형 데이터만 Map으로 변환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 12.
	 * @param t
	 * @param isNullBinding
	 *            값이 빈경우 map에 바인딩할지 유무를 지정.
	 * @return
	 * @throws Exception
	 */
	public static <T> Map<String, Object> toMap(final T t, boolean isNullBinding) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());

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

				Object originalValue = readMethod.invoke(t);

				// 2015.11.19 수정자:박종광 기본형 이외의 값 String으로 변환하여 Map생성하는 로직 제거 SQL
				// SET문등 수행시 타입 오류 발생
				if (ValueUtil.isNotEmpty(originalValue)) {
					hashMap.put(methodName, originalValue);
				} else {
					if (isNullBinding)
						hashMap.put(methodName, null);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hashMap;
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**********************************************************************************************/
	/* 이벤트 처리항목 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**********************************************************************************************/
	/* 그 밖의 API 기술 */
	// TODO Auto-generated constructor stub
	/**********************************************************************************************/

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 17.
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param i
	 * @return
	 */
	private VocStA001DVO getSample(String string, String string2, String string3, String string4, int i) {
		VocStA001DVO vocStA001DVO = new VocStA001DVO();
		vocStA001DVO.setSysCode(string);
		vocStA001DVO.setVocStatusCode(string2);
		vocStA001DVO.setVocStatusNm(string3);
		vocStA001DVO.setWeek(string4);
		vocStA001DVO.setCnt(i);
		return vocStA001DVO;
	}

	class PivotProperty {
		private String name;
		private String propertyName;

		/**
		 * @return the name
		 */
		public final String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public final void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the propertyName
		 */
		public final String getPropertyName() {
			return propertyName;
		}

		/**
		 * @param propertyName
		 *            the propertyName to set
		 */
		public final void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

	}

	class Header extends PivotProperty {

		private List<Header> childrens;

		private GroupingSet groupingSet;

		public Header() {
			super();
		}

		public Header(String name) {
			super();
			super.name = name;
		}

		public Header(String name, String propertyName) {
			super();
			super.name = name;
			super.propertyName = propertyName;
		}

		public Header(List<Header> childrens, String name, String propertyName) {
			super();
			this.childrens = childrens;
			super.name = name;
			super.propertyName = propertyName;
		}

		/**
		 * @return the groupingSet
		 */
		public final GroupingSet getGroupingSet() {
			return groupingSet;
		}

		/**
		 * @param groupingSet
		 *            the groupingSet to set
		 */
		public final void setGroupingSet(GroupingSet groupingSet) {
			this.groupingSet = groupingSet;
		}

		/**
		 * @return the childrens
		 */
		public final List<Header> getChildrens() {
			return childrens;
		}

		/**
		 * @param childrens
		 *            the childrens to set
		 */
		public final void setChildrens(List<Header> childrens) {
			this.childrens = childrens;
		}

		public String getCategoryName() {

			StringBuffer sb = new StringBuffer();
			if (childrens != null) {
				for (Header header : childrens) {
					sb.append(header.getCategoryName()).append("_");
				}
			}
			sb.append(super.propertyName);

			// GroupingSet groupingSet = this.groupingSet;
			// if (groupingSet != null) {
			// String groups = groupingSet.groups;
			// if (groups != null)
			// arrayList.addAll(Arrays.asList(groups));
			// }

			return sb.toString();
		}

		public Set<String> getCategoryNameSet() {
			Set<String> arrayList = new LinkedHashSet<String>();
			arrayList.add(super.propertyName);
			if (childrens != null) {
				for (Header header : childrens) {
					arrayList.addAll(header.getCategoryNameSet());
				}
			}
			return arrayList;
		}
	}

	class GroupingSet {
		private String groups;
		private String value;

		public GroupingSet(String value, String groups) {
			super();
			this.value = value;
			this.groups = groups;
		}

		/**
		 * @return the groups
		 */
		public final String getGroups() {
			return groups;
		}

		/**
		 * @return the value
		 */
		public final String getValue() {
			return value;
		}

		/**
		 * @param groups
		 *            the groups to set
		 */
		public final void setGroups(String groups) {
			this.groups = groups;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public final void setValue(String value) {
			this.value = value;
		}

	}

	public class VocStA001DVO {
		private StringProperty sysCode;
		private StringProperty vocStatusCode;
		private StringProperty vocStatusNm;
		private StringProperty week;
		private DoubleProperty cnt;

		public VocStA001DVO() {
			sysCode = new SimpleStringProperty();
			vocStatusCode = new SimpleStringProperty();
			vocStatusNm = new SimpleStringProperty();
			week = new SimpleStringProperty();
			cnt = new SimpleDoubleProperty();

		}

		public final StringProperty sysCodeProperty() {
			return this.sysCode;
		}

		public final String getSysCode() {
			return this.sysCodeProperty().get();
		}

		public final void setSysCode(final String sysCode) {
			this.sysCodeProperty().set(sysCode);
		}

		public final StringProperty vocStatusCodeProperty() {
			return this.vocStatusCode;
		}

		public final String getVocStatusCode() {
			return this.vocStatusCodeProperty().get();
		}

		public final void setVocStatusCode(final String vocStatusCode) {
			this.vocStatusCodeProperty().set(vocStatusCode);
		}

		public final StringProperty weekProperty() {
			return this.week;
		}

		public final String getWeek() {
			return this.weekProperty().get();
		}

		public final void setWeek(final String week) {
			this.weekProperty().set(week);
		}

		/**
		 * @return the vocStatusNm
		 */
		public final String getVocStatusNm() {
			return vocStatusNm.get();
		}

		public final void setVocStatusNm(String vocStatusNmV) {
			this.vocStatusNm.set(vocStatusNmV);
		}

		public final StringProperty vocStatusNmProperty() {
			return this.vocStatusNm;
		}

		public final DoubleProperty cntProperty() {
			return this.cnt;
		}

		public final double getCnt() {
			return this.cntProperty().get();
		}

		public final void setCnt(final double cnt) {
			this.cntProperty().set(cnt);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return sysCode.get() + " : " + vocStatusCode.get() + " : " + vocStatusNm.get() + " : " + week.get() + " : " + cnt.get();
		}

	}

}
