/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 10. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author KYJ
 *
 */
public class AutoCompletionTextBindingExam extends Application {

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		TextField textField = new TextField();
		List<String> asList = Arrays.asList("sssss", "aaaaa");
		AutoCompletionTextFieldBinding<String> autoCompletionTextFieldBinding = new AutoCompletionTextFieldBinding<>(textField,
				new Callback<ISuggestionRequest, Collection<String>>() {

					@Override
					public Collection<String> call(ISuggestionRequest param) {
						String userText = param.getUserText();
						return asList.stream().filter(v -> v.startsWith(userText)).collect(Collectors.toList());
					}
				} );

		autoCompletionTextFieldBinding.setVisibleRowCount(10);

		primaryStage.setScene(new Scene(new BorderPane(textField)));
		primaryStage.show();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 21.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
