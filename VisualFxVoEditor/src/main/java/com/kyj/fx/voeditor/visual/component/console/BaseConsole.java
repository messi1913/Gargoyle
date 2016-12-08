/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 2. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.console;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.popup.TextSearchComposite;
import com.kyj.fx.voeditor.visual.momory.SkinManager;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 *
 * 콘솔에 대한 기능적인 구현 및 디자인 만 처리
 *
 *
 * ConsoleManager를 통해 특화된 이벤트에 대한 처리.
 *
 * @author KYJ
 *
 */
class BaseConsole extends BorderPane implements Closeable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseConsole.class);

	private static final String STYLE_CLASS_NAME = "console-skin";
	/**
	 * TextArea의 기본적인 기능을 사용하는게 아닌 특화된 기능적 처리를 지원
	 */
	private ConsoleManager manager;

	private TextArea textArea;

	private StringBuffer buf = new StringBuffer();
	private HBox buttonHbox;
	private Button btnClear;
	private BooleanProperty closeRequest = new SimpleBooleanProperty(false);
//	private ExecutorService newSingleThreadExecutor = ExecutorDemons.newFixedThreadExecutor("CONSOLE-SERVICE", 1);

//	AtomicBoolean nextJobRequest = new AtomicBoolean(true);

	public void init() {

//		Thread thread = new Thread("CONSOLE-SERVICE-HELPER") {
//
//			@Override
//			public void run() {
//
//				while (!closeRequest.get()) {
//
//					if (nextJobRequest.get()) {
//
//						if (buf.length() == 0) {
//
//							try {
//								Thread.sleep(500L);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//							continue;
//						}
//						Service<String> createFxService = createService();
//						createFxService.start();
//					} else {
//						try {
//							Thread.sleep(500L);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//
//				}
//
//			}
//
//		};
//		thread.setDaemon(true);
//		thread.setPriority(3);
//		thread.start();
	}

//	private Service<String> createService() {
//		Service<String> createFxService = FxUtil.createFxService(new Task<String>() {
//
//			@Override
//			protected String call() throws Exception {
//				nextJobRequest.set(false);
//
//				synchronized (buf) {
//					String string = buf.toString();
//					buf.setLength(0);
//					updateMessage(string);
//				}
//
//				return null;
//			}
//		});
//
//		createFxService.setOnSucceeded(w -> {
//			String message = w.getSource().getMessage();
//			textArea.appendText(message);
//			nextJobRequest.set(true);
//		});
//
//		createFxService.setExecutor(newSingleThreadExecutor);
//		return createFxService;
//	}

	public BaseConsole() {

		btnClear = new Button("Clear");
		btnClear.getStyleClass().add("button-gargoyle");
		btnClear.setOnAction(this::btnClearOnAction);
		buttonHbox = new HBox(5, btnClear);
		buttonHbox.getStyleClass().add("hbox-gargoyle");
		buttonHbox.setAlignment(Pos.CENTER_RIGHT);
		buttonHbox.setPadding(new Insets(5, 5, 5, 5));
		setTop(buttonHbox);

		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.addEventHandler(KeyEvent.KEY_PRESSED, this::textAreaOnKeyEvent);
		textArea.getStyleClass().add(STYLE_CLASS_NAME);

		textArea.setCache(false);

		manager = new ConsoleManager(this);
		setCenter(textArea);
		// this.getStyleClass().add(STYLE_CLASS_NAME);
		this.getStylesheets().clear();

		this.getStylesheets().add(getClass().getResource("Console.css").toExternalForm());
		this.getStylesheets().add(SkinManager.getInstance().getSkin());
		this.getStylesheets().add(SkinManager.getInstance().getButtonSkin());
		init();

	}

	public synchronized void setConsoleManager(ConsoleManager manager) {
		this.manager = manager;
	}

	public ConsoleManager getConsoleManager() {
		return this.manager;
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * 콘솔출력부에서 키 클릭 이벤트
	 *
	 * @param e
	 ********************************/
	public void textAreaOnKeyEvent(KeyEvent e) {

		// ctrl + F
		if (e.isControlDown() && e.getCode() == KeyCode.F && !e.isShiftDown() && !e.isAltDown()) {

			TextSearchComposite textSearchView = new TextSearchComposite(this, textArea.textProperty());
			textSearchView.setOnSearchResultListener((vo) -> {

				switch (vo.getSearchType()) {
				case SEARCH_SIMPLE: {
					int startIndex = vo.getStartIndex();
					int endIndex = vo.getEndIndex();
					textArea.selectRange(startIndex, endIndex);
					break;
				}
				default:
					break;
				}

			});

			textSearchView.isSelectScopePropertyProperty().addListener((oba, oldval, newval) -> {
				// if (newval)
				// LOGGER.debug("User Select Locale Scope..");
				// else
				// LOGGER.debug("User Select Gloval Scope..");
			});

			textArea.setOnMouseClicked(event -> {
				IndexRange selection = textArea.getSelection();
				int start = selection.getStart();
				textSearchView.setSlidingStartIndexProperty(start);
			});

			textSearchView.show();

			e.consume();
		}

	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * clear클릭 이벤트
	 *
	 * @param e
	 ********************************/
	public void btnClearOnAction(ActionEvent e) {
		synchronized (buf) {
			this.textArea.clear();
		}
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 29.
	 * @param text
	 */
	public void appendText(char text) {
//		synchronized (buf) {
//			buf.append(manager.appendText(String.valueOf(text), false));
//		}
			Platform.runLater(()->{
				textArea.appendText(manager.appendText(String.valueOf(text), false));

			});
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 *
	 * @param text
	 ********************************/
	public void appendText(String text) {
//		synchronized (buf) {
//			buf.append(manager.appendText(text, true));
//		}

		//		textArea.appendText(manager.appendText(text));

			Platform.runLater(()->{
				textArea.appendText(manager.appendText(text, true));

			});
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 *
	 * @param text
	 * @param appendLine
	 ********************************/
	public void appendText(String text, boolean appendLine) {
//		synchronized (buf) {
			buf.append(manager.appendText(text, appendLine));
//		}
		Platform.runLater(()->{
			textArea.appendText(manager.appendText(text, appendLine));

		});
	}

	/********************************
	 * 작성일 : 2016. 6. 23. 작성자 : KYJ
	 *
	 * 수정가능여부
	 *
	 * @param editable
	 ********************************/
	public void setEditable(boolean editable) {
		textArea.setEditable(editable);
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		closeRequest.set(true);
	}

}
