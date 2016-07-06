/**
 * KYJ
 * 2015. 10. 14.
 */
package com.kyj.fx.voeditor.visual.main.layout;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang.SystemUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.kyj.fx.voeditor.core.VoEditor;
import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.util.EditorUtil;
import com.kyj.fx.voeditor.visual.component.ClassTypeCheckBoxCellFactory;
import com.kyj.fx.voeditor.visual.component.CommonsContextMenu;
import com.kyj.fx.voeditor.visual.component.Menus;
import com.kyj.fx.voeditor.visual.component.popup.DatabaseTableView;
import com.kyj.fx.voeditor.visual.component.popup.JavaTextView;
import com.kyj.fx.voeditor.visual.events.CommonContextMenuEvent;
import com.kyj.fx.voeditor.visual.exceptions.FileAlreadyExistException;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.ExcelUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.fx.voeditor.visual.util.VoEditorConverter;
import com.kyj.fx.voeditor.visual.util.VoWizardUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;
import kyj.Fx.dao.wizard.core.model.vo.TableDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableMasterDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;

/**
 * Vo에디터 컨트롤러 클래스
 *
 * @author KYJ
 *
 */
@FXMLController(value = "VoEditorView.fxml")
public class VoEditorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(VoEditorController.class);

	@FXML
	private TableView<TableModelDVO> tbVoEditor;

	@FXML
	private Button btnGenerate;

	@FXML
	private Button btnSelect;

	@FXML
	private Button btnDatabase;

	@FXML
	private TextField txtPackageName;

	@FXML
	private TextField txtClassName;

	@FXML
	private TextField txtLocation;

	@FXML
	private TextArea txtAreaDesc;

	@FXML
	private TableColumn<TableModelDVO, String> colName;

	@FXML
	private TableColumn<TableModelDVO, String> colType;

	@FXML
	private TableColumn<TableModelDVO, String> colSize;

	@FXML
	private TableColumn<TableModelDVO, String> colDesc;

	@FXML
	private CheckBox chkWriteThenOpen;

	/**
	 * 이벤트 초기화
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 */
	@FXML
	public void initialize() {
		colType.setCellFactory(new ClassTypeCheckBoxCellFactory());
		colName.setCellFactory(TextFieldTableCell.forTableColumn());
		colSize.setCellFactory(TextFieldTableCell.forTableColumn());
		colDesc.setCellFactory(TextFieldTableCell.forTableColumn());

		CommonsContextMenu.addMenus(tbVoEditor, (Menus.useCudButtons() | Menus.UP | Menus.DOWN));
		// tbVoEditor.setContextMenu();

		tbVoEditor.addEventHandler(ActionEvent.ACTION, event -> {
			if (event instanceof CommonContextMenuEvent) {
				CommonContextMenuEvent _event = (CommonContextMenuEvent) event;
				int mode = _event.getMode();

				ObservableList<TableModelDVO> items = tbVoEditor.getItems();

				int selectedIndex = tbVoEditor.getSelectionModel().getSelectedIndex();
				int rowSize = items.size();
				boolean isMove = false;

				if (Menus.isAdd(mode)) {
					items.add(defaultTableModelDVO());
					isMove = true;
				} else if (Menus.isDelete(mode)) {
					items.remove(selectedIndex);
					isMove = true;
				} else if (Menus.isUp(mode)) {
					btnUpOnMouseClick();
				} else if (Menus.isDown(mode)) {
					btnDownOnMouseClick();
				}

				if (isMove) {
					tbVoEditor.getSelectionModel().select(rowSize - 1);
				}

			}

		});

		// 컨트롤 키를 누르면 그리드 데이터 이동처리.
		tbVoEditor.setOnKeyPressed(event -> {

			if (event.isControlDown()) {

				KeyCode code = event.getCode();

				switch (code) {
				case UP:
					btnUpOnMouseClick();
					break;
				case DOWN:
					btnDownOnMouseClick();
					break;
				default:
					break;
				}

				event.consume();
			}
		});

		btnGenerate.addEventHandler(MouseEvent.MOUSE_CLICKED, this::btnGenerateOnMouseClick);

		btnSelect.setOnMouseClicked(this::btnSelectOnMouseClick);

		btnDatabase.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				DatabaseTableView databaseTableView = new DatabaseTableView();
				TableDVO show = databaseTableView.show();
				if (show != null) {

					TableMasterDVO tableMasterDVO = show.getTableMasterDVO();
					List<TableModelDVO> tableModelDVOList = show.getTableModelDVOList();
					ObservableList<TableModelDVO> items = tbVoEditor.getItems();

					if (tableMasterDVO != null) {
						txtClassName.setText(tableMasterDVO.getClassName());
						txtAreaDesc.setText(tableMasterDVO.getDescription());
					}
					if (tableModelDVOList != null) {
						items.clear();
						items.addAll(tableModelDVOList);
					}

				}
			}
		});
	}

	/********************************
	 * 작성일 : 2016. 6. 6. 작성자 : KYJ
	 *
	 * 생성 마우스 클릭
	 *
	 * @param me
	 ********************************/
	public void btnGenerateOnMouseClick(MouseEvent me) {

		try {
			checkValidation();

			String className = txtClassName.getText();
			String location = txtLocation.getText();
			String packageName = txtPackageName.getText();
			String extendsBaseClass = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.VOEDITOR_DEFAULT_EXTENDS_CLASS);

			ObservableList<TableModelDVO> items = tbVoEditor.getItems();
			ClassMeta classMeta = EditorUtil.extractedClassMeta(className, packageName, extendsBaseClass);

			VoEditorConverter converter = new VoEditorConverter(classMeta, items);
			VoEditor voEditor = converter.convert();

			if (location != null && !location.isEmpty()) {
				Optional<Pair<String, String>> showYesOrNoDialog = DialogUtil.showYesOrNoDialog("파일생성여부", "파일도 만드시겠습니까? ");
				showYesOrNoDialog.ifPresent(string -> {
					if ("Y".equals(string.getValue())) {
						try {
							// String fileName =
							// ValueUtil.getIndexcase(className, 0,
							// IndexCaseTypes.UPPERCASE);
							voEditor.toFile(location);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

			JavaTextView simpleTextView = new JavaTextView(voEditor.toText());
			simpleTextView.show();

		} catch (Exception e) {
			DialogUtil.showExceptionDailog(e);
			return;
		}

	}

	/********************************
	 * 작성일 : 2016. 6. 6. 작성자 : KYJ
	 *
	 * Browse 마우스 클릭 이벤트
	 *
	 * @param e
	 ********************************/
	public void btnSelectOnMouseClick(MouseEvent e) {

		/* 해당 위치에 존재하는 다이얼로그를 오픈함. */

		//
		// File file = new File(locationDir);
		// boolean browseFile = FileUtil.browseFile(file);
		// if (!browseFile)
		// DialogUtil.showMessageDialog("열기에 실패했습니다.");

		File selectedDir = DialogUtil.showDirectoryDialog(SharedMemory.getPrimaryStage(), chooser -> {

			String locationDir = txtLocation.getText();

			if (ValueUtil.isEmpty(locationDir)) {
				locationDir = System.getProperty("user.dir");
			}

			chooser.setInitialDirectory(new File(locationDir));
		});

		if (selectedDir != null && selectedDir.exists()) {
			txtLocation.setText(selectedDir.getAbsolutePath());
		}

	}

	/********************************
	 * 작성일 : 2016. 2. 27. 작성자 : KYJ
	 *
	 * 내용 : 데이터 항목 추가.
	 *
	 * @param items
	 *******************************/
	public void addItem(List<TableModelDVO> items) {
		this.tbVoEditor.getItems().addAll(items);
	}

	@FXML
	public void btnUpOnMouseClick() {
		int selectedIndex = tbVoEditor.getSelectionModel().getSelectedIndex();
		if (selectedIndex <= 0)
			return;
		replace(selectedIndex - 1, selectedIndex);
		tbVoEditor.getSelectionModel().select(selectedIndex - 1);
	}

	@FXML
	public void btnDownOnMouseClick() {
		int selectedIndex = tbVoEditor.getSelectionModel().getSelectedIndex();
		int rowSize = tbVoEditor.getItems().size();
		if (selectedIndex + 1 >= rowSize)
			return;
		replace(selectedIndex, selectedIndex + 1);
		tbVoEditor.getSelectionModel().select(selectedIndex + 1);

	}

	private void replace(int idx1, int idx2) {

		TableModelDVO tableModelDVO1 = tbVoEditor.getItems().get(idx1);
		TableModelDVO tableModelDVO2 = tbVoEditor.getItems().get(idx2);

		tbVoEditor.getItems().set(idx2, tableModelDVO1);
		tbVoEditor.getItems().set(idx1, tableModelDVO2);
	}

	/**
	 * 디폴트 TableModelDVO 반환
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 * @return
	 */
	private TableModelDVO defaultTableModelDVO() {
		TableModelDVO tableModelDVO = new TableModelDVO();
		String typeName = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.VOEDITOR_DEFAULT_TYPE_NAME);
		tableModelDVO.setType(typeName);
		return tableModelDVO;
	}

	/**
	 * 유효성 검증
	 *
	 * @param tableMasterDVO
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 */
	private void checkValidation() {
		String className = txtClassName.getText();

		if (isEmpty(className)) {
			throw new IllegalArgumentException("Class Name이 비어있습니다.");
		}

		tbVoEditor.getItems().forEach(vo -> {
			if (isEmpty(vo.getName()))
				throw new IllegalArgumentException("그리드에 Name에 빈값이 있습니다.");

			if (isEmpty(vo.getType()))
				throw new IllegalArgumentException("그리드에 Type에 빈값이 있습니다.");

		});
	}

	/**
	 * 비어있는지 여부 체크
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
		if (str == null || str.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 엑셀 export
	 *
	 * @param e
	 */
	@FXML
	public void btnExcelExportOnMouseClick(MouseEvent e) {

		File saveFile = DialogUtil.showFileSaveCheckDialog(SharedMemory.getPrimaryStage(), new Consumer<FileChooser>() {

			@Override
			public void accept(FileChooser choser) {

				String fileName = txtClassName.getText();
				File dir = SystemUtils.getUserDir();
				choser.setInitialFileName(fileName);
				choser.setInitialDirectory(dir);
				choser.getExtensionFilters().add(new ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
			}
		});
		boolean isSuccess = false;
		if (saveFile != null) {
			try {
				File createExcelFile = VoWizardUtil.createExcelFile(saveFile.getParentFile(), saveFile.getName(), tbVoEditor.getItems(),
						true);

				if (createExcelFile != null && createExcelFile.exists()) {
					isSuccess = true;
				}
			} catch (FileAlreadyExistException e1) {
				ValueUtil.toString(e1);
				DialogUtil.showExceptionDailog(e1);
				return;
			}
		}

		if (isSuccess) {
			if (chkWriteThenOpen.isSelected()) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(saveFile);
					} catch (IOException e1) {
						DialogUtil.showExceptionDailog(e1);
					}
				}
			}

		}

	}

	@FXML
	public void btnExcelImporOnMouseClick() {
		File selectFile = DialogUtil.showFileDialog(SharedMemory.getPrimaryStage(), new Consumer<FileChooser>() {

			@Override
			public void accept(FileChooser choser) {
				String fileName = txtClassName.getText();
				File dir = SystemUtils.getUserDir();
				choser.setInitialFileName(fileName);
				choser.setInitialDirectory(dir);
				choser.getExtensionFilters().add(new ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
			}
		});

		if (selectFile != null && selectFile.exists()) {
			try {
				String simpleName = selectFile.getName();
				String className = simpleName.substring(0, simpleName.indexOf("."));
				txtClassName.setText(className);

				// TODO 좀 더 깔끔한 방법으로 작성하는걸 고려.. ex) template기반
				List<TableModelDVO> list = ExcelUtil.toK(selectFile, new BiFunction<File, Workbook, List<TableModelDVO>>() {

					@Override
					public List<TableModelDVO> apply(File file, Workbook xlsx) {
						List<TableModelDVO> llist = new ArrayList<>();

						Sheet sheetAt = xlsx.getSheetAt(0);

						// 헤더부 처리
						// Row columnRow = sheetAt.getRow(2);
						// Cell _column = columnRow.getCell(0);
						// Cell _type = columnRow.getCell(1);
						// Cell _size = columnRow.getCell(2);
						// Cell _comment = columnRow.getCell(3);

						// 데이터부 처리
						for (int _row = 3; _row <= sheetAt.getLastRowNum(); _row++) {
							Row row = sheetAt.getRow(_row);
							Cell column = row.getCell(0);
							Cell type = row.getCell(1);
							Cell size = row.getCell(2);
							Cell comment = row.getCell(3);

							TableModelDVO modelDVO = new TableModelDVO();
							modelDVO.setName(column.getStringCellValue());
							modelDVO.setType(type.getStringCellValue());
							modelDVO.setSize(size.getStringCellValue());
							modelDVO.setDesc(comment.getStringCellValue());
							llist.add(modelDVO);
						}

						return llist;
					}

				});
				tbVoEditor.getItems().clear();
				tbVoEditor.getItems().addAll(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/********************************
	 * 작성일 : 2016. 7. 4. 작성자 : KYJ
	 *
	 *
	 * @param voFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 ********************************/
	public void setFromFile(File voFile) {
		if (voFile == null || !voFile.exists())
			return;

		if (voFile.isFile()) {

			CompilationUnit cu;
			try (FileInputStream in = new FileInputStream(voFile)) {
				// parse the file
				cu = JavaParser.parse(in);
				PackageDeclaration packageDeclaration = cu.getPackage();

				txtPackageName.setText(packageDeclaration.getName().toString());
				txtClassName.setText(voFile.getName().substring(0, voFile.getName().indexOf('.')));
				txtLocation.setText(voFile.getAbsolutePath());

				MethodVisitor methodVisitor = new MethodVisitor();
				methodVisitor.visit(cu, null);

				List<TableModelDVO> valideFieldMeta = methodVisitor.getValideFieldMeta(t -> {
					TableModelDVO tableModelDVO = new TableModelDVO();
					tableModelDVO.setName(t.getName());
					tableModelDVO.setType(t.getFieldType().getSimpleName());
					return tableModelDVO;
				});

				this.tbVoEditor.getItems().addAll(valideFieldMeta);
			} catch (IOException | ParseException e) {
				LOGGER.error(ValueUtil.toString(e));
			}

		} else {
			txtLocation.setText(voFile.getAbsolutePath());
		}

	}

	/**
	 * 메타정보를 콜렉팅
	 *
	 * @author KYJ
	 *
	 */
	private static class MethodVisitor extends VoidVisitorAdapter<Object> {

		private static final Integer INIT_PAIR_VALUE = 0;

		private Map<String, Pair<FieldMeta, Integer>> fieldMes = new HashMap<>();

		public void visit(MethodDeclaration n, Object arg) {

			String name = n.getName();

			if (name.startsWith("set") || name.startsWith("get")) {
				String fieldName = ValueUtil.getIndexLowercase(name.substring(3, name.length()), 0);
				Pair<FieldMeta, Integer> pair = fieldMes.get(fieldName);
				int count = pair.getValue().intValue() + 1;
				Pair<FieldMeta, Integer> createPair = createPair(pair.getKey(), count);
				fieldMes.put(fieldName, createPair);
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Object arg) {

			Type type = n.getType();

			if (type instanceof ReferenceType) {
				ReferenceType refType = (ReferenceType) type;
				if (refType.getType() instanceof ClassOrInterfaceType) {
					ClassOrInterfaceType cOrInterfaceType = (ClassOrInterfaceType) refType.getType();
					if (cOrInterfaceType == null)
						return;
					int modifiers = n.getModifiers();
					List<VariableDeclarator> variables = n.getVariables();
					String fieldName = null;
					if (variables != null && variables.size() == 1) {
						fieldName = variables.get(0).toString();
					}

					if (fieldName == null)
						return;

					FieldMeta fieldMeta = ClassTypeResourceLoader.getInstance().get(cOrInterfaceType.getName(),
							err -> LOGGER.error(ValueUtil.toString(err)));


					if (fieldMeta == null) {
						super.visit(n, arg);
						return;
					}

					fieldMeta.setName(fieldName);
					fieldMeta.setModifier(modifiers);
					// fieldMeta.setFieldType(fieldType);

					fieldMes.put(fieldName, createPair(fieldMeta, INIT_PAIR_VALUE));

				}
			}
			super.visit(n, arg);
		}

		private Pair<FieldMeta, Integer> createPair(FieldMeta fieldMeta, Integer num) {
			return new Pair<FieldMeta, Integer>(fieldMeta, num);
		}

		public List<FieldMeta> getValideFieldMeta() {
			return getValideFieldMeta((m, c) -> c >= 2, t -> t);
		}

		public <T> List<T> getValideFieldMeta(Function<FieldMeta, T> cast) {
			return getValideFieldMeta((m, c) -> c >= 2, cast);
		}

		public <T> List<T> getValideFieldMeta(BiPredicate<FieldMeta, Integer> isValide, Function<FieldMeta, T> cast) {
			ArrayList<T> arrayList = new ArrayList<>(fieldMes.size());

			Iterator<Entry<String, Pair<FieldMeta, Integer>>> iterator = fieldMes.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, Pair<FieldMeta, Integer>> next = iterator.next();
				Pair<FieldMeta, Integer> value = next.getValue();
				if (isValide.test(value.getKey(), value.getValue())) {
					arrayList.add(cast.apply(value.getKey()));
				}
			}

			return arrayList;
		}
	}

}