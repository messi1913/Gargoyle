/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 12. 14.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 텍스트기반 검색
 *
 * NotePad +프로그램을 베이스로 잡고 작성.
 *
 * @author KYJ
 *
 */
public class TextSearchComposite extends BorderPane {
	private static Logger LOGGER = LoggerFactory.getLogger(TextSearchComposite.class);

	private FXMLLoader loader;
	/**
	 * 본문 컨텐츠
	 *
	 * @최초생성일 2015. 12. 14.
	 */
	private StringProperty contentProperty;

	/**
	 * 검색 시작 인덱스
	 *
	 * @최초생성일 2015. 12. 14.
	 */
	private IntegerProperty slidingStartIndexProperty;

	/**
	 * 찾기탭에서 찾을 내용 텍스트
	 *
	 * @최초생성일 2015. 12. 14.
	 */
	@FXML
	private TextField txtFindTextContent;

	@FXML
	private TabPane tabPane;

	/**
	 * 찾은 내용에 대한 메타정보
	 *
	 * @최초생성일 2015. 12. 14.
	 */
	private ObjectProperty<SearchResultVO> searchResultVOProperty;

	private Parent parent;

	/**
	 * 범위선택 라이오 박스를 선택했는지 유무
	 */
	private BooleanProperty isSelectScopeProperty = new SimpleBooleanProperty();

	@FXML
	private RadioButton rdoSelectScope;

	@FXML
	private RadioButton rdoGlobalScope;

	/**
	 * description text field
	 *
	 * @최초생성일 2015. 12. 28.
	 */
	@FXML
	private Label txtDesc;

	/**
	 * 바꾸기에서 찾기 텍스트
	 *
	 * @최초생성일 2015. 12. 28.
	 */
	@FXML
	private TextField txtFind;
	/**
	 * 바꾸기 텍스트
	 *
	 * @최초생성일 2015. 12. 28.
	 */
	@FXML
	private TextField txtReplace;

	/****************************************************************/

	/****************************************************************/
	/*
	 * 사용자가 필수적으로 처리해야할 이벤트
	 */
	/**
	 * 검색결과를 발견했을때 처리할 내용을 등록한다.
	 *
	 * @param consumer
	 */
	public void setOnSearchResultListener(Consumer<SearchResultVO> consumer) {
		searchResultVOProperty.addListener((oba, oldval, newval) -> {
			consumer.accept(newval);
		});
	}

	/****************************************************************/
	/****************************************************************/

	/**
	 * 생성자
	 *
	 * @param parent
	 * @param content
	 */
	public TextSearchComposite(Parent parent, ObservableValue<String> content) {
		loader = new FXMLLoader();
		loader.setLocation(TextSearchComposite.class.getResource("TextSearchView.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
			this.contentProperty = new SimpleStringProperty();
			this.contentProperty.bind(content);
			this.searchResultVOProperty = new SimpleObjectProperty<>();
			slidingStartIndexProperty = new SimpleIntegerProperty();

			this.parent = parent;
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtFindTextContent.requestFocus();
			}
		});

	}

	/**
	 * 검색 처리를 진행하며 검색이 완료되면 searchResultVOProperty에 값을 set함과 동시에 부가정보 (
	 * slidingStartIndexProperty, slidingEndIndexProperty )를 갱신함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 */
	private int find() {
		return find(this.txtFindTextContent.getText(), contentProperty.get());
	}

	/**
	 * 검색 처리를 진행하며 검색이 완료되면 searchResultVOProperty에 값을 set함과 동시에 부가정보 (
	 * slidingStartIndexProperty, slidingEndIndexProperty )를 갱신함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 14.
	 * @param content
	 */
	private int find(String content) {
		return find(content, txtFindTextContent.getText());
	}

	/**
	 * 검색 처리를 진행하며 검색이 완료되면 searchResultVOProperty에 값을 set함과 동시에 부가정보 (
	 * slidingStartIndexProperty, slidingEndIndexProperty )를 갱신함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param content
	 * @param function
	 *            부가적으로 처리할 내용을 기술.
	 */
	private int find(String content, Function<SearchResultVO, SearchResultVO> function) {
		return find(txtFindTextContent.getText(), content, function);
	}

	private int find(String findWord, String content) {
		return find(findWord, content, null);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param findWord
	 *            찾을 문자열
	 * @param content
	 * @param function
	 */
	private int find(String findWord, String content, Function<SearchResultVO, SearchResultVO> function) {
		if (content == null || content.isEmpty())
			return -1;

		int startIdx = content.indexOf(findWord, slidingStartIndexProperty.get());
		int findStartIndex = -1;
		if (startIdx >= 0) {
			findStartIndex = startIdx;
			int endIdx = startIdx + findWord.length();

			SearchResultVO value = new SearchResultVO();
			value.setSearchText(findWord);
			value.setStartIndex(startIdx);
			value.setEndIndex(endIdx);

			if (function != null)
				value = function.apply(value);

			searchResultVOProperty.set(value);
			slidingStartIndexProperty.set(endIdx);

		} else {
			slidingStartIndexProperty.set(0);
		}
		return findStartIndex;
	}

	private void findAll(String content, Function<SearchResultVO, SearchResultVO> function) {
		findAll(txtFindTextContent.getText(), content, function);
	}

	private void findAll(String findword, String content, Function<SearchResultVO, SearchResultVO> function) {
		if (content == null || content.isEmpty())
			return;

		int startIdx = 0;

		while (startIdx >= 0) {
			startIdx = content.indexOf(findword, startIdx);
			if (startIdx == -1)
				break;

			int endIdx = startIdx + findword.length();
			SearchResultVO value = new SearchResultVO();
			value.setSearchText(findword);
			value.setStartIndex(startIdx);
			value.setEndIndex(endIdx);
			LOGGER.debug(String.format("FindContent : %s , %d", content.subSequence(startIdx, endIdx), startIdx));
			if (function != null)
				value = function.apply(value);
			searchResultVOProperty.set(value);

			startIdx = endIdx;
		}
	}

	@FXML
	public void initialize() {
		rdoSelectScope.selectedProperty().addListener((oba, oldval, newval) -> {
			if (newval)
				isSelectScopeProperty.set(true);
		});

		rdoGlobalScope.selectedProperty().addListener((oba, oldval, newval) -> {
			if (newval)
				isSelectScopeProperty.set(false);
		});
	}

	/**
	 * 찾기탭에서 찾기
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param e
	 */
	@FXML
	public void btnFindNextOnMouseClick(MouseEvent e) {
		find();
	}

	/**
	 * 바꾸기탭에서 찾기
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param e
	 */
	@FXML
	public void btnReplaceOnFindOnMouseClick() {
		String content = contentProperty.get();
		int findStartIndex = find(txtFind.getText(), content);
		/*
		 * 특화처리. 검색된 문자열의 시작인덱스를 다시 slidingStartIndexProperty에 집어넣음. 이렇게 처리하지않으면
		 * 찾은문자열의 끝부분부터 찾게된다. 이 API의 목적은 replace가 목적이므로 시작인덱스를 넣어줘야함.
		 */
		slidingStartIndexProperty.set(findStartIndex);
	}

	@FXML
	public void txtFindTextContentOnKeyPress(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			find();
		}
	}

	/**
	 * 닫기
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param e
	 */
	@FXML
	public void btnCloseOnMouseClick(MouseEvent e) {
		Scene scene = getScene();
		Stage window = (Stage) scene.getWindow();
		window.close();
	}

	/**
	 * 모두찾기
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 */
	@FXML
	public void btnFindAllOnMouseClick() {
		String content = contentProperty.get();
		findAll(content, null);
	}

	/**
	 * 일치하는수
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 */
	@FXML
	public void btnMatchCountOnMouseClick() {
		final String content = contentProperty.get();
		final String findText = this.txtFindTextContent.getText();

		if (ValueUtil.isEmpty(findText))
			return;

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				int matchIndex = 0;
				int matchCount = -1;
				do {
					matchIndex = content.indexOf(findText, matchIndex);
					matchIndex += 1;
					matchCount++;
				} while (matchIndex > 0);
				txtDesc.setText(String.format("Found Word \" %s \" : count : %d ", findText, matchCount));
			}
		});
	}

	/**
	 * 바꾸기탭에서 찾기 마우스 클릭 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 28.
	 * @param e
	 */
	@FXML
	public void txtFindOnKeyClick(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			btnReplaceOnFindOnMouseClick();
		}
	}

	/**
	 *
	 *
	 * @Date 2015. 10. 21.
	 * @throws IOException
	 * @User KYJ
	 */
	public void show() {
		Stage stage = new Stage();
		LOGGER.debug("SHOW SimpleSQLResult View.....");
		LOGGER.debug("call executeSQL function....");

		Scene scene = new Scene(this/* , 1100, 700 */);
		stage.setScene(scene);

		stage.setResizable(false);
		// stage.setAlwaysOnTop(true);
		stage.initModality(Modality.NONE);
		stage.initOwner(parent.getScene().getWindow());
		stage.setTitle(tabPane.getSelectionModel().getSelectedItem().getText());
		tabPane.getSelectionModel().selectedItemProperty().addListener((oba, oldval, newval) -> {
			String text = newval.getText();
			stage.setTitle(text);
		});

		// esc키를 누르면 팝업 close
		stage.addEventHandler(KeyEvent.ANY, event -> {

			KeyCode code = event.getCode();
			if (KeyCode.ESCAPE == code) {
				event.consume();
				stage.close();
			}
		});
		stage.centerOnScreen();
		stage.showAndWait();
	}

	/* [시작] prop 속성 */
	public final StringProperty contentPropertyProperty() {
		return this.contentProperty;
	}

	public final java.lang.String getContentProperty() {
		return this.contentPropertyProperty().get();
	}

	public final void setContentProperty(final java.lang.String contentProperty) {
		this.contentPropertyProperty().set(contentProperty);
	}

	public final IntegerProperty slidingStartIndexPropertyProperty() {
		return this.slidingStartIndexProperty;
	}

	public final int getSlidingStartIndexProperty() {
		return this.slidingStartIndexPropertyProperty().get();
	}

	public final void setSlidingStartIndexProperty(final int slidingStartIndexProperty) {
		this.slidingStartIndexPropertyProperty().set(slidingStartIndexProperty);
	}

	public final ObjectProperty<SearchResultVO> searchResultVOPropertyProperty() {
		return this.searchResultVOProperty;
	}

	public final com.kyj.fx.voeditor.visual.component.popup.SearchResultVO getSearchResultVOProperty() {
		return this.searchResultVOPropertyProperty().get();
	}

	public final void setSearchResultVOProperty(final com.kyj.fx.voeditor.visual.component.popup.SearchResultVO searchResultVOProperty) {
		this.searchResultVOPropertyProperty().set(searchResultVOProperty);
	}

	public final BooleanProperty isSelectScopePropertyProperty() {
		return this.isSelectScopeProperty;
	}

	public final boolean isIsSelectScopeProperty() {
		return this.isSelectScopePropertyProperty().get();
	}

	public final void setIsSelectScopeProperty(final boolean isSelectScopeProperty) {
		this.isSelectScopePropertyProperty().set(isSelectScopeProperty);
	}

	/* [종료] prop 속성 */

}
