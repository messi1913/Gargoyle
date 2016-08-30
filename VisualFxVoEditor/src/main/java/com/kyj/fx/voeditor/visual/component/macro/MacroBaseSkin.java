/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * @author KYJ
 *
 */
@SuppressWarnings("restriction")
public class MacroBaseSkin extends BehaviorSkinBase<MacroControl, BehaviorBase<MacroControl>> {

	private BorderPane rootLayout;
	private TextArea textArea;

	private BooleanProperty isStarted = new SimpleBooleanProperty();

	/**
	 * @param control
	 */
	public MacroBaseSkin(MacroControl control) {
		this(control, new BehaviorBase<>(control, Collections.<KeyBinding> emptyList()));
	}

	/**
	 * @param control
	 * @param behavior
	 */
	protected MacroBaseSkin(MacroControl control, BehaviorBase<MacroControl> behavior) {
		super(control, behavior);

		rootLayout = new BorderPane();
		TableView<Map<String, Object>> tbResult = new TableView<>();
		textArea = new TextArea();
		SplitPane splitPane = new SplitPane(textArea, tbResult);
		splitPane.setOrientation(Orientation.VERTICAL);
		rootLayout.setCenter(splitPane);

		Button btnStart = createStartButton();
		Button btnStop = createStopButton();
		btnStop.setDisable(true);
		btnStart.setOnAction(this::btnStartOnAction);
		btnStop.setOnAction(this::btnStopOnAction);

		Label label = new Label(" Wait Time (Mills) : ");
		NumberTextField numberTextField = new NumberTextField();

		HBox buttonBox = new HBox(5, label, numberTextField, btnStart, btnStop);
		buttonBox.setId("btn-box");
		buttonBox.setPadding(new Insets(5));
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		rootLayout.setBottom(buttonBox);
		getChildren().add(this.rootLayout);

		isStarted.addListener((oba, o, n) -> {
			if (n) {
				btnStart.setDisable(true);
				btnStop.setDisable(false);
			} else {
				btnStart.setDisable(false);
				btnStop.setDisable(true);
			}
		});

		isStarted.set(false);

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public Button createStartButton() {
		return new Button("Start");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public Button createStopButton() {
		return new Button("Stop");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param e
	 */
	public void btnStartOnAction(ActionEvent e) {
		List<Map<Object, Object>> execute = this.getSkinnable().start(textArea.getText());
		isStarted.set(true);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param e
	 */
	public void btnStopOnAction(ActionEvent e) {
		isStarted.set(false);
	}

	class NumberTextField extends TextField {

		NumberTextField() {
			this.textProperty().addListener((oba, o, n) -> {
				if (!isValid(n)) {
					setText(o);
				}
			});

		}

		private boolean isValid(final String value) {
			// 음수
			if (value == null || value.length() == 0 || value.equals("-") || value.equals(".")) {
				return true;
			}

			try {
				new Double(value);
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}

		//		@Override
		//		public void replaceText(int start, int end, String text) {
		//			if (isValid(text)) {
		//				super.replaceText(start, end, text);
		//			}
		//		}
		//
		//		@Override
		//		public void replaceSelection(String text) {
		//			if (validate(text)) {
		//				super.replaceSelection(text);
		//			}
		//		}
		//
		//		private boolean validate(String text) {
		//			return text.matches("[0-9.]*");
		//		}
	}
}
