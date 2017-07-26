/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.text;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.handler.ExceptionHandler;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
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
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
@FXMLController(value = "BigTextView.fxml", isSelfController = true, css = "BigTextView.css")
public class BigTextView extends BorderPane implements Closeable {
	private static Logger LOGGER = LoggerFactory.getLogger(BigTextView.class);

	/*대용량 파일*/
	private File file;
	private RandomAccessFile randomAccessFile;

	// private TextArea javaTextArea;

	// private Map<String, String> pageBuffer;
	private static final int SEEK_SIZE = 1024 * 512;

	// 객체 생성시 초기만 세팅 readOnly
	private long TOTAL_SIZE = 0;
	// 객체 생성시 초기만 세팅 readOnly
	private int TOTAL_PAGE = 0;

	/**
	 * 버튼박스
	 */
	// @FXML
	// private HBox hboxButtons;
	@FXML
	private ListView<FindModel> lvFindRslt;
	@FXML
	private FlowPane fpLines;

	@FXML
	private TextField txtMovePage;

	@FXML
	private TextField txtSrch;

	/**
	 * Thread-Pool
	 * @최초생성일 2017. 7. 25.
	 */
	private ExecutorService executor = ExecutorDemons.newFixedThreadExecutor("Gargoyle-Big-Text-Search-Thread", 3);

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

	/**
	 * @최초생성일 2017. 7. 25.
	 */
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
	 * 
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
		pagination = new Pagination(TOTAL_PAGE) {
			@Override
			protected Skin<?> createDefaultSkin() {
				return new CPagenationSkin(this) {
				};
			}
		};

		pagination.setCache(true);

		pagination.setPageFactory(new Callback<Integer, Node>() {

			@Override
			public Node call(Integer param) {

				//				if (isUsePageCache && pageCache.containsKey(param)) {
				//					SimpleTextView simpleTextView = pageCache.get(param);
				//					if (simpleTextView != null)
				//						return simpleTextView;
				//				}

				String readContent = readPage(param);
				SimpleTextView simpleTextView = new SimpleTextView(readContent, false);
				simpleTextView.setPrefSize(TextArea.USE_COMPUTED_SIZE, Double.MAX_VALUE);

				//				if (isUsePageCache)
				//					pageCache.put(param, simpleTextView);

				return simpleTextView;
			}
		});
		pagination.setPrefSize(Pagination.USE_COMPUTED_SIZE, Pagination.USE_COMPUTED_SIZE);
		this.setCenter(pagination);
		//
		lvFindRslt.setCellFactory(TextFieldListCell.forListView(new StringConverter<FindModel>() {

			@Override
			public String toString(FindModel object) {
				return String.format("%d   ( Count : %d )", object.getPage() + 1, object.lines.size());
			}

			@Override
			public FindModel fromString(String string) {
				return null;
			}
		}));

		lvFindRslt.setOnMouseClicked(this::lvFindRsltOnMouseClick);
	}

	/**
	 * 리스트뷰를 클릭했을때 이벤트
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 25. 
	 * @param e
	 */
	public void lvFindRsltOnMouseClick(MouseEvent e) {
		FindModel selectedItem = lvFindRslt.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.pagination.setCurrentPageIndex(selectedItem.getPage());

			List<Hyperlink> collect = IntStream.iterate(0, a -> a + 1).limit(selectedItem.getLines().size()).mapToObj(idx -> {
				Line line = selectedItem.getLines().get(idx);

				Hyperlink hyperlink = new Hyperlink(String.format("%d", line.getLine()));
				hyperlink.setOnAction(ev -> {
					CPagenationSkin skin = (CPagenationSkin) pagination.getSkin();
					SimpleTextView view = (SimpleTextView) skin.getCurrentPage();
					view.getHelper().moveToLine(line.getLine(), line.getStartCol() - 1, line.getEndCol() - 1);

				});
				return hyperlink;

			}).collect(Collectors.toCollection(LinkedList::new));

			this.fpLines.getChildren().setAll(collect);

		}
	}

	/**
	 * 현재 페이지 인덱스를 리턴함.
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 2.
	 * @return
	 */
	public int getCurrentPageIndex() {
		return pagination.getCurrentPageIndex();
	}

	/**
	 * 현재 페이지 뷰를 리턴.
	 * 
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

			LOGGER.debug(String.format("Load file ... page %d ", page));
			int iwantReadPage = page;
			byte[] data = new byte[SEEK_SIZE];

			randomAccessFile.seek(iwantReadPage * SEEK_SIZE);
			randomAccessFile.read(data);
			/* pointer : %02d str : */
			/* randomAccessFile.getFilePointer(), */
			// pageBuffer.put(currentPage, String.format("%s \n", new String(data).trim()));
			return new String(data);
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

		stopPool();

	}

	/**
	 * 동작 초기화
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 26. 
	 */
	private void stopPool() {
		if (!executor.isShutdown())
			executor.shutdownNow();

		if (!executor.isTerminated()) {
			try {
				executor.awaitTermination(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	public void txtMovePageOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			if (e.isConsumed())
				return;

			String text = txtMovePage.getText();

			try {
				int nextPage = Integer.parseInt(text);
				pagination.setCurrentPageIndex(nextPage - 1);
				e.consume();
			} catch (NumberFormatException ex) {
			}

		}
	}

	@FXML
	public void txtSrchOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {

			if (!btnSrch.isDisabled())
				btnSrchOnAction();
		}
	}

	@FXML
	private Button btnSrch;

	@FXML
	public void btnSrchOnAction() {
		String keyword = txtSrch.getText();
		
		// Stream.

		//17.7.25 keyword가 없는경우 검색하지않음.
		if (ValueUtil.isNotEmpty(keyword)) {
			lvFindRslt.getItems().clear();
			Finder finder = new Finder(keyword, item -> {

				Platform.runLater(() -> {
					lvFindRslt.getItems().add(item);
				});

			});

			btnSrch.setDisable(true);
			finder.run();
		}
		else
		{
			DialogUtil.showMessageDialog(FxUtil.getWindow(this), "검색어를 입력하십시요.");
			return;
		}
	}

	interface FireNext {
		void nextJob();
	}

	interface Option {

	}

	class FindModel implements Comparable<FindModel> {
		String keyword;
		int page;
		List<Line> lines = Collections.emptyList();
		String printText;

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public List<Line> getLines() {
			return lines;
		}

		public void setLines(List<Line> lines) {
			this.lines = lines;
		}

		public String getPrintText() {
			return printText;
		}

		public void setPrintText(String printText) {
			this.printText = printText;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		@Override
		public int compareTo(FindModel o) {
			return Integer.compare(this.page, o.page);
		}

	}

	class Line {
		private int line;
		private int startCol;
		private int endCol;

		public int getLine() {
			return line;
		}

		public void setLine(int line) {
			this.line = line;
		}

		public int getStartCol() {
			return startCol;
		}

		public void setStartCol(int startCol) {
			this.startCol = startCol;
		}

		public int getEndCol() {
			return endCol;
		}

		public void setEndCol(int endCol) {
			this.endCol = endCol;
		}

	}

	class Finder implements Runnable {

		private String keyword;
		//		private ObservableList<FindModel> models = FXCollections.observableArrayList();//FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		private Consumer<FindModel> action;
		private int keywordSize;

		public Finder(String keyword, Consumer<FindModel> action) {
			this.keyword = keyword;
			this.keywordSize = keyword.length();
			this.action = action;
		}

		//		public ObservableList<FindModel> getModels() {
		//			return models;
		//		}

		@Override
		public void run() {
			executor.execute(new Extract(0));
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

					executor.execute(new Transform(this.page /*조회용*/, this.data));

					if (this.canNext()) {
						executor.execute(new Extract(this.page + 1 /* extract next page */));
					}
				}

			}

		}

		class Transform implements Runnable, FireNext {
			int page;
			byte[] b;
			List<Line> lines;

			Transform(int page, byte[] b) {
				this.page = page;
				this.b = b;
			}

			@Override
			public void run() {

				LineNumberReader reader = new LineNumberReader(new InputStreamReader(new ByteArrayInputStream(b)));
				String cont = null;
				ArrayList<Line> arrayList = new ArrayList<>();
				try {

					while ((cont = reader.readLine()) != null) {

						int line = reader.getLineNumber();
						int col = 0;

						while (col != -1) {
							col = cont.indexOf(keyword, col);

							if (col == -1)
								break;

							Line l = new Line();
							l.setLine(line);
							l.setStartCol(col + 1);
							l.setEndCol(col + 1 + keywordSize);
							arrayList.add(l);
							col = col + keywordSize;

						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				lines = arrayList;

				//				LOGGER.debug("page : {} find count : {} ", (this.page + 1), this.lines.size());
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
			List<Line> lines;

			public Loder(int page, List<Line> lines) {
				this.page = page;
				this.lines = lines;
			}

			@Override
			public void run() {

				if (page >= TOTAL_PAGE - 1) {
					Platform.runLater(() -> {
						btnSrch.setDisable(false);
					});
				}
				if (this.lines == null || this.lines.size() == 0) {
					return;
				}

				FindModel findModel = new FindModel();
				findModel.setPage(this.page);
				findModel.setLines(this.lines);
				// findModel.setPrintText(data);
				findModel.setKeyword(keyword);

				action.accept(findModel);

			}

		}

	}
}
