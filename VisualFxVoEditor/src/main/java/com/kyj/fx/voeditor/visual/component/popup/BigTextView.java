/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.ExceptionHandler;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 *
 * 대용량 텍스트를 읽는 경우에 사용.
 *
 * 페이지 기반
 *
 * 스레드에 안전하지않으므로 데이터 공유는 금지
 *
 *
 * [주의] 그리고 해당 컨텐츠를 사용시 반드시 리소스를 해제하는 코드를 작성해야함. randomAccessFile.close호출할것
 *
 * @author KYJ
 *
 */
public class BigTextView extends BorderPane implements Closeable {
	private static Logger LOGGER = LoggerFactory.getLogger(BigTextView.class);

	private File file;
	private RandomAccessFile randomAccessFile;
	private boolean showButtons;
	private TextArea javaTextArea;

	private Map<String, String> pageBuffer;
	private static final int SEEK_SIZE = 1024 * 100;

	// 객체 생성시 초기만 세팅 readOnly
	private long TOTAL_SIZE = 0;
	// 객체 생성시 초기만 세팅 readOnly
	private int TOTAL_PAGE = 0;

	/**
	 * 버튼박스
	 */
	@FXML
	private HBox hboxButtons;

	@FXML
	private Button btnClose;

	public BigTextView(File file) {
		this(file, true);
	}

	public BigTextView(File file, boolean showButtons) {
		this(file, showButtons, null);
	}

	public BigTextView(File file, boolean showButtons, ExceptionHandler handler) {
		try {
			this.file = file;
			TOTAL_SIZE = file.length();
			TOTAL_PAGE = (int) (TOTAL_SIZE / SEEK_SIZE) + 1;

			randomAccessFile = new RandomAccessFile(file, "r");
			TOTAL_SIZE = randomAccessFile.length();
			this.showButtons = showButtons;
			pageBuffer = new HashMap<>();

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(BigTextView.class.getResource("SimpleTextView.fxml"));
			loader.setRoot(this);
			loader.setController(this);
			loader.load();
		} catch (Exception e) {
			if (handler == null) {
				LOGGER.error(ValueUtil.toString(e));
			} else {
				handler.handle(e);
			}
		}
	}

	public void setEditable(boolean editable) {
		javaTextArea.setEditable(false);
	}

	/**
	 * 세팅했던 파일정보
	 *
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	public void show() throws IOException {
		Stage stage = new Stage();
		Scene scene = new Scene(this, 1100, 700);
		stage.setScene(scene);
		stage.setAlwaysOnTop(true);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(SharedMemory.getPrimaryStage());
		btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.close();
			}
		});
		stage.showAndWait();
		stage.close();

	}

	@FXML
	public void initialize() {
		javaTextArea = new TextArea();
		hboxButtons.setVisible(showButtons);

		Pagination pagination = new Pagination(TOTAL_PAGE);
		pagination.setPageFactory(new Callback<Integer, Node>() {

			@Override
			public Node call(Integer param) {
				String readContent = readPage(param);
				return new SimpleTextView(readContent, false);
			}
		});
		this.setCenter(pagination);
	}

	public String readPage(int page) {
		try {
			String currentPage = String.valueOf(page);
			if (pageBuffer.containsKey(currentPage)) {
				return pageBuffer.get(currentPage);
			}

			LOGGER.debug(String.format("Load file ... page %d ", page));
			int iwantReadPage = page;
			byte[] data = new byte[SEEK_SIZE];

			randomAccessFile.seek(iwantReadPage * SEEK_SIZE);
			randomAccessFile.read(data);
			/* pointer : %02d str : */
			/* randomAccessFile.getFilePointer(), */
			pageBuffer.put(currentPage, String.format("%s \n", new String(data).trim()));
			return pageBuffer.get(currentPage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void close() throws IOException {
		if (randomAccessFile != null)
			randomAccessFile.close();
	}

}
