/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 1. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.kyj.fx.voeditor.visual.component.FxControlTreeView;
import com.kyj.fx.voeditor.visual.main.layout.SystemLayoutViewController;

import javafx.application.Application;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

/**
 * UI에 구성된 컨트롤 요소를을 분류
 *
 * @author KYJ
 *
 */
public class ComponentClassifier extends Application {

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		super.init();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(SystemLayoutViewController.class.getResource("VoEditorView.fxml"));
		try {
			Node node = loader.load();

			// {
			//
			// ObservableList<Node> recursive = recursive(node);
			// print(recursive);
			//
			// }
			// {
			// Group group = new Group();
			//
			// // Text는 Shape클래스의 하위클래스
			// group.getChildren().add(new Text());
			// group.getChildren().add(new Button());
			// group.getChildren().add(new Button());
			// group.getChildren().add(new Label());
			// ObservableList<Node> recursive = recursive(group);
			// print(recursive);
			//
			// }
			// {
			// ScrollPane scrollPane = new ScrollPane();
			// ScrollPane scrollPane2 = new ScrollPane();
			// scrollPane2.setContent(new TextArea());
			// scrollPane.setContent(new BorderPane(scrollPane2));
			// ObservableList<Node> recursive = recursive(scrollPane);
			// print(recursive);
			// }

			{
				BorderPane borderPane = new BorderPane();
				ScrollPane scrollPane2 = new ScrollPane();
				scrollPane2.setContent(new TextArea());
				borderPane.setTop(new HBox(new Button(), new Button(), new HTMLEditor()));
				borderPane.setCenter(new BorderPane(scrollPane2));

				FxControlTreeView tv = new FxControlTreeView(borderPane);
				tv.setOnMouseClicked(event -> {
					System.out.println(tv.getSelectionModel().getSelectedItem());
				});
				Scene scene = new Scene(tv);
				primaryStage.setScene(scene);
				primaryStage.show();

				// List<Node> recursive = recursive(borderPane);
				// print(recursive);

			}
			//
			// Scene scene = new Scene((Parent) node);
			// primaryStage.setScene(scene);
			// primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param node
	 * @return
	 */
	List<Node> recursive(Node node) {
		List<Node> controls = new ArrayList<>();
		if (node == null) {
			return controls;
		}

		if (node instanceof Control) {
			Control c = (Control) node;

			DefaultProperty annotation = c.getClass().getAnnotation(DefaultProperty.class);
			if (annotation != null) {
				String value = annotation.value();
				try {
					Object invoke = getMethod(c, value + "Property");
					if (ReadOnlyProperty.class.isAssignableFrom(invoke.getClass())) {
						ReadOnlyProperty prop = (ReadOnlyProperty) invoke;

						Node bean2 = (Node) prop.getBean();
						if (bean2 == null) {
							return controls;
						}
						System.out.println("컨트롤 : " + bean2);
						controls.add((Control) bean2);
					} else {
						System.out.println("컨트롤 : " + c);
						controls.add(c);
					}

				} catch (Exception e) {

					ObservableList<Node> childrenUnmodifiable = c.getChildrenUnmodifiable();

					ObservableList<Node> collect = childrenUnmodifiable.stream().flatMap(new Function<Node, Stream<Node>>() {
						@Override
						public Stream<Node> apply(Node t) {
							return recursive(t).stream();
						}
					}).collect(FXCollections::observableArrayList, (collections, item) -> collections.add(item),
							(collections, item) -> collections.addAll(item));
					controls.addAll(collect);

				}
			}
			/*
			 * if (node instanceof ScrollPane || node instanceof TabPane) { //
			 * scrollpane, TabPane은 컨트롤요소에 추가하지않음. // controls.add(c);
			 * ObservableList<Control> recursive = recursive(((ScrollPane)
			 * node).getContent()); controls.addAll(recursive); }
			 */
			else {
				controls.add(c);

				ObservableList<Node> childrenUnmodifiable = c.getChildrenUnmodifiable();

				ObservableList<Node> collect = childrenUnmodifiable.stream().flatMap(new Function<Node, Stream<Node>>() {

					@Override
					public Stream<Node> apply(Node t) {
						return recursive(t).stream();
					}
				}).collect(FXCollections::observableArrayList, (collections, item) -> collections.add(item),
						(collections, item) -> collections.addAll(item));
				controls.addAll(collect);

				/* 개선 */
				// childrenUnmodifiable.forEach(n -> {
				// ObservableList<Node> recursive = recursive(n);
				// controls.addAll(recursive);
				// });

			}

			return controls;
		} else if (node instanceof Parent) {

			Parent p = (Parent) node;

			// ObservableList<Node> collect =
			// childrenUnmodifiable.stream().map(n->this).collect(FXCollections::observableArrayList,
			// (collections, item) -> collections.add(item), (collections, item)
			// -> collections.addAll(item));
			// controls.addAll(collect);

			ObservableList<Node> childrenUnmodifiable = p.getChildrenUnmodifiable();
			// childrenUnmodifiable.forEach(n -> {
			// ObservableList<Node> recursive = recursive(n);
			// controls.addAll(recursive);
			// });

			ObservableList<Node> collect = childrenUnmodifiable.stream().flatMap(new Function<Node, Stream<Node>>() {
				@Override
				public Stream<Node> apply(Node t) {
					return recursive(t).stream();
				}
			}).collect(FXCollections::observableArrayList, (collections, item) -> collections.add(item),
					(collections, item) -> collections.addAll(item));
			controls.addAll(collect);

		} else {
			System.err.println("her.! : " + node);
		}

		return controls;
	}

	Object getMethod(Object o, String methodName) throws Exception {
		Method declaredMethod = o.getClass().getMethod(methodName);
		declaredMethod.setAccessible(true);
		return declaredMethod.invoke(o);
	}

	Object getDeclaredMethod(Object o, String methodName) throws Exception {
		Method declaredMethod = o.getClass().getDeclaredMethod(methodName);
		declaredMethod.setAccessible(true);
		return declaredMethod.invoke(o);
	}

	void print(List<Node> controls) {

		System.out.println("#### 컨트롤 개수 ####");
		System.out.println(controls.size());

		controls.forEach(n -> {
			if (n instanceof Control) {
				Skin<?> skin = ((Control) n).getSkin();
				System.out.println("skin : " + skin);
				n.getPseudoClassStates();
			}
			System.out.println(n);

		});
	}

}
