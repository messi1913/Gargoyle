/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 8. 30.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

/**
 * SQL 매크로 기능을 지우너하기 위한 컨트롤러
 *
 * @author KYJ
 *
 */
public class MacroControl extends Control {

	private static final Logger LOGGER = LoggerFactory.getLogger(MacroControl.class);

	/**
	 * 접속가능한 데이터베이스 Connection을 리턴
	 *
	 * @최초생성일 2016. 8. 30.
	 */
	private Supplier<Connection> connectionSupplier;
	private String initText;

	/**
	 * @param connectionSupplier
	 */
	public MacroControl(Supplier<Connection> connectionSupplier) {
		this(connectionSupplier, "");
	}

	/**
	 * @param connectionSupplier
	 * @param initText
	 */
	public MacroControl(Supplier<Connection> connectionSupplier, String initText) {
		this.connectionSupplier = connectionSupplier;
		this.initText = initText;
	}

	/* (non-Javadoc)
	 * @see javafx.scene.control.Control#createDefaultSkin()
	 */
	@Override
	protected Skin<?> createDefaultSkin() {

		MacroBaseSkin macroBaseSkin = new MacroBaseSkin(this);
		macroBaseSkin.setInitText(this.initText);
		return macroBaseSkin;
	}

	/**
	 * Start 버튼을 클릭하면 결과가 리턴된다. param으로 입력받은 데이터는 textArea에서 적혀져있는 텍스트문자열.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @param param
	 * @return
	 * @throws Exception
	 */
	//	public void start(TableView<Map<String, String>> tbResult, String param) throws Exception {
	//		Skin<?> skin = this.getSkin();
	//		if (skin instanceof MacroBaseSkin) {
	//			MacroBaseSkin mskin = (MacroBaseSkin) skin;
	//			mskin.start();
	//		}
	//	}

	/**
	 * 매크로 동작을 멈춘다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 30.
	 * @return
	 */
	public boolean stop() {
		Skin<?> skin = this.getSkin();
		if (!(skin instanceof MacroBaseSkin)) {
			return false;
		}
		MacroBaseSkin mskin = (MacroBaseSkin) skin;
		return mskin.stop();
	}

	public void tbResultOnKeyReleased() {

		Skin<?> skin = this.getSkin();
		if (!(skin instanceof MacroBaseSkin)) {
			return;
		}

		MacroBaseSkin mskin = (MacroBaseSkin) skin;
		TableView<Map<String, String>> tbResult = mskin.getTbResult();
		int type = 1;

		ObservableList<TablePosition> selectedCells = tbResult.getSelectionModel().getSelectedCells();

		TablePosition tablePosition = selectedCells.get(0);
		TableColumn tableColumn = tablePosition.getTableColumn();
		int row = tablePosition.getRow();
		int col = tbResult.getColumns().indexOf(tableColumn);

		switch (type) {
		case 1:
			StringBuilder sb = new StringBuilder();
			for (TablePosition cell : selectedCells) {
				// TODO :: 첫번째 컬럼(행 선택 기능)도 빈값으로 복사됨..
				// 행변경시
				if (row != cell.getRow()) {
					sb.append("\n");
					row++;
				}
				// 열 변경시
				else if (col != tbResult.getColumns().indexOf(cell.getTableColumn())) {
					sb.append("\t");
				}
				Object cellData = cell.getTableColumn().getCellData(cell.getRow());
				sb.append(ValueUtil.decode(cellData, cellData, "").toString());
			}
			FxClipboardUtil.putString(sb.toString());

			break;
		case 2:
			Object cellData = tableColumn.getCellData(row);
			FxClipboardUtil.putString(ValueUtil.decode(cellData, cellData, "").toString());
			break;
		}

	}

	/**
	 * @return the connectionSupplier
	 */
	public final Supplier<Connection> getConnectionSupplier() {
		return connectionSupplier;
	}

	/**
	 * Stop요청이 들어온경우 성공적으로 정지되면 호출되는 이벤트 정의
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param onStopSuccessed
	 */
	//	public void setonStopSuccessed(Consumer<Void> onStopSuccessed) {
	//		Skin<?> skin = this.getSkin();
	//		if (!(skin instanceof MacroBaseSkin)) {
	//			return;
	//		}
	//
	//		MacroBaseSkin mskin = (MacroBaseSkin) skin;
	//		mskin.setOnStopSuccessed(onStopSuccessed);
	//	}

	/**
	 * 에러가 발생되서 정지되는경우 호출되는 이벤트 정의
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 31.
	 * @param onStopErrored
	 */
	//	public void setOnStopErrored(Consumer<Void> onStopErrored) {
	//		Skin<?> skin = this.getSkin();
	//		if (!(skin instanceof MacroBaseSkin)) {
	//			return;
	//		}
	//
	//		MacroBaseSkin mskin = (MacroBaseSkin) skin;
	//		mskin.setOnStopErrored(onStopErrored);
	//	}

}
