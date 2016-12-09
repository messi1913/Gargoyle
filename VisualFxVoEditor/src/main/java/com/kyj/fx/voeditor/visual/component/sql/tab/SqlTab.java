/**
 * package : com.kyj.fx.voeditor.visual.component.sql.tab
 *	fileName : SqlTab.java
 *	date      : 2015. 11. 6.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.tab;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.kyj.fx.voeditor.visual.component.sql.functions.SaveSQLFileFunction;
import com.kyj.fx.voeditor.visual.component.sql.functions.SaveSQLFunction;
import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.functions.LoadFileOptionHandler;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.GargoyleExtensionFilters;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

/**
 *
 * 윈도우 기능성 키 이벤트처리.
 * @author KYJ
 *
 */
public class SqlTab extends Tab {

	/**
	 * 디폴트 탭 이름
	 */
	private static final String NEW_TAB = "new Tab*";

	private ObjectProperty<SqlKeywords> sqlPane = new SimpleObjectProperty<>();

	private SaveSQLFunction<File> saveFileFunction;
	//	private LoadSQLFunction<File> loadFileFunction;
	//	private SqlKeywords content;

	public SqlTab() {
		saveFileFunction = new SaveSQLFileFunction();
		//		loadFileFunction = new LoadSQLFileFunction();
	}

	/**
	 *
	 */
	public SqlTab(EventHandler<KeyEvent> excutable) {
		this();
		setText(NEW_TAB);

		SqlKeywords createNewSqlPane = createNewSqlPane();
		sqlPane.set(createNewSqlPane);

		setContent(createNewSqlPane);
		//		content.setOnKeyPressed(excutable);
		createNewSqlPane.addEventHandler(KeyEvent.KEY_RELEASED, excutable);

		EventDispatcher eventDispatcher = createNewSqlPane.getEventDispatcher();

		createNewSqlPane.setEventDispatcher((event, tail) -> {

			EventType<? extends Event> eventType = event.getEventType();
			if (eventType == KeyEvent.KEY_PRESSED) {

				tail.append(keyEventDispatcher);
				tail.append(eventDispatcher);

			}

			Event dispatchEvent = eventDispatcher.dispatchEvent(event, tail);
			return dispatchEvent;

		});

		/***************************************************************************************************************************/
		/* 컨텍스트 메뉴 추가. */
		ContextMenu contextMenu = createNewSqlPane.getCodeArea().getContextMenu();
		ObservableList<MenuItem> items = contextMenu.getItems();
		MenuItem muOpen = new MenuItem("Open");
		muOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		muOpen.setOnAction(this::miOenOnAction);
		items.add(0, muOpen);

		MenuItem muSave = new MenuItem("Save");
		muSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		muSave.setOnAction(this::muSaveOnAction);
		items.add(1, muSave);
		/***************************************************************************************************************************/

	}

	public void muSaveOnAction(ActionEvent e) {

		int lastIndexOf = getText().lastIndexOf('*');
		if (lastIndexOf >= 0) {

			File selectedFile = DialogUtil.showFileSaveDialog(null, choser -> {

				//경로를 지정하지않을시 마지막에 처리된 경로에 기본으로 로드되므로 주석.
				//						String dir = System.getProperty("user.home");
				//						choser.setInitialDirectory(new File(dir));

				choser.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.SQL_NAME, GargoyleExtensionFilters.SQL));
				choser.getExtensionFilters().add(new ExtensionFilter(GargoyleExtensionFilters.ALL_NAME, GargoyleExtensionFilters.ALL));

			});

			if (selectedFile != null) {

				boolean isWritableStatus = true;
				if (selectedFile.exists()) {
					Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("File already exists", "Overwrite ? ");
					isWritableStatus = showYesOrNoDialog.isPresent();
				}

				if (isWritableStatus) {

					// 파일에 실제 쓰는작업처리
					Boolean apply = saveFileFunction.apply(selectedFile, getSqlText());

					// 파일생성에 문제없는경우 탭이름 변경
					if (apply) {
						// tab title 변경
						setText(selectedFile.getName());
					}

				}

			}

			e.consume();
		}

	}

	public void miOenOnAction(ActionEvent e) {
		File showFileDialog = DialogUtil.showFileDialog(/*SharedMemory.getPrimaryStage()*/ null, choser -> {
			/*마지막에 선택한 경로를 자동선택하는 기능이 추가되었으므로 기본경로 선택 처리는 없앰.*/
			//					String dir = System.getProperty("user.home");
			//					choser.setInitialDirectory(new File(dir));
			choser.getExtensionFilters().add(new ExtensionFilter("SQL files (*.sql)", "*.sql"));
		});

		//선택한 파일이 정상적으로 선택된 경우는 null이 아님.
		if (showFileDialog != null) {

			String fileContent = FileUtil.readFile(showFileDialog, new LoadFileOptionHandler());
			if (fileContent != null /*공백여부는 체크안함. 파일 내용에 실제 공백이 포함될 수 있으므로...*/) {
				setTxtSql(fileContent);
				setText(showFileDialog.getName());

			}
		}

		e.consume();
	}

	EventDispatcher keyEventDispatcher = new EventDispatcher() {

		@Override
		public Event dispatchEvent(Event event, EventDispatchChain tail) {

			KeyEvent keyE = (KeyEvent) event;
			// 저장키를 누른경우 * 탭명에 *을 지우기 위한 작업처리
			String text2 = getText();

			if (keyE != null) {
				if (KeyCode.O == keyE.getCode() && keyE.isControlDown() && !keyE.isAltDown() && !keyE.isShiftDown()) {
					miOenOnAction(new ActionEvent());
					keyE.consume();
				} else if (KeyCode.S == keyE.getCode() && keyE.isControlDown() && !keyE.isAltDown() && !keyE.isShiftDown()) {
					muSaveOnAction(new ActionEvent());
					keyE.consume();
				}
			}

			if (!text2.contains("*"))
				setText(text2 + "*");

			//			}

			return event;
		}
	};

	/**
	 * 파일로부터 데이터를 읽은후 텍스트패널에 입력한
	 *
	 * @param file
	 * @throws IOException
	 */
	public void setTextSqlFromFile(File file) throws IOException {
		if (file != null && file.exists()) {
			String readFileToString = FileUtils.readFileToString(file);
			setTxtSql(readFileToString);
		}
	}

	/**
	 * SQL처리하는 패널을 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 9.
	 * @return
	 */
	protected SqlKeywords createNewSqlPane() {
		return new SqlKeywords();
	}

	public SqlKeywords getSqlNode() {
		return sqlPane.get();
	}

	public void setSqlNode(SqlKeywords txtSql) {
		this.sqlPane.set(txtSql);
	}

	public void setTxtSql(String txtSql) {
		this.sqlPane.get().setContent(txtSql);
	}

	public void appendTextSql(String txtSql) {
		this.sqlPane.get().appendContent(txtSql);
	}

	public ObjectProperty<SqlKeywords> txtSqlProperty() {
		return this.sqlPane;
	}

	public String getSelectedSQLText() {
		return sqlPane.get().getSelectedText();
	}

	public String getSqlText() {
		return sqlPane.get().getText();
	}

	public ContextMenu getTxtSqlPaneContextMenu() {
		return sqlPane.get().getCodeArea().getContextMenu();
	}

	public void setTxtSqlPaneContextMenu(ContextMenu menu) {
		sqlPane.get().getCodeArea().setContextMenu(menu);
	}
}
