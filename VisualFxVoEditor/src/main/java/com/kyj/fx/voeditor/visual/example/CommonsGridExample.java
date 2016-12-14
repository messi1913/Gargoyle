/********************************
 *	프로젝트 : FxTemplate
 *	패키지   : com.samsung.sds.sos.client.component.grid
 *	작성일   : 2015. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.grid.AnnotationOptions;
import com.kyj.fx.voeditor.visual.component.grid.CommboInfo;
import com.kyj.fx.voeditor.visual.component.grid.CrudBaseGridView;
import com.kyj.fx.voeditor.visual.component.grid.IOptions;

/**
 * @author KYJ
 *
 */
public class CommonsGridExample extends Application {

	/**
	 * @Date 2015. 10. 8.
	 * @param args
	 * @User KYJ
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.application.Application#start(javafx.stage.Stage) KYJ
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			primaryStage.setWidth(1100);
			primaryStage.setHeight(800);
			BorderPane root = new BorderPane();

			Person person1 = new Person();
			person1.setName("김영준");
			person1.setAge("28");
			person1.setAddress("광주 광역시 북구 운암동");
			person1.setMarrayYn("N");

			Person person2 = new Person();
			person2.setName("박종광");
			person2.setAge("33");
			person2.setAddress("경기도 용인시 수지구 상현동");
			person2.setPhoneNumber("123342342");
			person2.setMarrayYn("Y");

			Person person3 = new Person();
			person3.setName("홍정호");
			person3.setAge("33");
			person3.setAddress("경기도 용인시 수지구 상현동    ㅁㄴㅇㅁㄴㅇㅁㄴㅇ");
			person3.setPhoneNumber("23423423423");
			person3.setMarrayYn("N");

			// 컬럼명이 name인경우 이름으로 alias를 준다.
			AnnotationOptions<Person> options = new AnnotationOptions<Person>(Person.class) {

				@Override
				public String convert(String columnName) {
					String name = columnName;
					if ("name".equals(columnName)) {
						name = "이름";
					} else if ("address".equals(columnName)) {
						name = "주소";
					}
					return name;
				}

				@Override
				public boolean editable(String columnName) {
					if ("address".equals(columnName)) {
						return false;
					}

					return true;
				}

				@Override
				public CommboInfo<?> comboBox(String columnName) {
					if ("marrayYn".equals(columnName)) {
						CodeDVO y = new CodeDVO("Y", "Y");
						CodeDVO n = new CodeDVO("N", "N");
						return new CommboInfo<>(FXCollections.observableArrayList(y, n), "code", "codeNm");
					}
					return null;
				}

			};

			CrudBaseGridView<Person> grid = new CrudBaseGridView<Person>(Person.class, /*
																						 * 사용하지고
																						 * 하는
																						 * 데이터셋
																						 * 객체
																						 */
			FXCollections.emptyObservableList() /* 만약 객체생성시 데이터까지 넣고싶다면 사용 */
			/* Arrays.asList("address", "name"), *//*
													 * 컬럼정렬순서 혹은 사용하고자하는 특정컬럼만
													 * 사용하고자하는경우
													 */
			, options /* 컬럼명을 alias로 지정 */
			);

			// 저장버튼을 클릭한경우 처리할 콜백 등록
			grid.setSaveClickCallback(list -> {
				list.forEach(vo -> System.out.println("status : [" + vo.get_status() + "]  item : " + vo));
			});

			// 그리드에 데이터 바인드
			ObservableList<Person> asList = FXCollections.observableArrayList(Arrays.asList(person1, person2, person3));
			grid.addItems(asList);

			root.setCenter(grid);

			Scene scene = new Scene(root, 400, 400);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
