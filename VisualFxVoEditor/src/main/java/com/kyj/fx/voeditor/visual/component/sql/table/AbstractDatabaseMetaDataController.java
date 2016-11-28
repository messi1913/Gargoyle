/**
 * package : com.kyj.fx.voeditor.visual.component.table
 *	fileName : TableBaseInformationController.java
 *	date      : 2016. 1. 1.
 *	user      : KYJ
 */
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import com.kyj.fx.voeditor.visual.framework.KeyValue;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

/**
 * 테이블에 이름 및 코멘트에 대한 정보를 보여준다.
 *
 * @author KYJ
 *
 */
public abstract class AbstractDatabaseMetaDataController extends AbstractTableInfomation {

	public static final String KEY_DATABASE_MEATADATA = TableInformationFrameView.KEY_DATABASE_MEATADATA;

	private TableInformationFrameView parent;

	@FXML
	private TableView<KeyValue> tbMetadata;

	/**
	 * 생성자 fxml을 로드한다.
	 *
	 * @throws Exception
	 */
	public AbstractDatabaseMetaDataController() throws Exception {
		super(KEY_DATABASE_MEATADATA);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.ItableInformation#
	 * setParentFrame
	 * (com.kyj.fx.voeditor.visual.component.table.TableInformationFrameView)
	 */
	@Override
	public void setParentFrame(TableInformationFrameView tableInformationFrameView) {
		this.parent = tableInformationFrameView;
	}

	@FXML
	public void initialize() {

		tbMetadata.getSelectionModel().setCellSelectionEnabled(true);
		tbMetadata.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// 클립보드 이벤트 install
		FxUtil.installClipboardKeyEvent(tbMetadata);
		// 더블클릭 이벤트 install
		FxUtil.installDoubleClickPopup(FxUtil.POPUP_STYLE.POPUP, tbMetadata);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.kyj.fx.voeditor.visual.component.table.ItableInformation#init()
	 */
	public void init() throws Exception {
		//		TableInformationUserMetadataVO metadata = this.parent.getMetadata();
		//		String databaseName = metadata.getDatabaseName();
		//		String tableName = metadata.getTableName();

		//		if (ValueUtil.isEmpty(tableName)) {
		//			throw new NullPointerException("tableName 이 비었습니다.");
		//		}

		List<KeyValue> listMetadata = listMetadata();
		this.tbMetadata.getItems().setAll(listMetadata);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 11. 28.
	 * @return
	 * @throws Exception
	 */
	private List<KeyValue> listMetadata() throws Exception {
		List<KeyValue> items = new ArrayList<>();
		try (Connection connection = this.parent.getConnection()) {
			DatabaseMetaData metaData = connection.getMetaData();
			Method[] declaredMethods = DatabaseMetaData.class.getDeclaredMethods();
			for (Method m : declaredMethods) {
				Class<?> returnType = m.getReturnType();

				if ((m.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
					if (!m.isAccessible())
						m.setAccessible(true);

					if (m.isAccessible()) {
						if (m.getParameterCount() == 0) {

							KeyValue keyValue = new KeyValue();
							String methodName = m.getName();
							Object value = null;
							if (returnType == java.lang.String.class) {
								value = m.invoke(metaData);
							} else if (returnType.isAssignableFrom(java.lang.Number.class)) {
								value = m.invoke(metaData);
							} else if (returnType == java.lang.Boolean.class) {
								value = m.invoke(metaData);
							} else if (returnType == java.lang.Character.class) {
								value = m.invoke(metaData);
							} else if (returnType == java.util.Properties.class) {
								value = m.invoke(metaData);
								if (value != null)
									value = value.toString();
							} else if (returnType.isAssignableFrom(java.util.Collection.class)) {
								value = m.invoke(metaData);
								if (value != null)
									value = value.toString();
							} else if (returnType.isPrimitive()) {

								try {
									value = m.invoke(metaData);
								} catch (InvocationTargetException e) {
									//NotSupprted
									value = "Not Supported.";
								}
							}

							keyValue.setKey(methodName);
							keyValue.setValue(value);
							items.add(keyValue);
						}
					}
				}

			}
		}
		return items;
	}

	@Override
	public void clear() throws Exception {

	}

	@Override
	public String getDbmsDriver() {
		return this.parent.getDbmsDriver();
	}

}
