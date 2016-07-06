/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 2. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.awt.Color;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.core.VoEditor;
import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.visual.excels.base.ExcelDataDVO;
import com.kyj.fx.voeditor.visual.excels.base.ExcelSVO;
import com.kyj.fx.voeditor.visual.exceptions.FileAlreadyExistException;
import com.kyj.fx.voeditor.visual.functions.DatabaseTypeMappingFunction;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;

import kyj.Fx.dao.wizard.core.model.vo.TableMasterDVO;
import kyj.Fx.dao.wizard.core.model.vo.TableModelDVO;

/**
 * VO Wizard를 만들기 쉽게 도와주는 유틸리티클래스
 *
 * @author KYJ
 *
 */
public class VoWizardUtil {

	/**
	 * VO에디터가 생성하는 시트명
	 */
	public static final String SHEET_NAME = "voeditor";

	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String DATA_LENGTH = "DATA_LENGTH";
	public static final String COMMENTS = "COMMENTS";
	public static final String PK = "PK";
	public static final String TYPE = "TYPE";
	public static final String COLUMN_NAME = "COLUMN_NAME";

	/**
	 * 테이블모델생성
	 *
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static List<TableMasterDVO> listTable(String tableName) throws Exception {
		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_TABLES);

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("tableName", tableName);
		return DbUtil.select(sql, hashMap, new RowMapper<TableMasterDVO>() {

			@Override
			public TableMasterDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TableMasterDVO tableMasterDVO = new TableMasterDVO();
				String tableName = rs.getString(TABLE_NAME);
				tableMasterDVO.setTableName(tableName);
				String className = ValueUtil.toDVOName(tableName);
				tableMasterDVO.setClassName(className);
				tableMasterDVO.setDescription(rs.getString(COMMENTS));
				return tableMasterDVO;
			}
		});
	}

	/**
	 * 테이블컬럼모델생성
	 *
	 * @param selectedItem
	 * @return
	 * @throws Exception
	 */
	public static List<TableModelDVO> listColumns(TableMasterDVO selectedItem) throws Exception {
		DatabaseTypeMappingFunction databaseTypeMappingFunction = new DatabaseTypeMappingFunction();

		String tableName = selectedItem.getTableName();
		String sql = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.SQL_COLUMN);

		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("tableName", tableName);
		return DbUtil.select(sql, hashMap, new RowMapper<TableModelDVO>() {

			@Override
			public TableModelDVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				TableModelDVO dvo = new TableModelDVO();

				String columnName = rs.getString(COLUMN_NAME);
				dvo.setName(ValueUtil.getPrefixLowerTextMyEdit(columnName));
				dvo.setDatabaseColumnName(columnName);

				dvo.setDatabaseColumnName(columnName);
				String typeName = rs.getString(TYPE);
				dvo.setDabaseTypeName(typeName);

//				rs.getInt(columnLabel)
//				rs.getMetaData().getColumnTypeName()
				dvo.setType( databaseTypeMappingFunction.apply(typeName));
				dvo.setPk(rs.getString(PK));
				dvo.setDesc(rs.getString(COMMENTS));
				dvo.setSize(rs.getString(DATA_LENGTH));
				return dvo;
			}
		});
	}

	/**
	 * VO문자열로 리턴
	 *
	 * @param classMeta
	 * @param models
	 * @return
	 * @throws Exception
	 */
	public static String toVoString(ClassMeta classMeta, List<TableModelDVO> models) throws Exception {
		VoEditorConverter converter = new VoEditorConverter(classMeta, models);
		VoEditor voEditor = converter.convert();
		return voEditor.toText();
	}

	/**
	 * 엑셀파일생성
	 *
	 * @param pathDir
	 * @param models
	 * @return 생성된 엑셀파일에 대한 파일 객체
	 * @throws FileAlreadyExistException
	 */
	public static File createExcelFile(File pathDir, List<TableModelDVO> models) throws FileAlreadyExistException {
		// 고유한 id 생성
		String fileNameId = IdGenUtil.generate();
		return createExcelFile(pathDir, fileNameId, models, false);
	}

	/**
	 * 엑셀파일생성
	 *
	 * @param pathDir
	 * @param _fileName
	 * @param models
	 * @return 생성된 엑셀파일에 대한 파일 객체
	 * @throws FileAlreadyExistException
	 */
	public static File createExcelFile(File pathDir, String _fileName, List<TableModelDVO> models, boolean isOverWrite)
			throws FileAlreadyExistException {

		File saveFile = null;
		if (!pathDir.exists() || !pathDir.isDirectory())
			return saveFile;
		String fileName = _fileName;
		String createFilePathName = pathDir.getAbsolutePath() + File.separator + fileName;
		if (!_fileName.endsWith(".xlsx")) {
			createFilePathName.concat(".xlsx");
		}

		saveFile = new File(createFilePathName);

		if (saveFile.exists() && !isOverWrite) {
			throw new FileAlreadyExistException("already exists. file");
		}
		// 로우번호 채번링
		AtomicInteger atomicInteger = new AtomicInteger(2);

		List<ExcelDataDVO> collect = models.stream().map(vo -> {

			/* 컬럼데이터 처리 */
			List<ExcelDataDVO> list = new ArrayList<ExcelDataDVO>();
			String columnName = vo.getDatabaseColumnName();
			String dataType = vo.getType();
			String dataSize = vo.getSize();
			String desc = vo.getDesc();
			int row = atomicInteger.getAndIncrement();

			list.add(new ExcelDataDVO(row, 0, ValueUtil.toCamelCase(ValueUtil.decode(columnName, columnName, vo.getName()).toString())));
			list.add(new ExcelDataDVO(row, 1, dataType));
			list.add(new ExcelDataDVO(row, 2, dataSize));
			list.add(new ExcelDataDVO(row, 3, desc));
			return list;
		}).collect(() -> {
			/* 컬럼헤더처리 */
			ArrayList<ExcelDataDVO> arrayList = new ArrayList<ExcelDataDVO>();
			arrayList.add(new ExcelDataDVO(1, 0, VoWizardUtil.COLUMN_NAME/* "컬럼명" */, Color.GREEN));
			arrayList.add(new ExcelDataDVO(1, 1, VoWizardUtil.TYPE/* "데이터타입" */, Color.GREEN));
			arrayList.add(new ExcelDataDVO(1, 2, VoWizardUtil.DATA_LENGTH/* "데이터사이즈" */, Color.GREEN));
			arrayList.add(new ExcelDataDVO(1, 3, VoWizardUtil.COMMENTS/* "설명" */, Color.GREEN));
			return arrayList;
		}, (t, u) -> t.addAll(u), (t, u) -> t.addAll(u));

		ExcelSVO svo = new ExcelSVO();
		svo.addSheetExcelDVO(SHEET_NAME, collect);

		try {
			ExcelUtil.createExcel(saveFile.getAbsolutePath(), svo, false);
		} catch (Exception e1) {
			ValueUtil.toString(e1);
			saveFile = null;
		}
		return saveFile;
	}

}
