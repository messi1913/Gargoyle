package com.kyj.javafx.scene.control.skin;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.AnnotateBizOptions;
import com.kyj.fx.voeditor.visual.component.grid.FixedBaseGridView;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CTableViewTest extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		ConfigResourceLoader.getInstance();

		// CTableView<Person> root = new CTableView<>();
		//
		// TableColumn<Person, String> colName = new TableColumn<>();
		// colName.setText("name");
		// root.getColumns().add(colName);
		// colName.setCellValueFactory(param -> param.getValue().nameProperty());
		//
		// Person e = new Person();
		// e.setName("kyj");
		// root.getItems().add(e);

		FixedBaseGridView<Person> view = new FixedBaseGridView<>(Person.class, new AnnotateBizOptions<Person>(Person.class) {

			@Override
			public boolean showRowNumber() {
				return false;
			}

		});

		view.setFixedColumnIndex(1);
//		view.getFixedColumns().add(view.getColumns().get(0));
//		view.getFixedColumns().add(view.getColumns().get(1));
		
		for(int i=0; i< 5; i++)
		{
			Person e = new Person();
			e.setName("kyj");
			e.setAddr("addr");
			e.setAge("age");
			e.setPhoneNumber("asdasdasdasdasdasdasdasdasdasdasdadasdadasd");
			e.setSkil1("zzxxcxczxcxczczxc");
			e.setSkil2("asdasdasdasdasdasdasdas");
			e.setSkil3("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
			view.getItems().add(e);
		}
		

		primaryStage.setScene(new Scene(view));
		primaryStage.show();
	}

	public static class Person extends AbstractDVO {
		StringProperty name;
		StringProperty age;
		StringProperty addr;
		StringProperty phoneNumber;
		StringProperty skil1;
		StringProperty skil2;
		StringProperty skil3;

		public Person() {

			name = new SimpleStringProperty();
			age = new SimpleStringProperty();
			addr = new SimpleStringProperty();
			phoneNumber = new SimpleStringProperty();
			skil1 = new SimpleStringProperty();
			skil2 = new SimpleStringProperty();
			skil3 = new SimpleStringProperty();
		}

		public final StringProperty nameProperty() {
			return this.name;
		}

		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

		public final StringProperty ageProperty() {
			return this.age;
		}

		public final String getAge() {
			return this.ageProperty().get();
		}

		public final void setAge(final String age) {
			this.ageProperty().set(age);
		}

		public final StringProperty addrProperty() {
			return this.addr;
		}

		public final String getAddr() {
			return this.addrProperty().get();
		}

		public final void setAddr(final String addr) {
			this.addrProperty().set(addr);
		}

		public final StringProperty phoneNumberProperty() {
			return this.phoneNumber;
		}

		public final String getPhoneNumber() {
			return this.phoneNumberProperty().get();
		}

		public final void setPhoneNumber(final String phoneNumber) {
			this.phoneNumberProperty().set(phoneNumber);
		}

		public final StringProperty skil1Property() {
			return this.skil1;
		}

		public final String getSkil1() {
			return this.skil1Property().get();
		}

		public final void setSkil1(final String skil1) {
			this.skil1Property().set(skil1);
		}

		public final StringProperty skil2Property() {
			return this.skil2;
		}

		public final String getSkil2() {
			return this.skil2Property().get();
		}

		public final void setSkil2(final String skil2) {
			this.skil2Property().set(skil2);
		}

		public final StringProperty skil3Property() {
			return this.skil3;
		}

		public final String getSkil3() {
			return this.skil3Property().get();
		}

		public final void setSkil3(final String skil3) {
			this.skil3Property().set(skil3);
		}

	}
}
