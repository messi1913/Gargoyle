/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.ArrayList;
import java.util.List;

import com.kyj.fx.voeditor.visual.component.chart.RangeSliderLineChartComposite;
import com.kyj.fx.voeditor.visual.component.chart.SliderLineChartComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class SliderLineChartExam extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SliderLineChartComposite<Person> loadRoot1 = null;
		RangeSliderLineChartComposite<Person> loadRoot2 = null;
		try {
			List<Person> arrayList = new ArrayList<>();
			for (int i = 0; i < 30; i++) {
				arrayList.add(new Person(i + "", i));
			}
			loadRoot1 = new SliderLineChartComposite<Person>() {

				@Override
				public Data<String, Number> converter(Person t) {
					return new Data<>(t.name, t.age);
				}

			};
			loadRoot1.getItems().addAll(arrayList);

			loadRoot2 = new RangeSliderLineChartComposite<Person>() {

				@Override
				public Data<String, Number> converter(Person t) {
					return new Data<>(t.name, t.age);
				}

			};
			loadRoot2.getItems().addAll(arrayList);

			primaryStage.setScene(new Scene(new SplitPane(loadRoot1, loadRoot2)));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		System.out.println(loadRoot1 == loadRoot2);

	}

	static class Person {
		String name;
		int age;

		public Person(String name, int age) {
			super();
			this.name = name;
			this.age = age;
		}

	}

}