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
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

/**
 * @author KYJ
 *
 */
public class SqlTab extends Tab {

	/**
	 * 디폴트 탭 이름
	 */
	private static final String NEW_TAB = "new Tab*";

	private ObjectProperty<SqlKeywords> txtSql;

	private SaveSQLFunction<File> saveFileFunction;
	//	private LoadSQLFunction<File> loadFileFunction;

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
		SqlKeywords content = new SqlKeywords();
		txtSql = new SimpleObjectProperty<>(content);
		setContent(content);
		//		content.setOnKeyPressed(excutable);
		content.addEventHandler(KeyEvent.KEY_RELEASED, excutable);

		EventDispatcher eventDispatcher = content.getEventDispatcher();

		content.setEventDispatcher((event, tail) -> {

			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				//				System.out.println("sqlTab");
				
				tail.append(keyEventDispatcher);
				tail.append(eventDispatcher);

			}
			Event dispatchEvent = eventDispatcher.dispatchEvent(event, tail);
			return dispatchEvent;

		});

	}

	EventDispatcher keyEventDispatcher = new EventDispatcher() {

		@Override
		public Event dispatchEvent(Event event, EventDispatchChain tail) {

			KeyEvent keyE = (KeyEvent) event;
			// 저장키를 누른경우 * 탭명에 *을 지우기 위한 작업처리
			String text2 = getText();

			boolean isControlDown = keyE.isControlDown();
			if (isControlDown && keyE.getCode() == KeyCode.S) {
				int lastIndexOf = text2.lastIndexOf('*');
				if (lastIndexOf >= 0) {

					File selectedFile = DialogUtil.showFileSaveDialog(null, choser -> {
						
						//경로를 지정하지않을시 마지막에 처리된 경로에 기본으로 로드되므로 주석.
//						String dir = System.getProperty("user.home");
//						choser.setInitialDirectory(new File(dir));

						choser.getExtensionFilters()
								.add(new ExtensionFilter(GargoyleExtensionFilters.SQL_NAME, GargoyleExtensionFilters.SQL));
						choser.getExtensionFilters()
								.add(new ExtensionFilter(GargoyleExtensionFilters.ALL_NAME, GargoyleExtensionFilters.ALL));

					});
					
					if (selectedFile != null) {

						boolean isWritableStatus = true;
						if (selectedFile.exists()) {
							Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("File already exists",
									"Overwrite ? ");
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

					event.consume();
					return event;
				}

			} else if (isControlDown && keyE.getCode() == KeyCode.O) {
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

				event.consume();
			} else {
				// if (keyE.isAltDown() || isControlDown ||
				// keyE.isShiftDown())
				// return event;

				if (!text2.contains("*"))
					setText(text2 + "*");

			}

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

	public SqlKeywords getSqlNode() {
		return txtSql.get();
	}

	public void setSqlNode(SqlKeywords txtSql) {
		this.txtSql.set(txtSql);
	}

	public void setTxtSql(String txtSql) {
		this.txtSql.get().setContent(txtSql);
	}

	public void appendTextSql(String txtSql) {
		this.txtSql.get().appendContent(txtSql);
	}

	public ObjectProperty<SqlKeywords> txtSqlProperty() {
		return this.txtSql;
	}

	public String getSelectedSQLText() {
		return txtSql.get().getSelectedText();
	}

	public String getSqlText() {
		return txtSql.get().getText();
	}

	public ContextMenu getTxtSqlPaneContextMenu(){
		return txtSql.get().getCodeArea().getContextMenu();
	}
	public void setTxtSqlPaneContextMenu(ContextMenu menu){
		 txtSql.get().getCodeArea().setContextMenu(menu);
	}
}
