/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2017. 7. 5.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import java.util.Collections;
import java.util.List;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ObjectUtil;
import com.kyj.javafx.scene.control.skin.CTableView;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;

/**
 * @author KYJ
 *
 */
public class FixedTableViewExam extends Application {

	/**
	 * 
	 */
	public FixedTableViewExam() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		CTableView<TbmSysDaoDVO> root = new CTableView<TbmSysDaoDVO>();
		
		CTableColumn e = new CTableColumn("packageName");
		root.getColumns().add(e);
		root.getColumns().add(new CTableColumn("className"));
		root.getColumns().add(new CTableColumn("location"));
		root.getColumns().add(new CTableColumn("classDesc"));
		root.getColumns().add(new CTableColumn("tableName"));
		root.getColumns().add(new CTableColumn("extendsClassName"));

		
		root.getFixedColumns().add(e);
		root.setFixedCellSize(80.d);
		//		
		List<TbmSysDaoDVO> select = DbUtil.select("select * from tbm_sys_dao", Collections.emptyMap(),
				DbUtil.createBeanRowMapper(TbmSysDaoDVO.class));
		root.getItems().addAll(select);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	static class CTableColumn extends TableColumn<TbmSysDaoDVO, String> {

		public CTableColumn() {
			super();

		}

		public CTableColumn(String fieldName) {
			this(fieldName, fieldName);
		}

		public CTableColumn(String text, String fieldName) {
			super(text);
			setCellFactory(new Callback<TableColumn<TbmSysDaoDVO, String>, TableCell<TbmSysDaoDVO, String>>() {

				@Override
				public TableCell<TbmSysDaoDVO, String> call(TableColumn<TbmSysDaoDVO, String> param) {
					return new TextFieldTableCell<TbmSysDaoDVO, String>();
				}
			});
			setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TbmSysDaoDVO, String>, ObservableValue<String>>() {

				@Override
				public ObservableValue<String> call(CellDataFeatures<TbmSysDaoDVO, String> param) {

					try {
						Object fieldValue = ObjectUtil.getDeclaredFieldValue(param.getValue(), fieldName);
						return (ObservableValue<String>) fieldValue;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return param.getValue().classNameProperty();
				}
			});
		}

	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 7. 5. 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

}
