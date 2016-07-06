/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.table
 *	작성일   : 2016. 1. 3.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.NumberingCellValueFactory;
import com.kyj.fx.voeditor.visual.component.sql.table.IKeyType.KEY_TYPE;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
public abstract class AbstractTableColumnInformationController extends BorderPane implements ItableInformation {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractTableColumnInformationController.class);;
	private TableInformationFrameView parent;

	@FXML
	private TableView<TableColumnMetaVO> tbColumns;

	@FXML
	private TableColumn<TableColumnMetaVO, KEY_TYPE> colKeyType;

	@FXML
	private TableColumn<TableColumnMetaVO, Integer> colNumber;

	/**
	 * 테이블의 컬럼 구성요소에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_COLUMNS_INFORMATION = TableInformationFrameView.KEY_TABLE_COLUMNS_INFORMATION;

	/**
	 * 기본키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String PRIMAKRY_KEY_IMAGE_NAME = "key_R";
	/**
	 * 멀티키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String MULTI_KEY_IMAGE_NAME = "key_G";
	/**
	 * 외리키인경우 보여줄 이미지
	 *
	 * @최초생성일 2016. 1. 21.
	 */
	private static final String FOREIGN_KEY_IMAGE_NAME = "key_B";

	/**
	 * 생성자 fxml을 로드한다.
	 *
	 * @throws Exception
	 */
	public AbstractTableColumnInformationController() throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(TableInformationFrameView.class.getResource(KEY_TABLE_COLUMNS_INFORMATION));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	@FXML
	public void initialize() {
		colNumber.setCellValueFactory(new NumberingCellValueFactory<TableColumnMetaVO>(tbColumns));

		colKeyType.setCellFactory(param -> new TableCell<TableColumnMetaVO, KEY_TYPE>() {

			@Override
			protected void updateItem(KEY_TYPE item, boolean empty) {
				super.updateItem(item, empty);

				if (empty) {

					setGraphic(null);

				} else {

					ImageView image = null;
					switch (item) {
					case PRI:
						image = getImage(PRIMAKRY_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					case MULTI:
						image = getImage(MULTI_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					case FOREIGN:
						image = getImage(FOREIGN_KEY_IMAGE_NAME);
						image.setFitWidth(getPrefWidth());
						setGraphic(image);
						break;

					default:
						image = new ImageView();
					}

					setGraphic(image);
				}

			}

		});

		colKeyType.setCellValueFactory(new PropertyValueFactory<>("keyType"));
		colKeyType.setStyle("-fx-alignment:center");

	}

	/**
	 * resources패키지로부터 image를 가져오는 처리
	 *
	 * @param name
	 * @return
	 */
	static ImageView getImage(String name) {
		try {
			String name2 = "META-INF/images/keyImages/" + name + ".png";
			InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(name2);
			return new ImageView(new Image(is));
		} catch (Exception e) {

			// not important...
		}

		return new ImageView();
	}

	@Override
	public void setParentFrame(TableInformationFrameView tableInformationFrameView) {
		this.parent = tableInformationFrameView;
	}

	@Override
	public void init() throws Exception {
		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		String databaseName = metadata.getDatabaseName();
		String tableName = metadata.getTableName();

		if (ValueUtil.isEmpty(tableName)) {
			throw new NullPointerException("tableName 이 비었습니다.");
		}

		String sql = getTableColumnsSQL(databaseName, tableName);
		List<TableColumnMetaVO> query = this.parent.query(sql, rowMapper());
		tbColumns.getItems().addAll(query);
	}

	/**
	 * 테이블코멘트정보를 리턴하는 SQL문을 반환
	 *
	 * @return
	 * @throws Exception
	 */
	public abstract String getTableColumnsSQL(String databaseName, String tableName) throws Exception;

	/**
	 * 쿼리 결과에 따란 데이터매퍼를 설정한후 반환한다.
	 *
	 * @return
	 */
	public abstract RowMapper<TableColumnMetaVO> rowMapper();

	@Override
	public void clear() throws Exception {
		tbColumns.getItems().clear();
	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}
}
