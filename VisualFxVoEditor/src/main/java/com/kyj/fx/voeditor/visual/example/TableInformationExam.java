/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2015. 12. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.sql.Connection;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.kyj.fx.voeditor.visual.component.sql.table.TableInformationFrameView;
import com.kyj.fx.voeditor.visual.component.sql.table.TableInformationUserMetadataVO;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.FxClipboardUtil;

/**
 * 테이블에 대한 정보를 보기위한 example 프로그램.
 *
 * sql editor에서 해당 뷰를 팝업형태로 불러서 조회할 수 있게 만듬.
 *
 * @author KYJ
 *
 */
public class TableInformationExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 12. 31.
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 여러가지 dbms가 사용가능하게 처리하기때문에 Connection을 리턴하는 Supplier 인터페이스를 구현.
	 */
	private Supplier<Connection> suppler = () -> {
		try {
			return DbUtil.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	};

	/**
	 * 조회하고자하는 테이블에 대한 메타정보를 정의한후 리턴해준다.
	 */
	private Supplier<TableInformationUserMetadataVO> metadata = () -> {
		try {

			TableInformationUserMetadataVO metadataVO = new TableInformationUserMetadataVO();
			// 조회하고자하는 테이블명을 기술한다.
			metadataVO.setTableName("tbm_sys_dao");
			// 조회하고자하는 테이블 dbms를 기술한다.
			metadataVO.setDatabaseName("meerkat");
			return metadataVO;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	};

	@Override
	public void start(Stage primaryStage) throws Exception {

		// 테스트하고자하는 뷰를 정의한다.
		TableInformationFrameView view = new TableInformationFrameView(suppler, metadata);
		Scene scene = new Scene(view);
		primaryStage.setScene(scene);
		primaryStage.show();


		System.out.println(FxClipboardUtil.pastImage());
	}

}
