/**
 *
 */
package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

import com.kyj.fx.voeditor.visual.momory.SharedMemory;

/**
 * @author KYJ
 *
 */
public class DialogUtil {

	private DialogUtil() {
	}

	public static final String MEMO_LOCAL_USER = "memo.local.user";

	/**
	 * 파일다이얼로그 오픈
	 *
	 * @Date 2015. 10. 12.
	 * @param ownerWindow
	 * @param option
	 * @return
	 * @User KYJ
	 */
	public static File showFileDialog(final Window ownerWindow, Consumer<FileChooser> option) {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Open Resource File");

		installDefaultPath(fileChooser);
		option.accept(fileChooser);

		File file = fileChooser.showOpenDialog(ownerWindow);
		applyLastPath(file);
		return file;
	}

	public static File showFileDialog(final Window ownerWindow) {
		return showFileDialog(ownerWindow, option -> {
		});
	}

	/**
	 * properties 파일 선택 다이얼로그
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 21.
	 * @param ownerWindow
	 * @return
	 */
	public static File showPropertyFileSelectDialog(final Window ownerWindow) {
		return showFileDialog(ownerWindow, chooser -> {
			chooser.getExtensionFilters()
					.add(new ExtensionFilter(GargoyleExtensionFilters.PROPERTIES_NAME, GargoyleExtensionFilters.PROPERTIES));
		});
	}

	/**
	 * Css 파일 선택 다이얼로그
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 21.
	 * @param ownerWindow
	 * @return
	 */
	public static File showCssFileSelectDialog(final Window ownerWindow) {
		return showFileDialog(ownerWindow, chooser -> {
			chooser.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.FX_CSS_NAME, GargoyleExtensionFilters.FX_CSS));
		});
	}

	/********************************
	 * 작성일 : 2016. 6. 19. 작성자 : KYJ
	 *
	 *
	 * 최근 설정했는 경로로 세팅.
	 *
	 *
	 * @param fileChooser
	 ********************************/
	private static void installDefaultPath(FileChooser fileChooser) {
		String path = PreferencesUtil.getDefault().get(PreferencesUtil.KEY_LAST_SELECTED_PATH, "");
		if (ValueUtil.isNotEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {

				if (file.isFile()) {
					fileChooser.setInitialDirectory(file.getParentFile());
				} else if (file.isDirectory()) {
					fileChooser.setInitialDirectory(file);
				}
			}
		}
	}

	private static void applyLastPath(File file) {
		if (file != null && file.exists())
			PreferencesUtil.getDefault().put(PreferencesUtil.KEY_LAST_SELECTED_PATH, file.getAbsolutePath());
	}

	public static File showFileSaveDialog(final Window ownerWindow, Consumer<FileChooser> option) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");

		installDefaultPath(fileChooser);

		option.accept(fileChooser);

		File result = fileChooser.showSaveDialog(ownerWindow);
		if (result != null)
			applyLastPath(result.getParentFile());

		return result;
	}

	/**
	 * 파일저장다이얼로그,
	 *
	 * showFileSaveDialog와 달리 파일존재여부도 함께 체크해준다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 2. 5.
	 * @param ownerWindow
	 * @param option
	 * @return
	 */
	public static File showFileSaveCheckDialog(final Window ownerWindow, Consumer<FileChooser> option) {
		File saveFile = showFileSaveDialog(ownerWindow, option);
		return saveFile;
	}

	/**
	 *
	 *
	 * 디렉토리 선택 다이얼로그 오픈
	 *
	 * @Date 2015. 10. 12.
	 * @param ownerWindow
	 * @param option
	 * @return
	 * @User KYJ
	 */
	public static File showDirectoryDialog(final Window ownerWindow, Consumer<DirectoryChooser> option) {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Open Resource Directory");
		if (option != null)
			option.accept(chooser);
		return chooser.showDialog(ownerWindow);
	}

	public static File showDirectoryDialog(final Window ownerWindow) {
		return showDirectoryDialog(ownerWindow, null);
	}

	/**
	 * Exception Dialog 예외가 발생햇을때 표시할 다이얼로그
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 22.
	 * @param owner
	 * @param ex
	 */
	public static void showExceptionDailog(Node owner, Throwable ex) {
		showExceptionDailog(getWindow(owner), ex, "The exception stacktrace was:");
	}

	public static void showExceptionDailog(Throwable ex) {
		showExceptionDailog((Window) null, ex, "The exception stacktrace was:");
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 22.
	 * @param e
	 * @param string
	 */
	public static void showExceptionDailog(Exception ex, String string) {
		showExceptionDailog((Window) null, ex, "The exception stacktrace was:");
	}

	private static Window getWindow(Node owner) {
		Window _owner = null;
		if (owner != null) {
			Scene scene = owner.getScene();
			if (scene != null) {
				_owner = scene.getWindow();
			}
		}

		if (_owner == null) {
			_owner = SharedMemory.getPrimaryStage();
		}
		return _owner;
	}

	/**
	 * Exception Dialog 예외가 발생햇을때 표시할 다이얼로그
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 22.
	 * @param owner
	 * @param ex
	 */
	public static void showExceptionDailog(Window owner, Throwable ex) {
		showExceptionDailog(owner, ex, "The exception stacktrace was:");
	}

	/**
	 * Exception Dialog 예외가 발생햇을때 표시할 다이얼로그
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 22.
	 * @param owner
	 * @param ex
	 * @param message
	 */
	public static void showExceptionDailog(Node owner, Throwable ex, String message) {
		showExceptionDailog(getWindow(owner), ex, message);
	}

	/**
	 * Exception Dialog 예외가 발생햇을때 표시할 다이얼로그
	 *
	 * @param ex
	 */
	public static void showExceptionDailog(Window owner, Throwable ex, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");

		alert.setHeaderText(message);
		alert.setContentText(ex.getMessage());

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label(message);

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.initOwner(owner);
		alert.showAndWait();
	}

	/**
	 * login Dialog 로그인 처리 다이얼로그
	 *
	 * @param consumer
	 * @return
	 */
	public static <T> Optional<Pair<String, String>> showLoginDialog(Consumer<? super Pair<String, String>> consumer) {

		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		dialog.setHeaderText("Look, a Custom Login Dialog");
		dialog.setGraphic(new ImageView(new Image("file:resources/images/login.png")));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		ButtonType localButtonType = new ButtonType("Local", ButtonData.APPLY);

		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, localButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		// Enable/Disable login button depending on whether a username was
		// entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> username.requestFocus());

		// Convert the result to a username-password-pair when the login button
		// is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			} else if (dialogButton == localButtonType) {
				return new Pair<>(MEMO_LOCAL_USER, "");
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(consumer);

		return result;
	}

	/**
	 * confirm Dialog
	 *
	 * confirm 다이얼로그
	 *
	 * @return
	 */
	public static ButtonType showConfirmDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Look, a Confirmation Dialog");
		alert.setContentText("Are you ok with this?");

		Optional<ButtonType> result = alert.showAndWait();
		/*
		 * if (result.get() == ButtonType.OK) { // ... user chose OK } else { //
		 * ... user chose CANCEL or closed the dialog }
		 */
		return result.get();
	}

	/**
	 * info Dialog 메세지 다이얼로그
	 *
	 * @param message
	 */
	public static void showMessageDialog(String message) {
		showMessageDialog(SharedMemory.getPrimaryStage(), message);
	}

	/**
	 * show info Dialog info Dialog
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param initOwner
	 * @param message
	 */
	public static void showMessageDialog(Stage initOwner, String message) {
		showMessageDialog(initOwner, "Info", "", message, alert -> {
			alert.initModality(Modality.APPLICATION_MODAL);
			alert.initOwner(initOwner);
			alert.showAndWait();
		});

	}

	/**
	 * show info Dialog
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 12.
	 * @param initOwner
	 * @param title
	 * @param headerText
	 * @param message
	 * @param apply
	 */
	public static void showMessageDialog(Stage initOwner, String title, String headerText, String message, Consumer<Alert> apply) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.setContentText(message);
		apply.accept(alert);
	}

	public static Optional<Pair<String, String>> showYesOrNoDialog(String title, String message) {
		return showYesOrNoDialog(title, message, str -> {
		});
	}

	public static Optional<Pair<String, String>> showYesOrNoDialog(String title, String message,
			Consumer<? super Pair<String, String>> consumer) {

		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(message);

		// Set the button types.
		ButtonType yesBtn = new ButtonType("Yes", ButtonData.YES);
		ButtonType noBtn = new ButtonType("No", ButtonData.NO);

		dialog.getDialogPane().getButtonTypes().addAll(yesBtn, noBtn);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == yesBtn) {
				return new Pair<>("RESULT", "Y");
			} else if (dialogButton == noBtn) {
				return new Pair<>("RESULT", "N");
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(consumer);

		return result;

	}

	public static Optional<Pair<String, String>> showInputDialog(String title, String message) {
		return showInputDialog(title, message, str -> {
		});
	}

	public static Optional<Pair<String, String>> showInputDialog(String title, String message,
			Consumer<? super Pair<String, String>> consumer) {

		// Create the custom dialog.
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(message);
		TextField graphic = new TextField();
		dialog.setGraphic(graphic);

		// Set the button types.
		ButtonType okBtn = new ButtonType("OK", ButtonData.OK_DONE);

		dialog.getDialogPane().getButtonTypes().add(okBtn);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okBtn) {
				return new Pair<>("OK", graphic.getText());
			} else {
				return null;
			}
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(consumer);

		return result;

	}

	/**
	 * 특정 location에 위치한 fxml파일을 팝업형태로 로드한다.
	 *
	 * @Date 2015. 10. 15.
	 * @param packageLocation
	 * @param fxmlName
	 * @param width
	 * @param height
	 * @param modal
	 * @throws IOException
	 * @User KYJ
	 */
	public static void open(Class<?> packageLocation, String fxmlName, int width, int height, boolean modal) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(packageLocation.getResource(fxmlName));
		Parent parent = loader.load();

		BorderPane borderPane = new BorderPane(parent);
		Stage stage = new Stage();
		Scene scene = new Scene(borderPane, width, height);
		stage.setScene(scene);
		if (modal)
			stage.initModality(Modality.WINDOW_MODAL);
		stage.setAlwaysOnTop(true);
		stage.initOwner(SharedMemory.getPrimaryStage());
		stage.show();

		// stage.setScene(borderPane, 800, 500);

	}

}
