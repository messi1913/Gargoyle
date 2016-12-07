/********************************
  * 프로젝트 : VisualFxVoEditor
  * 패키지   : com.kyj.fx.voeditor.visual.component.grid
  * 작성일   : 2016. 8. 23.
  * 작성자   : KYJ
  *******************************/
package com.kyj.fx.voeditor.visual.component.grid;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ColumnExpression;
import com.kyj.fx.voeditor.visual.component.grid.EditableTableView.ValueExpression;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/***************************
 *
 * EditableTableView를 업무적으로 사용하기 편하게 처리하기 위한 구성품
 *
 * 버튼 및 이벤트 항목이 정의되있음.
 *
 * @author KYJ
 *
 ***************************/
public class EditableTableViewComposite extends BorderPane {

	private EditableTableView editableTableView;
	private StringProperty sql = new SimpleStringProperty();
	private Label txtTableName;
	private Button btnExec, btnAdd, btnRemove, btnSave;
	private Label lblStatus;

	public EditableTableViewComposite(Supplier<Connection> connectionSupplier) {

		btnExec = new Button("조회");
		btnAdd = new Button("추가");
		btnAdd.setDisable(true);
		btnRemove = new Button("삭제");
		btnSave = new Button("저장");

		txtTableName = new Label();
		lblStatus = new Label("Status - Ready");

		HBox hBox = new HBox(5, txtTableName, btnExec, btnAdd, btnRemove, btnSave);
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.setPadding(new Insets(5));
		editableTableView = new EditableTableView(connectionSupplier);
		editableTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		editableTableView.getSelectionModel().setCellSelectionEnabled(true);

		editableTableView.tableNameProperty().addListener((oba, oldval, newval) -> {
			btnAdd.setDisable(false);
		});

		btnExec.setOnAction(defaultExecOnAction);

		btnAdd.setOnAction(ev -> {
			editableTableView.getItems().add(new HashMap<>());
			editableTableView.getSelectionModel().selectLast();
			editableTableView.scrollTo(editableTableView.getItems().size() - 1);
		});

		btnRemove.setOnAction(ev -> {
			editableTableView.getItems().removeAll(editableTableView.getSelectionModel().getSelectedItems());
		});

		btnSave.setOnAction(ev -> {
			try {
				editableTableView.save();
			} catch (Exception e) {
				DialogUtil.showExceptionDailog(e);
			}
		});

		editableTableView.setOnTransactionSucessListener(lblStatus::setText);
		editableTableView.setOnFailListener(lblStatus::setText);

		//		editableTableView.setOnMouseClicked(ev -> {
		//			Map<ColumnExpression, ObjectProperty<ValueExpression>> selectedItem = editableTableView.getSelectionModel().getSelectedItem();
		//			System.out.println(selectedItem);
		//
		//		});

		FxUtil.installClipboardKeyEvent(editableTableView);
		//		editableTableView.addEventHandler(KeyEvent.KEY_RELEASED, this::editableTableViewOnKeyReleased);
		setTop(hBox);
		setCenter(editableTableView);
		setBottom(lblStatus);

		addStyle();

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 12. 7.
	 * @param buttons2
	 */
	private void addStyle() {
		Button[] buttons = new Button[] { btnExec, btnAdd, btnRemove, btnSave };;
		for (Button btn : buttons) {
			btn.getStyleClass().add("button-gargoyle");
		}

	}

	private EventHandler<ActionEvent> defaultExecOnAction = e -> {
		try {
			execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	};

	public void setSql(String sql) {

		if (ValueUtil.isEditScript(sql)) {
			String tableName = findTableNameBySql(sql);
			String _sql = sql.replaceFirst("edit", "select * from");
			this.sql.set(_sql);
			txtTableName.setText(tableName != null ? tableName : "");
		}
	}

	public int execute() throws Exception {
		String tableName = txtTableName.getText();
		if (!sql.get().isEmpty()) {
			editableTableView.readByTableName(sql.get(), tableName);
		}
		return editableTableView.getItems().size();
	}

	public static boolean isEditScript(String text) {
		String[] split = text.trim().split("\\s");
		if (split != null && split.length != 0 && "edit".equalsIgnoreCase(split[0]))
			return true;
		return false;
	}

	public static String findTableNameBySql(String text) {
		String[] split = text.trim().split("\\s+");
		boolean isStart = false;
		String tableName = null;
		for (String str : split) {

			if ("edit".equalsIgnoreCase(str)) {
				isStart = true;
				continue;
			}

			if (isStart) {
				tableName = str;
				break;
			} else {
				break;
			}
		}
		return tableName;
	}

	public ObservableList<TableColumn<Map<ColumnExpression, ObjectProperty<ValueExpression>>, ?>> getColumns() {
		return editableTableView.getColumns();
	}

	public ObservableList<Map<ColumnExpression, ObjectProperty<ValueExpression>>> getItems() {
		return editableTableView.getItems();
	}

	/**
	 * @return the editableTableView
	 */
	public final EditableTableView getEditableTableView() {
		return editableTableView;
	}

	/**
	 * @return the btnExec
	 */
	public final Button getBtnExec() {
		return btnExec;
	}

	/**
	 * @return the btnAdd
	 */
	public final Button getBtnAdd() {
		return btnAdd;
	}

	/**
	 * @return the btnRemove
	 */
	public final Button getBtnRemove() {
		return btnRemove;
	}

	/**
	 * @return the btnSave
	 */
	public final Button getBtnSave() {
		return btnSave;
	}

	/**
	 * @param editableTableView
	 *            the editableTableView to set
	 */
	public final void setEditableTableView(EditableTableView editableTableView) {
		this.editableTableView = editableTableView;
	}

	/**
	 * @param btnExec
	 *            the btnExec to set
	 */
	public final void setBtnExec(Button btnExec) {
		this.btnExec = btnExec;
	}

	/**
	 * @param btnAdd
	 *            the btnAdd to set
	 */
	public final void setBtnAdd(Button btnAdd) {
		this.btnAdd = btnAdd;
	}

	/**
	 * @param btnRemove
	 *            the btnRemove to set
	 */
	public final void setBtnRemove(Button btnRemove) {
		this.btnRemove = btnRemove;
	}

	/**
	 * @param btnSave
	 *            the btnSave to set
	 */
	public final void setBtnSave(Button btnSave) {
		this.btnSave = btnSave;
	}

}
