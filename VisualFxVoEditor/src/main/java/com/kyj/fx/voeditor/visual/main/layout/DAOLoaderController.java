/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 11. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.DiffAppController;
import com.kyj.fx.voeditor.visual.component.TextBaseDiffAppController;
import com.kyj.fx.voeditor.visual.component.grid.CommonsBaseGridView;
import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.diff.TextBaseComparator;
import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.main.model.vo.TbmSysDaoMethodsHDVO;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;

/**
 * @author KYJ
 *
 */
@FXMLController("DaoWizardView.fxml")
public class DAOLoaderController {

	private static Logger LOGGER = LoggerFactory.getLogger(DAOLoaderController.class);
	@FXML
	private TextField txtSrchTable;
	@FXML
	private TableView<Map<String, Object>> tbSrchDao;

	@FXML
	private TableColumn<Map<String, Object>, Object> colSrchClassName;
	@FXML
	private TableColumn<Map<String, Object>, Object> colSrchPackageName;

	@FXML
	public void initialize() {
		txtSrchTable.setText("*");
		colSrchClassName.setCellValueFactory(param -> new SimpleObjectProperty<Object>(param.getValue().get("CLASS_NAME").toString()));
		colSrchPackageName.setCellValueFactory(param -> new SimpleObjectProperty<Object>(param.getValue().get("PACKAGE_NAME").toString()));

		MenuItem history = new MenuItem("history");
		history.setOnAction(this::menuHistoryOnAction);

		MenuItem menuRemove = new MenuItem("remove");
		menuRemove.setOnAction(this::menuRemoveOnAction);

		tbSrchDao.setContextMenu(new ContextMenu(history, menuRemove));
	}

	/**
	 * 히스토리 이력을 살펴보는경우 사용
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 6.
	 * @param event
	 */
	public void menuHistoryOnAction(ActionEvent event) {
		Map<String, Object> selectedItem = tbSrchDao.getSelectionModel().getSelectedItem();
		if (selectedItem == null)
			return;

		String packageName = ValueUtil.emptyThan(selectedItem.get("PACKAGE_NAME"), "").toString();
		String className = ValueUtil.emptyThan(selectedItem.get("CLASS_NAME"), "").toString();

		// Stage primaryStage = SharedMemory.getPrimaryStage();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("packageName", packageName);
		paramMap.put("className", className);
		try {
			List<TbmSysDaoMethodsHDVO> select = listHistoryItems(paramMap);

			CommonsBaseGridView<TbmSysDaoMethodsHDVO> commonsBaseGridView = new CommonsBaseGridView<>(TbmSysDaoMethodsHDVO.class, select);
			TableViewSelectionModel<TbmSysDaoMethodsHDVO> selectionModel = commonsBaseGridView.getSelectionModel();
			selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
			BorderPane borderPane = new BorderPane();
			Label value = new Label("History");
			value.setStyle("-fx-font-size:75px");
			borderPane.setTop(value);

			SqlKeywords sqlKeyword = new SqlKeywords();
			borderPane.setBottom(sqlKeyword);
			SplitPane splitPane = new SplitPane(commonsBaseGridView, sqlKeyword);
			splitPane.setOrientation(Orientation.VERTICAL);
			borderPane.setCenter(splitPane);
			commonsBaseGridView.setOnMouseClicked(ev -> {

				if (ev.getClickCount() == 2) {
					try {
						TbmSysDaoMethodsHDVO selectedItem2 = commonsBaseGridView.getSelectionModel().getSelectedItem();
						String histTsp = selectedItem2.getHistTsp();
						String sqlBody = getSqlBody(histTsp);
						sqlKeyword.setContent(sqlBody);
					} catch (Exception e) {
						LOGGER.error(ValueUtil.toString(e));
					}
				}

			});

			MenuItem compare = new MenuItem("Compare.");
			compare.setDisable(true);
			compare.setOnAction(ev -> {

				ObservableList<TbmSysDaoMethodsHDVO> selectedItems = commonsBaseGridView.getSelectionModel().getSelectedItems();
				if (selectedItems.size() == 2) {
					compare(selectedItems.get(0), selectedItems.get(1));
				}
			});

			ContextMenu historyContextMenu = new ContextMenu(compare);
			historyContextMenu.setOnShowing(ev -> {
				if (selectionModel.getSelectedItems().size() == 2) {
					compare.setDisable(false);
				} else {
					compare.setDisable(true);
				}
			});
			commonsBaseGridView.setContextMenu(historyContextMenu);

			Scene scene = new Scene(borderPane);
			Stage stage = new Stage();
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param e
	 */
	public void menuRemoveOnAction(ActionEvent e) {
		Map<String, Object> selectedItem = tbSrchDao.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			String className = (String) selectedItem.get("CLASS_NAME");
			Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("Remove Item",
					"Are you sure ?  \nRemove Target : " + className);

			showYesOrNoDialog.ifPresent(v -> {

				if ("Y".equals(v.getValue())) {
					try {

						//Camel Case로 변경하는 작업이 필요함.
						HashMap<String, Object> param = new HashMap<String, Object>();
						Iterator<String> iterator = selectedItem.keySet().iterator();
						while(iterator.hasNext())
						{
							String key = iterator.next();
							param.put(ValueUtil.toCamelCase(key), selectedItem.get(key));
						}
//						Map<HashMap<String, Object>, HashMap<String, Object>> collect = selectedItem.entrySet().stream()
//						.map(m -> {
//							HashMap<String, Object> hashMap = new HashMap<String, Object>();
//							hashMap.put(ValueUtil.toCamelCase(m.getKey()), m.getValue());
//							return hashMap;
//						})
//						.collect(Collectors.toMap(k -> k, val -> val));


						//삭제
						int count = removDAO(param);

						if (count != -1) {
							//삭제성공.
							//재조회
							List<Map<String, Object>> listDAO = listDAO(txtSrchTable.getText().trim());
							tbSrchDao.getItems().addAll(listDAO);
							//DialogUtil.showMessageDialog(className + " 삭제 완료.");
						} else {
							//삭제 실패.
							DialogUtil.showMessageDialog(className + " 일치하는 조건이 없습니다. ");
						}

					} catch (Exception e1) {
						LOGGER.error(ValueUtil.toString(e1));
						DialogUtil.showExceptionDailog(e1);
					}
				}
			});

		}

	}

	private String getSqlBody(String histTsp) throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("histTsp", histTsp);
		return getHistorySQL(param).getSqlBody();
	}

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 * 두개의 데이터를 비교.
	 *
	 * @param tbmSysDaoMethodsHDVO
	 * @param tbmSysDaoMethodsHDVO2
	 ********************************/
	private void compare(TbmSysDaoMethodsHDVO o1, TbmSysDaoMethodsHDVO o2) {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(DiffAppController.class.getResource("TextBaseDiffApp.fxml"));
			BorderPane load = loader.load();
			TextBaseDiffAppController controller = loader.getController();
			controller.setCompare(new TextBaseComparator());
			controller.setDiff(getSqlBody(o1.getHistTsp()), getSqlBody(o2.getHistTsp()));

			Stage stage = new Stage();
			stage.initOwner(SharedMemory.getPrimaryStage());
			stage.centerOnScreen();
			stage.setScene(new Scene(load));
			stage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * DAO iTEM 삭제.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 9. 23.
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	private int removDAO(Map<String, Object> paramMap) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (DbUtil.isExistsSchemaDatabase()) {
			sb.append("delete from meerkat.tbm_sys_dao\n");
		} else {
			sb.append("delete from tbm_sys_dao\n");
		}
		sb.append("where package_name =:packageName\n");
		sb.append("and class_name =:className\n");

		return DbUtil.update(sb.toString(), paramMap);
	}

	private TbmSysDaoMethodsHDVO getHistorySQL(Map<String, Object> paramMap) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (DbUtil.isExistsSchemaDatabase())
			sb.append("select hist_tsp, sql_body from meerkat.tbp_sys_dao_methods_h ");
		else
			sb.append("select hist_tsp, sql_body from tbp_sys_dao_methods_h ");

		sb.append("where hist_tsp =:histTsp");

		List<TbmSysDaoMethodsHDVO> select = DbUtil.select(sb.toString(), paramMap, (rs, row) -> {
			TbmSysDaoMethodsHDVO tbmSysDaoMethodsHDVO = new TbmSysDaoMethodsHDVO();
			tbmSysDaoMethodsHDVO.setHistTsp(rs.getString("HIST_TSP"));
			tbmSysDaoMethodsHDVO.setSqlBody(rs.getString("SQL_BODY"));
			return tbmSysDaoMethodsHDVO;
		});
		if (select.isEmpty())
			return null;
		return select.get(0);
	}

	private List<TbmSysDaoMethodsHDVO> listHistoryItems(Map<String, Object> paramMap) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select b.hist_tsp, b.package_name, b.class_name, b.method_name, b.result_vo_class, b.dml_type, fst_reg_dt from \n");
		if (DbUtil.isExistsSchemaDatabase())
			sb.append("meerkat.tbm_sys_dao a inner join meerkat.tbp_sys_dao_methods_h b\n");
		else
			sb.append("tbm_sys_dao a inner join tbp_sys_dao_methods_h b\n");
		sb.append("on a.package_name = b.package_name and a.class_name = b.class_name\n");
		sb.append("and a.package_name = :packageName \n");
		sb.append("and a.class_name =:className\n");
		sb.append("order by fst_reg_dt desc\n");

		return DbUtil.select(sb.toString(), paramMap, (rs, row) -> {
			TbmSysDaoMethodsHDVO tbmSysDaoMethodsHDVO = new TbmSysDaoMethodsHDVO();
			tbmSysDaoMethodsHDVO.setHistTsp(rs.getString("HIST_TSP"));
			tbmSysDaoMethodsHDVO.setPackageName(rs.getString("PACKAGE_NAME"));
			tbmSysDaoMethodsHDVO.setClassName(rs.getString("CLASS_NAME"));
			tbmSysDaoMethodsHDVO.setMethodName(rs.getString("METHOD_NAME"));
			tbmSysDaoMethodsHDVO.setResultVoClass(rs.getString("RESULT_VO_CLASS"));
			tbmSysDaoMethodsHDVO.setDmlType(rs.getString("DML_TYPE"));
			tbmSysDaoMethodsHDVO.setFstRegDt(rs.getString("FST_REG_DT"));
			return tbmSysDaoMethodsHDVO;
		});
	}

	public void setSystemLayoutViewController(SystemLayoutViewController systemLayoutViewController) {
	}

	@FXML
	public void tbSrchDaoOnMouseClick(MouseEvent e) {

		if (e.getClickCount() == 2) {
			Platform.runLater(() -> {
				try {
					Map<String, Object> selectedItem = tbSrchDao.getSelectionModel().getSelectedItem();
					if (selectedItem == null)
						return;

					TbmSysDaoDVO tbmSysDAO = new TbmSysDaoDVO();
					tbmSysDAO.setClassName(selectedItem.get("CLASS_NAME").toString());
					tbmSysDAO.setPackageName(selectedItem.get("PACKAGE_NAME").toString());
					tbmSysDAO.setLocation(selectedItem.get("LOCATION").toString());
					tbmSysDAO.setClassDesc(selectedItem.get("CLASS_DESC").toString());
					Object object = selectedItem.get("TABLE_NAME");
					if (object != null)
						tbmSysDAO.setTableName(object.toString());

					FxUtil.load(DAOLoaderController.class);
					FXMLLoader loader = FxUtil.createNewFxmlLoader();//new FXMLLoader();
					loader.setLocation(getClass().getResource("DaoWizardView.fxml"));
					BorderPane pane = loader.load();
					DaoWizardViewController controller = loader.getController();
					controller.setTbmSysDaoProperty(tbmSysDAO);

					/*2016-10-26 by kyj change code  tab handling -> loadNewSystem api */
					//					Tab tab = new Tab("DaoWizard", pane);
					//					this.systemRoot.addTabItem(tab);
					//					tab.getTabPane().getSelectionModel().select(tab);
					SharedMemory.getSystemLayoutViewController().loadNewSystemTab("DaoWizard", pane);

					//2016-09-23 굳히 재조회 할 필요없으므로 주석.
					//					List<Map<String, Object>> listDAO = listDAO(txtSrchTable.getText().trim());
					//					tbSrchDao.getItems().addAll(listDAO);
				} catch (Exception e1) {
					LOGGER.error(e1.toString());
					DialogUtil.showExceptionDailog(e1);
				}
			});

		}
	}

	@FXML
	public void txtSrchTableOnKeyReleased(KeyEvent e) {
		if (KeyCode.ENTER.equals(e.getCode())) {
			Platform.runLater(() -> {
				try {
					List<Map<String, Object>> listDAO = listDAO(txtSrchTable.getText().trim());
					tbSrchDao.getItems().addAll(listDAO);
				} catch (Exception e1) {
					DialogUtil.showExceptionDailog(e1);
				}
			});
		}

	}

	private List<Map<String, Object>> listDAO(String daoName) throws Exception {
		tbSrchDao.getItems().clear();
		if (daoName == null || daoName.isEmpty())
			return FXCollections.emptyObservableList();

		Map<String, Object> param = new HashMap<String, Object>();

		if ("*".equals(daoName)) {
			param.put("tableName", null);
		} else {
			param.put("tableName", daoName);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		if (DbUtil.isExistsSchemaDatabase())
			sb.append("SELECT PACKAGE_NAME, CLASS_NAME,LOCATION,CLASS_DESC,TABLE_NAME FROM meerkat.tbm_sys_dao \n");
		else
			sb.append("SELECT PACKAGE_NAME, CLASS_NAME,LOCATION,CLASS_DESC,TABLE_NAME FROM tbm_sys_dao \n");

		sb.append("WHERE 1=1 \n");
		sb.append("#if($tableName) \n");
		sb.append("AND CLASS_NAME LIKE '%' || :tableName || '%'  \n");
		sb.append("#end \n");
		sb.append("LIMIT 100");

		String velocityToText = ValueUtil.getVelocityToText(sb.toString(), param);

		return DbUtil.select(velocityToText, param, (rs, rowNum) -> {
			Map<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("PACKAGE_NAME", rs.getObject("PACKAGE_NAME"));
			hashMap.put("CLASS_NAME", rs.getObject("CLASS_NAME"));
			hashMap.put("LOCATION", rs.getObject("LOCATION"));
			hashMap.put("CLASS_DESC", rs.getObject("CLASS_DESC"));
			hashMap.put("TABLE_NAME", rs.getObject("TABLE_NAME"));
			return hashMap;
		});

	}

}
