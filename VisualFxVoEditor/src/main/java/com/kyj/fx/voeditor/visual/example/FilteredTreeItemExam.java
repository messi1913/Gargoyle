/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.concurrent.Callable;
import java.util.function.Predicate;

import com.kyj.fx.voeditor.visual.component.tree.FilterableTreeItem;
import com.kyj.fx.voeditor.visual.component.tree.TreeItemPredicate;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class FilteredTreeItemExam extends Application {

	static class Person {
		String name;

	}

	private final BooleanProperty hide = new SimpleBooleanProperty();

	@Override
	public void start(Stage primaryStage) throws Exception {

		FilterableTreeItem<String> filterableTreeItem = new FilterableTreeItem<>("");
		filterableTreeItem.setExpanded(true);
		TreeView<String> treeView = new TreeView<>(filterableTreeItem);
		treeView.setShowRoot(false);
		treeView.setRoot(filterableTreeItem);

		BorderPane borderPane = new BorderPane(treeView);
		TextField value = new TextField();
		value.textProperty().addListener((oba, oldval, newval) -> {
			Callable<TreeItemPredicate<String>> func = () -> {
				Predicate<String> predicate = str -> str.indexOf(newval) >= 0;
				return TreeItemPredicate.<String> create(predicate);
			};
			ObjectBinding<TreeItemPredicate<String>> createObjectBinding = Bindings.createObjectBinding(func, hide);
			filterableTreeItem.predicateProperty().bind(createObjectBinding);
		});

		borderPane.setTop(value);
		Scene scene = new Scene(borderPane);

		primaryStage.setScene(scene);

		primaryStage.show();

		FilterableTreeItem<String> e = new FilterableTreeItem<>("ABC");
//		e.getChildren().add(new FilterableTreeItem<>("DEF"));
//		e.getChildren().add(new FilterableTreeItem<>("김aa"));
//		e.getChildren().add(new FilterableTreeItem<>("김bb"));
//		e.getChildren().add(new FilterableTreeItem<>("김cc"));
//		filterableTreeItem.getChildren().add(e);
//		filterableTreeItem.getChildren().add(new FilterableTreeItem<>("DEF"));
//		filterableTreeItem.getChildren().add(new FilterableTreeItem<>("김DD"));
//
		e.getSourceChildren().add(new FilterableTreeItem<>("DEF"));
		e.getSourceChildren().add(new FilterableTreeItem<>("김aa"));
		e.getSourceChildren().add(new FilterableTreeItem<>("김bb"));
		e.getSourceChildren().add(new FilterableTreeItem<>("김cc"));
		filterableTreeItem.getSourceChildren().add(e);
		filterableTreeItem.getSourceChildren().add(new FilterableTreeItem<>("DEF"));
		filterableTreeItem.getSourceChildren().add(new FilterableTreeItem<>("김DD"));

		// filterableTreeItem.predicateProperty()
		// .bind(Bindings.createObjectBinding(() ->
		// TreeItemPredicate<String>.create(str -> str.indexOf("김") >= 0),
		// hide));

	}

	public static void main(String[] args) {
		launch(args);
	}
	/***********************************************************************************/
	/* 이벤트 구현 */

	// 

	/***********************************************************************************/

	/***********************************************************************************/
	/* 일반API 구현 */

	//
	/***********************************************************************************/
}
