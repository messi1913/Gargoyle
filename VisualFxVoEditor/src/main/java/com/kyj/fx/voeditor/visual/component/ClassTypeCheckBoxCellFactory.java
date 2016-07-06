/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;

import com.kyj.fx.voeditor.visual.main.model.vo.ClassTypeCodeDVO;
import com.kyj.fx.voeditor.visual.momory.ClassTypeResourceLoader;

/**
 * @author KYJ
 *
 */
public class ClassTypeCheckBoxCellFactory
		implements Callback<TableColumn<TableModelDVO, String>, TableCell<TableModelDVO, String>> {

	private static List<ClassTypeCodeDVO> codeList;

	static synchronized void create() {
		codeList = FXCollections.observableArrayList();
		ClassTypeCodeDVO allCode = new ClassTypeCodeDVO();
		allCode.setCommCode("");
		allCode.setCommCodeNm("ALL");
		codeList.add(allCode);
	}

	static List<ClassTypeCodeDVO> getCodeList() {
		if (codeList == null)
			create();

		return codeList;
	}

	static ClassTypeCodeDVO getDVO(String codeNm) {
		Optional<ClassTypeCodeDVO> findFirst = getCodeList().stream().filter(vo -> {
			return vo.getCommCodeNm().equals(codeNm);
		}).findFirst();

		if (findFirst.isPresent())
			return findFirst.get();
		return new ClassTypeCodeDVO();
	}

	static StringConverter<String> value = new StringConverter<String>() {

		@Override
		public String toString(String codeNm) {

			if (codeNm == null)
				return "";

			return codeNm;
		}

		@Override
		public String fromString(String codeNm) {
			return codeNm;
		}
	};

	@Override
	public TableCell<TableModelDVO, String> call(TableColumn<TableModelDVO, String> param) {
		ChoiceBoxTableCell<TableModelDVO, String> choiceBoxTableCell = new ChoiceBoxTableCell<>();
		choiceBoxTableCell.getItems().addAll(ClassTypeResourceLoader.getInstance().getKeyList());
		choiceBoxTableCell.setConverter(value);
		return choiceBoxTableCell;
	}

}
