/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.functions
 *	작성일   : 2016. 4. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.functions;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public interface SQLPaneMotionable<K> {

	abstract List<Map<String, Object>> show100RowAction();

	/**
	 * 트리에서 우클릭 reflesh를 클릭한경우 이벤트 정의
	 *
	 * @param e
	 */
	abstract void menuRefleshOnAction(ActionEvent e);

	/**
	 * 스키마 트리 클릭 이벤트 정의
	 *
	 * @param e
	 */
	abstract void schemaTreeOnMouseClick(MouseEvent e);

	/**
	 * 스키마 트리 키 이벤트 정의
	 *
	 * @param e
	 */
	abstract void schemaTreeOnKeyClick(KeyEvent e);

	/**
	 * 테이블에 대한 상세정보를 조회하기 위한 팝업을 띄운다.
	 *
	 * @param connectionSupplier
	 * @param selectedItem
	 */
	abstract void showProperties(Supplier<Connection> connectionSupplier, K selectedItem);

	/********************************
	 * 작성일 : 2016. 5. 1. 작성자 : KYJ
	 *
	 *
	 * @param connectionSupplier
	 * @param schema
	 * @param tableName
	 ********************************/
	abstract void showProperties(Supplier<Connection> connectionSupplier, String schema, String tableName);

	/**
	 * 테이블뷰 키클릭 이벤트
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 11. 25.
	 * @param e
	 */
	abstract void tbResultOnKeyClick(KeyEvent e);

	/********************************
	 * 작성일 : 2016. 4. 20. 작성자 : KYJ
	 *
	 * 쿼리 실행
	 *
	 * @param query
	 * @param param
	 * @param onSuccess
	 * @param exceptionHandler
	 * @return
	 ********************************/
	abstract List<Map<String, Object>> query(String query, Map<String, Object> param, Consumer<List<Map<String, Object>>> onSuccess,
			BiConsumer<Exception, Boolean> exceptionHandler);

	/********************************
	 * 다건의 쿼리 실행
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 14.
	 * @param queryArray
	 * @param onSuccess
	 * @param exceptionHandler
	 * @return
	 ********************************/
	abstract void queryAll(List<String> queryArray, Consumer<Integer> onSuccess, BiConsumer<Exception, Boolean> exceptionHandler);

	/**
	 * 선택된 트리로부터 테이블명을 리턴
	 *
	 * @param selectItem
	 * @return
	 */
	abstract String getSelectedTreeByTableName(TreeItem<K> selectItem);

	/********************************
	 * 작성일 : 2016. 5. 1. 작성자 : KYJ
	 *
	 * 트리로부터 스키마명 리턴.
	 *
	 * @param selectItem
	 * @return
	 ********************************/
	abstract String getSchemaName(TreeItem<K> selectItem);

	/**
	 * 선택된 트리로부터 컬럼명 리스트를 리턴
	 *
	 * @param selectItem
	 * @return
	 */
	abstract List<String> getSelectedTreeByTableColumns(TreeItem<K> selectItem);

	/**
	 *
	 * 선택된 트리로부터 테이블 키값을 리턴.
	 *
	 * @param selectItem
	 * @return
	 */
	abstract List<String> getSelectedTreeByPrimaryKey(TreeItem<K> selectItem);
	/* [끝][추상화 메소드 정의] */
}
