/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
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
@FXMLController(value = "BigTextView.fxml", isSelfController = true)
public class BigTextView extends BorderPane implements Closeable {
	private static Logger LOGGER = LoggerFactory.getLogger(BigTextView.class);

	private File file;
	private RandomAccessFile randomAccessFile;
	private boolean showButtons;
	//	private TextArea javaTextArea;

	//	private Map<String, String> pageBuffer;
	private static final int SEEK_SIZE = 1024 * 100;

	// 객체 생성시 초기만 세팅 readOnly
	private long TOTAL_SIZE = 0;
	// 객체 생성시 초기만 세팅 readOnly
	private int TOTAL_PAGE = 0;

	/**
	 * 버튼박스
	 */
	//	@FXML
	//	private HBox hboxButtons;
	@FXML
	private ListView<FindModel> lvFindRslt;
	//	@FXML
	//	private Button btnClose;

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
			//			pageBuffer = new HashMap<>();

			FxUtil.loadRoot(getClass(), this);

		} catch (Exception e) {
			if (handler == null) {
				LOGGER.error(ValueUtil.toString(e));
			} else {
				handler.handle(e);
			}
		}
	}

	/**
	 * 세팅했던 파일정보
	 *
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	private Map<Integer, SimpleTextView> pageCache = new HashMap<Integer, SimpleTextView>() {

		int size;
		static final int LIMITED_SIZE = 150;

		@Override
		public SimpleTextView put(Integer key, SimpleTextView value) {

			SimpleTextView v = super.put(key, value);

			if (size > LIMITED_SIZE) {
				Optional<Integer> findFirst = this.keySet().stream().limit(1).findFirst();
				findFirst.ifPresent(param -> {
					this.remove(param);
				});
				size--;
			} else {
				size++;
			}

			return v;
		}

	};
	private boolean isUsePageCache = true;

	/**
	 * 페이지 캐쉬를 사용할지 유무 디폴트값은 true
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 2.
	 * @param isUsePageCache
	 */
	public void usePageCache(boolean isUsePageCache) {
		this.isUsePageCache = isUsePageCache;

		if (!isUsePageCache)
			pageCache.clear();

	}

	private Pagination pagination;

	public Pagination getPaginationView() {
		return this.pagination;
	}

	@FXML
	public void initialize() {
		//		javaTextArea = new TextArea();
		//		javaTextArea.setPrefSize(TextArea.USE_COMPUTED_SIZE, Double.MAX_VALUE);
		//		hboxButtons.setVisible(showButtons);

		pagination = new Pagination(TOTAL_PAGE) {

			@Override
			protected Skin<?> createDefaultSkin() {
				return new CPagenationSkin(this) {

				};
			}
			//
		};

		pagination.setCache(true);
		pagination.setPageFactory(new Callback<Integer, Node>() {

			@Override
			public Node call(Integer param) {

				if (isUsePageCache && pageCache.containsValue(param)) {
					return pageCache.get(param);
				}

				String readContent = readPage(param);
				SimpleTextView simpleTextView = new SimpleTextView(readContent, false);
				simpleTextView.setPrefSize(TextArea.USE_COMPUTED_SIZE, Double.MAX_VALUE);

				if (isUsePageCache)
					pageCache.put(param, simpleTextView);

				return simpleTextView;
			}
		});
		pagination.setPrefSize(Pagination.USE_COMPUTED_SIZE, Pagination.USE_COMPUTED_SIZE);
		this.setCenter(pagination);

		lvFindRslt.setCellFactory(TextFieldListCell.forListView(new StringConverter<FindModel>() {

			@Override
			public String toString(FindModel object) {
				if (object == null)
					return "";
				return object.getPrintText();
			}

			@Override
			public FindModel fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}
		}));

		lvFindRslt.addEventHandler(MouseEvent.MOUSE_CLICKED, this::lvFindRsltOnMouseClick);
	}

	public void lvFindRsltOnMouseClick(MouseEvent e) {
		FindModel selectedItem = lvFindRslt.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.pagination.setCurrentPageIndex(selectedItem.getPage());
		}
	}

	/**
	 * 현재 페이지 인덱스를 리턴함.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 2.
	 * @return
	 */
	public int getCurrentPageIndex() {
		return pagination.getCurrentPageIndex();
	}

	/**
	 * 현재 페이지 뷰를 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 2.
	 * @return
	 */
	public SimpleTextView getCurrentPageView() {
		int currentPageIndex = getCurrentPageIndex();
		return getPageView(currentPageIndex);
	}

	public SimpleTextView getPageView(Integer index) {
		if (isUsePageCache) {

			if (pageCache.containsValue(index))
				return pageCache.get(index);
		}

		return (SimpleTextView) pagination.getPageFactory().call(index);
	}

	public String readPage(int page) {
		try {
			//			String currentPage = String.valueOf(page);
			//			if (pageBuffer.containsKey(currentPage)) {
			//				return pageBuffer.get(currentPage);
			//			}

			LOGGER.debug(String.format("Load file ... page %d ", page));
			int iwantReadPage = page;
			byte[] data = new byte[SEEK_SIZE];

			randomAccessFile.seek(iwantReadPage * SEEK_SIZE);
			randomAccessFile.read(data);
			/* pointer : %02d str : */
			/* randomAccessFile.getFilePointer(), */
			//			pageBuffer.put(currentPage, String.format("%s \n", new String(data).trim()));
			return new String(data);//pageBuffer.get(currentPage);
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

	@FXML
	private TextField txtMovePage;

	@FXML
	public void txtMovePageOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			if (e.isConsumed())
				return;

			String text = txtMovePage.getText();
			int nextPage = Integer.parseInt(text);

			pagination.setCurrentPageIndex(nextPage - 1);
			e.consume();
		}
	}

	@FXML
	private TextField txtSrch;

	@FXML
	public void btnSrchOnAction() {
		String srch = txtSrch.getText();
		lvFindRslt.getItems().clear();
		//		Stream.
		ExecutorService newFixedThreadExecutor = ExecutorDemons.newFixedThreadExecutor(3);

		new Finder(newFixedThreadExecutor, srch).run();
	}

	interface FireNext {
		void nextJob();
	}

	class FindModel {
		int page;
		int[] lines;
		String printText;

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public int[] getLines() {
			return lines;
		}

		public void setLines(int[] lines) {
			this.lines = lines;
		}

		public String getPrintText() {
			return printText;
		}

		public void setPrintText(String printText) {
			this.printText = printText;
		}

	}

	class Finder implements Runnable {

		private ExecutorService executor;
		private String keyword;

		public Finder(ExecutorService executor, String keyword) {
			this.executor = executor;
			this.keyword = keyword;
		}

		@Override
		public void run() {
			executor.execute(new Extract(1));
		}

		class Extract implements Runnable, FireNext {

			int page;
			byte[] data;

			Extract(int page) {
				this.page = page;
				data = new byte[SEEK_SIZE];
			}

			@Override
			public void run() {

				try {
					randomAccessFile.seek(this.page * SEEK_SIZE);
					randomAccessFile.read(data);

					LOGGER.debug("page : {} ", page);

					nextJob();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			public boolean canNext() {

				return (this.page + 1) < TOTAL_PAGE;

			}

			public void nextJob() {
				if (!(executor.isShutdown() || executor.isTerminated())) {

					executor.execute(new Transform(this.page, this.data));

					if (this.canNext()) {
						executor.execute(new Extract(this.page + 1));
					}
				}

			}

		}

		class Transform implements Runnable, FireNext {
			int page;
			byte[] b;
			int[] lines;

			Transform(int page, byte[] b) {
				this.page = page;
				this.b = b;
			}

			@Override
			public void run() {

				String[] cont = new String(b).split(System.lineSeparator());

				lines = IntStream.iterate(0, a -> a + 1).limit(cont.length).map(idx -> {

					if (cont[idx].indexOf(keyword) != -1)
						return idx;
					return -1;
				}).filter(v -> v != -1).toArray();
				nextJob();
			}

			@Override
			public void nextJob() {
				if (!(executor.isShutdown() || executor.isTerminated())) {
					executor.execute(new Loder(this.page, this.lines));
				}

			}

		}

		class Loder implements Runnable {
			int page;
			int[] lines;

			public Loder(int page, int[] lines) {
				this.page = page;
				this.lines = lines;
			}

			@Override
			public void run() {

				IntStream.of(this.lines).mapToObj(a -> String.valueOf(a + 1)).reduce((a, b) -> a.concat(" ").concat(b)).ifPresent(v -> {
					String data = String.format("page : %d lines : { %s }", (this.page + 1), v);
					//					LOGGER.debug("page : {} , lines : {}", this.page, v);
					FindModel findModel = new FindModel();
					findModel.setPage(this.page);
					findModel.setLines(this.lines);
					findModel.setPrintText(data);
					Platform.runLater(() -> {
						lvFindRslt.getItems().add(findModel);
					});
				});

			}

		}

	}
}
