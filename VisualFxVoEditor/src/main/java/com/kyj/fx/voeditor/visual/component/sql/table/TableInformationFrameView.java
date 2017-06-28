/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.table
 *	작성일   : 2015. 12. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.component.sql.view.CommonDatabaseMetadataController;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonTableBaseInformationController;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonTableColumnInformationController;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.component.sql.view.CommonTableIndexInformationController;
import com.kyj.fx.voeditor.visual.component.sql.view.DerbyTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.component.sql.view.PostgreTableCreateCodeInformationController;
import com.kyj.fx.voeditor.visual.exceptions.NotSupportException;
import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * 테이블에 대한 정보를 보여주는 뷰이다.
 *
 * 컬럼, 스키마, 인덱스 정보를 보여준다.
 *
 * @author KYJ
 *
 */
public class TableInformationFrameView extends BorderPane {

	private static Logger LOGGER = LoggerFactory.getLogger(TableInformationFrameView.class);

	private static final String TABLE_INFORMATION_FRAME_VIEW_FXML = "TableInformationFrameView.fxml";
	/**
	 * 테이블 create문에 대한 텍스트를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_CREATE_CODE_INFORMATION = "TableCreateCodeInformation.fxml";
	/**
	 * 테이블 index에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_INDEX_INFOMATION = "TableIndexInfomation.fxml";
	/**
	 * 테이블 이름 및 코멘트에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_BASE_INFORMATION = "TableBaseInfomation.fxml";
	/**
	 * 테이블의 컬럼 구성요소에 대한 정보를 보여주는 fxml이자 키값
	 */
	public static final String KEY_TABLE_COLUMNS_INFORMATION = "TableColumnsInfomationView.fxml";

	/**
	 * 데이터베이스에 대한 메타데이터를 조회하기 위한 뷰
	 * @최초생성일 2016. 11. 28.
	 */
	public static final String KEY_DATABASE_MEATADATA = "DatabaseMetatadataView.fxml";

	/**
	 * 기능처리
	 */
	private TableInformationFrameManager manager;

	/**
	 * 스플리터 컨텐츠가 담길 메인 프레임
	 *
	 * @최초생성일 2015. 12. 31.
	 */
	@FXML
	private SplitPane splitMainContent;

	/**
	 * 각종 테이블에 대한 여러 메타정보가 담길 tab
	 *
	 * @최초생성일 2015. 12. 31.
	 */
	@FXML
	private TabPane tabInformations;

	/**
	 * 테이블 컬럼정보가 담길 패널
	 *
	 * @최초생성일 2015. 12. 31.
	 */
	@FXML
	private BorderPane borTableColumnInformation;

	private List<ItableInformation> graphicsItems;

	/**
	 * 사용된 jdbc Driver명
	 *
	 * @최초생성일 2016. 1. 5.
	 */
	private String driver;

	/**
	 * 생성자.
	 *
	 * @param connectionSupplier
	 *            데이터베이스 커넥션정보를 리턴.
	 * @throws IOException
	 */
	public TableInformationFrameView(Supplier<Connection> connectionSupplier, Supplier<TableInformationUserMetadataVO> metadata)
			throws IOException {
		manager = new TableInformationFrameManager();
		manager.setConnectionSupplier(connectionSupplier);
		manager.setMetadata(metadata);
		graphicsItems = new ArrayList<>();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(TableInformationFrameView.class.getResource(TABLE_INFORMATION_FRAME_VIEW_FXML));
		loader.setRoot(this);
		loader.setController(this);
		loader.load();

	}

	/**
	 * Frame에서 처리할 UI 컴포넌트들을 생성하고 로드한다.
	 *
	 * @throws Exception
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 31.
	 */
	@FXML
	public void initialize() throws Exception {

		// 테이블에 대한 이름 및 코멘트 정보
		{
			AbstractTableInfomation informationPane = new CommonTableBaseInformationController();
			informationPane.setParentFrame(this);
			try {
				informationPane.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}

			graphicsItems.add(informationPane);
			Tab e = new Tab("기본", informationPane);
			e.setId(KEY_TABLE_BASE_INFORMATION);
			tabInformations.getTabs().add(e);
		}

		// 테이블에 대한 컬럼정보
		{
			AbstractTableInfomation columnPane = new CommonTableColumnInformationController();
			columnPane.setParentFrame(this);

			try {
				columnPane.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			graphicsItems.add(columnPane);
			columnPane.setId(KEY_TABLE_BASE_INFORMATION);
			borTableColumnInformation.setCenter(columnPane);
		}

		// 테이블에 속한 인덱스에 대한 정보

		{
			AbstractTableInfomation indexPane = new CommonTableIndexInformationController();
			indexPane.setParentFrame(this);

			try {
				indexPane.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
			graphicsItems.add(indexPane);
			Tab e = new Tab("인덱스", indexPane);
			e.setId(KEY_TABLE_INDEX_INFOMATION);
			tabInformations.getTabs().add(e);
		}

		// 테이블에 대한 CREATE문에 대한 정보
		{
			AbstractTableInfomation createCodePane = new CommonTableCreateCodeInformationController();

			if (ResourceLoader.ORG_POSTGRESQL_DRIVER.equals(getDbmsDriver())) {
				createCodePane = new PostgreTableCreateCodeInformationController();
			} else if (ResourceLoader.ORG_APACHE_DERBY_JDBC.equals(getDbmsDriver())) {
				createCodePane = new DerbyTableCreateCodeInformationController();
			} else {
				createCodePane = new CommonTableCreateCodeInformationController();
			}
			createCodePane.setParentFrame(this);

			try {
				createCodePane.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));

			}
			graphicsItems.add(createCodePane);
			Tab e = new Tab("CREATE 코드", createCodePane);
			e.setId(KEY_TABLE_CREATE_CODE_INFORMATION);
			tabInformations.getTabs().add(e);
		}

		// 데이터베이스 메타데이터에 대한 정보
		{
			AbstractTableInfomation createCodePane = new CommonDatabaseMetadataController();
			createCodePane.setParentFrame(this);

			try {
				createCodePane.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));

			}
			graphicsItems.add(createCodePane);
			Tab e = new Tab("메타데이터", createCodePane);
			e.setId(KEY_DATABASE_MEATADATA);
			tabInformations.getTabs().add(e);
		}

		// 테이블에 속한 컬럼에 대한 정보
		// BorderPane columnInformationPane =
		// loader(KEY_TABLE_COLUMNS_INFORMATION);
		// borTableColumnInformation.setCenter(columnInformationPane);

		// 테이블에 속한 인덱스에 대한 정보
		// BorderPane indexInformationPane = loader(KEY_TABLE_INDEX_INFOMATION);
		// tabInformations.getTabs().add(new Tab("인덱스", indexInformationPane));

		// 테이블에 대한 CREATE문에 대한 정보
		// BorderPane createTableCodePane =
		// loader(KEY_TABLE_CREATE_CODE_INFORMATION);
		// createTableCodePane.setPadding(new Insets(5.0));
		// tabInformations.getTabs().add(new Tab("CREATE 코드",
		// createTableCodePane));

	}

	/**
	 * TableInformationFrameView.class 패키지에 속한 FXML 로더
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 31.
	 * @param fxml
	 * @return
	 * @throws IOException
	 */
	private <T> T loader(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(TableInformationFrameView.class.getResource(fxml));
		return loader.load();
	}

	/**
	 * 사용자 데이터 리턴
	 *
	 * @return
	 */
	public TableInformationUserMetadataVO getMetadata() {
		return manager.getMetadata().get();
	}

	/**
	 * 데이터베이스 커넥션 리턴
	 *
	 * @return
	 */
	public Connection getConnection() {

		Connection connection = manager.getConnectionSupplier().get();
		try {
			if (driver == null)
				driver = DbUtil.getDriverNameByConnection(connection);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
			// Nothing.....
		}
		return connection;
	}

	/**
	 * 사용된 jdbc Driver명
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 5.
	 * @return
	 */
	public String getDbmsDriver() {
		if (driver == null) {
			try (Connection con = getConnection()) {
				driver = DbUtil.getDriverNameByConnection(con);
			} catch (SQLException | NotSupportException e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		}
		return driver;
	}

	/**
	 * sql문 조회후 결과 리턴
	 *
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> query(String sql) throws Exception {
		List<Map<String, Object>> query = Collections.emptyList();
		try (Connection connection = this.getConnection()) {
			query = manager.query(connection, sql);
		}
		return query;
	}

	public List<Map<String, Object>> query(String sql, BiFunction<ResultSetMetaData, ResultSet, List<Map<String, Object>>> converter)
			throws Exception {
		List<Map<String, Object>> query = Collections.emptyList();
		try (Connection connection = this.getConnection()) {
			query = manager.query(connection, sql, converter);
		}
		return query;
	}

	public <T> T queryForMeta(Function<DatabaseMetaData, T> converter) throws Exception {
		T query = null;
		try (Connection connection = this.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			query = manager.queryForMeta(metaData, converter);
		}
		return query;
	}

	/**
	 * sql문 조회후 결과 리턴. 매핑처리.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 * @param sql
	 * @param mapper
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> query(String sql, RowMapper<T> mapper) throws Exception {
		List<T> query = Collections.emptyList();
		try (Connection connection = this.getConnection()) {
			query = manager.query(connection, sql, mapper);
		}
		return query;
	}

	/**
	 * 리로드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 4.
	 */
	@FXML
	public void btnReloadOnMouseClick() {
		graphicsItems.forEach(item -> {
			try {
				item.clear();
				item.init();
			} catch (Exception e) {
				LOGGER.error(ValueUtil.toString(e));
			}
		});
	}

}
