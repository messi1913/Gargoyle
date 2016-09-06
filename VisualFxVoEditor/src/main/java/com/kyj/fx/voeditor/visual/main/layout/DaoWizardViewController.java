
/********************************
 * 프로젝트 : VisualFxVoEditor
 * 패키지   : DaoWizardView
 * 작성일   : 2015. 10. 21.
 * 작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.layout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.util.EditorUtil;
import com.kyj.fx.voeditor.visual.component.CommonsContextMenu;
import com.kyj.fx.voeditor.visual.component.Menus;
import com.kyj.fx.voeditor.visual.component.NumberingCellValueFactory;
import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.BaseOpenClassResourceView;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseTableView;
import com.kyj.fx.voeditor.visual.component.popup.JavaTextView;
import com.kyj.fx.voeditor.visual.component.popup.MeerketAbstractVoOpenClassResourceView;
import com.kyj.fx.voeditor.visual.component.popup.SimpleSQLResultView;
import com.kyj.fx.voeditor.visual.component.text.SqlKeywords;
import com.kyj.fx.voeditor.visual.events.CommonContextMenuEvent;
import com.kyj.fx.voeditor.visual.functions.DatabaseTypeMappingFunction;
import com.kyj.fx.voeditor.visual.functions.FxDAOReadFunction;
import com.kyj.fx.voeditor.visual.functions.FxDAOSaveFunction;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DaoWizardConverter;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.NullExpresion;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import javafx.util.StringConverter;
import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TableDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableMasterDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;
import kyj.Fx.dao.wizard.core.util.QuerygenUtil;

/**
 * @author KYJ
 *
 */
public class DaoWizardViewController {

	private static final String MSG_NOMAL = "";

	private static final String MSG_CLASS_NAME_IS_EMPTY = "class Name is Empty.....";

	private static final String MSG_USED_RESERVED_KEYWORD = "Cant Use Java Keyword.....";

	private static final String MSG_RESULT_VO_CLASS_IS_EMPTY = "ResultVo class is Empty.....";

	private static final String MSG_MAPPING_DATA_EMPTY = "Mapping data Empty.....";

	private static final String MSG_NO_WARNNING = "No Warnning...";

	private static final List<String> MESSAGE_CODES = Arrays.asList(MSG_NOMAL, MSG_CLASS_NAME_IS_EMPTY, MSG_USED_RESERVED_KEYWORD,
			MSG_RESULT_VO_CLASS_IS_EMPTY, MSG_MAPPING_DATA_EMPTY, MSG_NO_WARNNING);

	private static final Logger LOGGER = LoggerFactory.getLogger(DaoWizardViewController.class);

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtClassName;
	@FXML
	private TextArea txtAreaDaoDesc;
	@FXML
	private TextField txtPackageName;
	@FXML
	private TextField txtDaoLocation;
	@FXML
	private TextField txtTableName;
	@FXML
	private SqlKeywords txtSql;
	@FXML
	private TableView<TbpSysDaoMethodsDVO> tbMethods;
	@FXML
	private TableColumn<TbpSysDaoMethodsDVO, Integer> colMethodNo;
	@FXML
	private TableColumn<TbpSysDaoMethodsDVO, String> colResultVoClass;
	@FXML
	private TableColumn<TbpSysDaoMethodsDVO, String> methodName;
	@FXML
	private TableColumn<TbpSysDaoMethodsDVO, String> methodDesc;
	@FXML
	private TableView<TbpSysDaoFieldsDVO> tbParams;
	@FXML
	private TableColumn<TbpSysDaoFieldsDVO, Integer> colParamNo;
	@FXML
	private TableColumn<TbpSysDaoFieldsDVO, String> colParamTypes;
	@FXML
	private TableColumn<TbpSysDaoFieldsDVO, String> colParamTestValue;
	@FXML
	private TableView<TbpSysDaoColumnsDVO> tbMappings;

	/**
	 * DAOWizard가 갖고 있는 메인 데이터
	 *
	 * @최초생성일 2015. 11. 2.
	 */
	private ObjectProperty<TbmSysDaoDVO> tbmSysDaoDVOProperty;

	/**
	 * 메타데이터 데이터 조회용 scope
	 *
	 * @최초생성일 2015. 11. 2.
	 */
	private FxDAOReadFunction fxDAOReadFunction;

	/**
	 * 생성자
	 */
	public DaoWizardViewController() {
		fxDAOReadFunction = new FxDAOReadFunction();
	}

	/**
	 * 파라미터로 온 데이터 바인딩
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 2.
	 * @param dvo
	 */
	public void setTbmSysDaoProperty(TbmSysDaoDVO tbmSysDaoDVO) {

		txtPackageName.setText(tbmSysDaoDVO.getPackageName());
		txtClassName.setText(tbmSysDaoDVO.getClassName());
		txtDaoLocation.setText(tbmSysDaoDVO.getLocation());
		txtAreaDaoDesc.setText(tbmSysDaoDVO.getClassDesc());
		txtTableName.setText(tbmSysDaoDVO.getTableName());

		TbmSysDaoDVO apply = fxDAOReadFunction.apply(tbmSysDaoDVO);
		this.tbmSysDaoDVOProperty.set(apply);

		txtDaoLocation.setText(ValueUtil.removeLocalLocation(tbmSysDaoDVO.getLocation()));

		List<TbpSysDaoMethodsDVO> tbpSysDaoMethodsDVOList = apply.getTbpSysDaoMethodsDVOList();
		tbMethods.getItems().clear();
		tbMethods.getItems().addAll(tbpSysDaoMethodsDVOList);
	}

	@FXML
	public void initialize() {

		tbmSysDaoDVOProperty = new SimpleObjectProperty<>(new TbmSysDaoDVO());
		colParamTestValue.setCellFactory(TextFieldTableCell.forTableColumn());
		methodDesc.setCellFactory(TextFieldTableCell.forTableColumn());
		methodName.setCellFactory(TextFieldTableCell.forTableColumn());
		// [시작] 넘버링 컬럼
		colMethodNo.setCellValueFactory(new NumberingCellValueFactory<>(tbMethods.getItems()));
		tbMethods.getSelectionModel().selectedItemProperty().addListener((arg, ov, nv) -> {
			if (nv != null) {
				tbMethodsOnSelected(nv);
			}
		});

		/*
		 * 2015-11-02 기존에 사용하던 NumberingCellValueFactory을 적용할 수 없음.
		 * colParamNo컬럼같은경우는 method에 따라 데이터의 주소값이 바뀌는 타입이라 주소값이 바뀌는 상태에선 적절한 넘버링
		 * 데이터가 화면에 보여주지않음. 하여 주소값을 계속 유지시킬 수 있도록 tbMethods에서 선택된 메소드정보에서 값을
		 * 참조하여 넘버링을 시킴.
		 *
		 * 2016-04-19 파람의 아이템을 삭제하고 다시 호출했을때 인덱스 순서가 맞지않던 버그 fix
		 */
		colParamNo.setCellValueFactory(param -> {
			return new ReadOnlyObjectWrapper<Integer>(tbParams.getItems().indexOf(param.getValue()) + 1);
		});
		// [끝] 넘버링 컬럼

		colParamTypes.setCellFactory(ChoiceBoxTableCell.forTableColumn("Nomal", "Arrays"));

		// 텍스트의 내용이 변경되면 메모리에 적재시키기 위한 이벤트 함수.
		// 2015.11.17 키 이벤트가 눌릴때 처리되지않게함, 타이밍 문제가 있어, 저장시에 문제가 있음.
		// txtSql.getCodeArea().setOnKeyPressed();

		txtSql.getCodeArea().setOnKeyReleased(event -> {

			if (!event.isShortcutDown()) {
				TbpSysDaoMethodsDVO selectedMethodItem = getSelectedMethodItem();
				if (selectedMethodItem != null)
					selectedMethodItem.setSqlBody(txtSql.getText());
			}
		});

		StringConverter<String> classConverter = new StringConverter<String>() {

			@Override
			public String toString(String clazz) {
				if (clazz == null || clazz.isEmpty())
					return "";
				String result = clazz;
				int lastIndexOf = clazz.lastIndexOf('.');
				if (lastIndexOf >= 0) {
					result = clazz.substring(lastIndexOf + 1);
				}
				return result;
			}

			@Override
			public String fromString(String clazz) {
				return clazz;
			}
		};

		// 메소드 컬럼에서 Result Vo Class를 더블클릭한경우 VO참조를 걸 수 있도록 팝업창을 뜨는 로직 포함되있음.
		colResultVoClass.setCellFactory(param -> {

			TextFieldTableCell<TbpSysDaoMethodsDVO, String> textFieldTableCell = new TextFieldTableCell<>();
			textFieldTableCell.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {

					String clazz = textFieldTableCell.getItem();

					try {
						BaseOpenClassResourceView view = null;
						if (clazz != null) {
							view = new MeerketAbstractVoOpenClassResourceView(clazz);
						} else {
							view = new MeerketAbstractVoOpenClassResourceView();
						}

						view.setConsumer(str -> {
							if (str == null || str.isEmpty())
								return;
							try {
								int selectedItem = tbMethods.getSelectionModel().getSelectedIndex();

								tbMethods.getItems().get(selectedItem).setResultVoClass(str);

							} catch (Exception e) {
								DialogUtil.showExceptionDailog(e, "로드할 수 없는 클래스 유형입니다.");
								return;
							}
						});
						ResultDialog<String> show = view.show();
						show.consume();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			textFieldTableCell.setConverter(classConverter);

			return textFieldTableCell;
		});

		colResultVoClass.setCellValueFactory(param -> param.getValue().resultVoClassProperty());

		// SQL 에디터 마우스 클릭이벤트, 선택한 메소드 항목이 없다면 Editable을 활성화 시키지않는다.
		txtSql.getCodeArea().setOnMouseClicked(event -> {
			if (!event.isShortcutDown()) {
				int selectedMethodIndex = getSelectedMethodIndex();
				if (selectedMethodIndex == -1) {
					txtSql.getCodeArea().setEditable(false);
				} else {
					txtSql.getCodeArea().setEditable(true);
				}
			}

		});

		lblMessage.setText(MSG_NO_WARNNING);

		// 메뉴등록작업
		applyContextMenu();

	}

	/**
	 * 메뉴 적용
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 */
	private void applyContextMenu() {
		// tbMethod 테이블의 컨텍스트 메뉴 적용
		tbMethodContextMenu();
		// tbParam 테이블의 컨텍스트 메뉴 적용
		tbParamContextMenu();
		// tbMappings 테이블의 컨텍스트 메뉴 적용
		tbMappingsContextMenu();
	}

	/**
	 * 메소드 그리드 메뉴 컨텍스트 추가.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 */
	private void tbMethodContextMenu() {

		/* ContextMenu addMenus = */CommonsContextMenu.addMenus(tbMethods, Menus.useCudButtons());
		tbMethods.addEventHandler(CommonContextMenuEvent.ACTION, event -> {
			CommonContextMenuEvent _event = (CommonContextMenuEvent) event;
			// 메뉴 추가시.....
			if (Menus.ADD == _event.getMode()) {
				addDefDaoMethod("", true);
			}
			// 메뉴 삭제시.......
			else if (Menus.DELETE == _event.getMode()) {
				TbpSysDaoMethodsDVO item = getSelectedMethodItem();
				int selectedIndex = getSelectedMethodIndex();
				tbmSysDaoDVOProperty.get().getTbpSysDaoMethodsDVOList().remove(item);
				txtSql.setContent("");
				tbParams.getItems().clear();
				tbMethods.getItems().remove(selectedIndex);
			}

		});
	}

	/**
	 * tbParam 테이블의 컨텍스트 메뉴 적용
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 */
	private void tbParamContextMenu() {

		MenuItem menuItemExtractFromQuery = new MenuItem("Extract from Query");
		menuItemExtractFromQuery.setOnAction(this::menuItemExtractFromQueryOnAction);

		MenuItem menuItemRemove = new MenuItem("Remove Var.");
		menuItemRemove.setOnAction(this::menuItemRemoveOnAction);
		// 사실 이 항목은 필요없을것같음.
		// MenuItem addMenuItem = new MenuItem("Add");
		// addMenuItem.setOnAction(this::addMenuParamOnAction);
		// MenuItem deleteMenuItem = new MenuItem("Delete");
		// deleteMenuItem.setOnAction(this::deleteMenuParamOnAction);

		tbParams.setContextMenu(new ContextMenu(menuItemExtractFromQuery, menuItemRemove));

	}

	private void tbMappingsContextMenu() {
		MenuItem menuToVoEditor = new MenuItem("To VO Editor");
		menuToVoEditor.setOnAction(this::menuToVoEditorOnAction);

		// 사실 이 항목은 필요없을것같음.
		// MenuItem addMenuItem = new MenuItem("Add");
		// addMenuItem.setOnAction(this::addMenuParamOnAction);
		// MenuItem deleteMenuItem = new MenuItem("Delete");
		// deleteMenuItem.setOnAction(this::deleteMenuParamOnAction);

		tbMappings.setContextMenu(new ContextMenu(menuToVoEditor));
	}

	/**
	 * 파라미터 항목을 추가한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 6.
	 * @param e
	 */
	@Deprecated
	public void addMenuParamOnAction(ActionEvent e) {

		TbpSysDaoFieldsDVO e2 = new TbpSysDaoFieldsDVO();
		tbParams.getItems().add(e2);

		// List<TbpSysDaoFieldsDVO> tbpSysDaoFieldsDVOList =
		// this.getSelectedMethodItem().getTbpSysDaoFieldsDVOList();
		// tbpSysDaoFieldsDVOList.add(e2);
	}

	@Deprecated
	public void deleteMenuParamOnAction(ActionEvent e) {
		TbpSysDaoFieldsDVO selectedItem = tbParams.getSelectionModel().getSelectedItem();
		if (selectedItem != null)
			this.getSelectedMethodItem().getTbpSysDaoFieldsDVOList().remove(selectedItem);
	}

	/**
	 * 쿼리문에서 Velocity 변수값을 찾는다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param e
	 */
	public void menuItemExtractFromQueryOnAction(ActionEvent e) {
		/// 2016.4.7 모두 삭제후 등록하는게 아닌 기존에 있는 변수는 유지.
		// tbParams.getItems().clear();
		String velocitySQL = txtSql.getText().trim();
		if (velocitySQL == null || velocitySQL.isEmpty())
			return;

		final List<String> velocityKeys = ValueUtil.getVelocityKeys(velocitySQL);
		ObservableList<TbpSysDaoFieldsDVO> fields = FXCollections.observableArrayList();
		Set<String> keys = new HashSet<String>(velocityKeys);

		// 기존에 존재했던 값이 있으면 먼저 바인드.
		ObservableList<TbpSysDaoFieldsDVO> oldFieldList = tbParams.getItems();
		for (TbpSysDaoFieldsDVO vo : oldFieldList) {
			String fieldName = vo.getFieldName();
			if (keys.contains(fieldName)) {
				fields.add(vo);
				keys.remove(fieldName);
			}
		}

		/* 구버젼 */
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();

			/* 2016.4.7 이미 존재하는 데이터는 유지한다. */
			// Optional<TbpSysDaoFieldsDVO> findAny =
			// tbParams.getItems().stream().filter(v ->
			// key.equals(v.getFieldName())).findAny();
			// if (findAny.isPresent()) {
			// continue;
			// }

			TbpSysDaoFieldsDVO dvo = new TbpSysDaoFieldsDVO();
			dvo.setFieldName(key);
			fields.add(dvo);
		}
		/* 구버젼 */

		/* 메소드항목에 생성된 다이나픽 필드변수를 메모리에 저장한다. */
		TbpSysDaoMethodsDVO tbpSysDaoMethodsDVO = getSelectedMethodItem();
		if (tbpSysDaoMethodsDVO != null) {
			tbpSysDaoMethodsDVO.setTbpSysDaoFieldsDVOList(fields);
			tbParams.getItems().clear();
			tbParams.getItems().addAll(fields);
		}

	}

	/********************************
	 * 작성일 : 2016. 4. 7. 작성자 : KYJ
	 *
	 * 내용 : 변수를 제거함.
	 *******************************/
	public void menuItemRemoveOnAction(ActionEvent e) {
		NullExpresion.ifNotNullDo(tbParams.getSelectionModel().getSelectedItem(), v -> {
			tbParams.getItems().remove(v);
		});
	}

	/********************************
	 * 작성일 : 2016. 2. 27. 작성자 : KYJ
	 *
	 * 내용 : VO에티터로 결과내용을 전달처리한다.
	 *******************************/
	public void menuToVoEditorOnAction(ActionEvent e) {
		ObservableList<TbpSysDaoColumnsDVO> items = tbMappings.getItems();
		if (items.isEmpty())
			return;
		DatabaseTypeMappingFunction typeConverter = new DatabaseTypeMappingFunction();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("VoEditorView.fxml"));
			BorderPane root = loader.load();
			VoEditorController controller = loader.getController();

			List<TableModelDVO> resultItems = items.stream().map(m -> {
				TableModelDVO dvo = new TableModelDVO();

				dvo.setName(ValueUtil.getPrefixLowerTextMyEdit(m.getColumnName()));
				dvo.setType(typeConverter.apply(m.getColumnType()));
				return dvo;
			}).collect(Collectors.toList());

			controller.addItem(resultItems);
			SharedMemory.getSystemLayoutViewController().loadNewSystemTab("New VO", root);
		} catch (IOException e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}

	}

	/**
	 * 선택한 메소드 그리드의 인덱스를 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @return
	 */
	public int getSelectedMethodIndex() {
		return tbMethods.getSelectionModel().getSelectedIndex();
	}

	/**
	 * 선택한 메소드 그리드의 아이템을 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @return
	 */
	public TbpSysDaoMethodsDVO getSelectedMethodItem() {
		int selectedMethodIndex = getSelectedMethodIndex();
		if (selectedMethodIndex == -1)
			return null;
		TbpSysDaoMethodsDVO tbpSysDaoMethodsDVO = tbmSysDaoDVOProperty.get().getTbpSysDaoMethodsDVOList().get(selectedMethodIndex);
		return tbpSysDaoMethodsDVO;
	}

	/**
	 * 선택한 메소드의 다이나믹 필드리스트 아이템을 반환한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @return
	 */
	public List<TbpSysDaoFieldsDVO> getSelectedFieldItems() {
		TbpSysDaoMethodsDVO selectedMethodItem = getSelectedMethodItem();
		if (selectedMethodItem != null)
			return selectedMethodItem.getTbpSysDaoFieldsDVOList();
		return null;
	}

	/**
	 * 데이터베이스 선택시....
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param e
	 */
	@FXML
	public void btnDaoDatabaseMouseClick(MouseEvent e) {
		DatabaseTableView view = new DatabaseTableView();
		TableDVO tableDVO = view.show();
		if (tableDVO != null) {
			TableMasterDVO tableMasterDVO = tableDVO.getTableMasterDVO();
			txtTableName.setText(tableMasterDVO.getTableName());

			// TbmSysDaoDVO tbmSysDaoDVO = new TbmSysDaoDVO();
			tbmSysDaoDVOProperty.get().setTableName(tableMasterDVO.getTableName());
		}

	}

	/**
	 * 키보드를 작성하게될떄마다 Description의 부분을 마스터 데이터셋에 바인드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 29.
	 * @param e
	 */
	@FXML
	public void txtAreadDaoDescOnKeyPressd(KeyEvent e) {

	}

	/**
	 * 테이블명에 해당하는 디폴트 쿼리문 작성
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 */
	@FXML
	public void btnMakeDefMouseClick() {
		String tableName = txtTableName.getText();
		if (tableName == null || tableName.isEmpty()) {
			DialogUtil.showMessageDialog("테이블이 비어있습니다.");
			return;
		}

		ObservableList<TbpSysDaoMethodsDVO> items = tbMethods.getItems();
		// int size = items.size();
		// int size =
		// tbmSysDaoDVOProperty.get().getTbpSysDaoMethodsDVOList().size();
		defaultDaoMethodByTable(items);

	}

	/**
	 * 메소드 테이블 로우 더블클릭 이벤트처리.
	 *
	 * 저장되어있는 SQL을 UI에 보여주는 기능을한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param e
	 */
	public void tbMethodsOnSelected(TbpSysDaoMethodsDVO selectedMethodItem) {
		// tbParams.getItems().clear();
		List<TbpSysDaoFieldsDVO> selectedFieldItems = getSelectedFieldItems();

		if (selectedMethodItem != null) {
			String sqlBody = selectedMethodItem.getSqlBody();
			txtSql.setContent(sqlBody);

			List<TbpSysDaoColumnsDVO> tbpSysDaoColumnsDVOList = selectedMethodItem.getTbpSysDaoColumnsDVOList();
			tbMappings.setItems(FXCollections.observableArrayList(tbpSysDaoColumnsDVOList));
		}

		if (selectedFieldItems != null)
			tbParams.setItems(FXCollections.observableList(selectedFieldItems));

	}

	/**
	 * 데이터베이스 처리에의한 디폴트값(TbpSysDaoMethodsDVO) 생성
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param items
	 * @param size
	 */
	private void defaultDaoMethodByTable(ObservableList<TbpSysDaoMethodsDVO> items) {
		String appendName = DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHSSS);
		TbpSysDaoMethodsDVO e = addDefDaoMethod("dList" + appendName, true);
		String tableName = txtTableName.getText();
		List<String> columns = getTableColumns(tableName);
		String sqlBody = QuerygenUtil.queryjen(tableName, columns);
		e.setSqlBody(sqlBody);
	}

	/**
	 * 디폴트 메소드를 추가함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @param methodName
	 * @return
	 */
	private TbpSysDaoMethodsDVO addDefDaoMethod(String methodName, boolean select) {
		TbmSysDaoDVO tbmSysDaoDVO = tbmSysDaoDVOProperty.get();

		TbpSysDaoMethodsDVO e = new TbpSysDaoMethodsDVO(tbmSysDaoDVO);
		e.setMethodName(methodName);

		// UI에 데이터를 보여줌
		tbMethods.getItems().add(e);
		if (select) {
			tbMethods.getSelectionModel().select(e);
		}

		/* 메모리에 데이터 적재 */

		List<TbpSysDaoMethodsDVO> tbpSysDaoMethodsDVOList = tbmSysDaoDVO.getTbpSysDaoMethodsDVOList();
		tbpSysDaoMethodsDVOList.add(e);

		return e;
	}

	@SuppressWarnings("unchecked")
	private List<String> getTableColumns(String tableName) {
		String sqlColumns = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_COLUMN);
		List<String> columns = Collections.EMPTY_LIST;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("tableName", tableName);
		try {
			columns = DbUtil.select(sqlColumns, hashMap, (rs, row) -> rs.getString("COLUMN_NAME"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columns;
	}

	/**
	 * 텍스트에 기술된 SQL문을 실행한다. 기본적으로 ROWNUM 기술문을 100개를 감싸서 SQL을 조회한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 */
	@FXML
	public void btnExecOnMouseClick(MouseEvent e) {

		LOGGER.debug("event] btnExecOnMouseClick");
		String velocitySQL = txtSql.getText().trim();
		if (velocitySQL == null || velocitySQL.isEmpty())
			return;

		LOGGER.debug(String.format("velocitySQL : %s", velocitySQL));
		// 파라미터 컬럼값 반환받는다.
		ObservableList<TbpSysDaoFieldsDVO> items = tbParams.getItems();

		Map<String, Object> paramMap = items.stream().filter(vo -> vo.getTestValue() != null && !vo.getTestValue().isEmpty())
				.collect(Collectors.toMap(TbpSysDaoFieldsDVO::getFieldName, new Function<TbpSysDaoFieldsDVO, Object>() {

					@Override
					public Object apply(TbpSysDaoFieldsDVO t) {
						if ("Arrays".equals(t.getType())) {
							String pattern = "'[^']{0,}'";
							List<String> regexMatchs = ValueUtil.regexMatchs(pattern, t.getTestValue(), str -> {
								return str.substring(1, str.length() - 1);
							});
							return regexMatchs;
						}

						return t.getTestValue();
					}
				}));

		SimpleSQLResultView simpleSQLResultView = new SimpleSQLResultView(velocitySQL, paramMap);
		try {
			simpleSQLResultView.show();
			List<TableModelDVO> columns = simpleSQLResultView.getColumns();
			List<TbpSysDaoColumnsDVO> collect = columns.stream().map(vo -> {
				TbpSysDaoColumnsDVO dvo = new TbpSysDaoColumnsDVO();
				dvo.setColumnName(vo.getDatabaseColumnName());
				dvo.setColumnType(vo.getDatabaseTypeName());
				return dvo;
			}).collect(Collectors.toList());

			// if (!this.tbMappings.getItems().isEmpty())
			if (!collect.isEmpty()) {
				this.tbMappings.getItems().clear();
				getSelectedMethodItem().getTbpSysDaoColumnsDVOList().clear();

				this.tbMappings.getItems().addAll(collect);
				getSelectedMethodItem().getTbpSysDaoColumnsDVOList().addAll(collect);
			}

		} catch (IOException e1) {
			LOGGER.error(ValueUtil.toString(e1));
		}

	}
}
