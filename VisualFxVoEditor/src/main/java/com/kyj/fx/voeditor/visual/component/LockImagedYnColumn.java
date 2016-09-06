/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 11. 16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.lang.reflect.Field;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/***************************
 * 
 * 값(String)의 상태(Y or N)에 따라 서로다른 이미지를 보여주기 위한 테이블 컬럼
 * 
 * @author KYJ
 *
 * @param <T>
 ***************************/
public class LockImagedYnColumn<T> extends TableColumn<T, String> {

	private static Logger LOGGER = LoggerFactory.getLogger(LockImagedYnColumn.class);
	private String columnName;
	private URL n;
	private URL y;

	public LockImagedYnColumn(@NamedArg("columnName") String columnName) {
		super();

		this.n = ClassLoader.getSystemClassLoader().getResource("META-INF/images/nodeicons/stock_lock_open.png");
		this.y = ClassLoader.getSystemClassLoader().getResource("META-INF/images/nodeicons/stock_lock.png");

		this.columnName = columnName;

		this.setCellFactory(defaultCellFactory);
		this.setCellValueFactory(defaultCellValueFactory);

	}

	private Callback<TableColumn<T, String>, TableCell<T, String>> defaultCellFactory = param -> {
		DaoWizardYnImageCell<T> daoWizardYnImageCell = new DaoWizardYnImageCell<>(y, n);
		return daoWizardYnImageCell;
	};

	private Callback<javafx.scene.control.TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> defaultCellValueFactory = param -> {
		try {
			Field declaredField = param.getValue().getClass().getDeclaredField(columnName);
			Object obj = param.getValue();
			if (declaredField != null) {
				declaredField.setAccessible(true);
				Object object = declaredField.get(obj);

				if (object instanceof StringProperty) {
					StringProperty se = (StringProperty) object;
					return se;
				} else if (object == null)
					return new SimpleStringProperty("N");

				return new SimpleStringProperty("N");
			}

		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return new SimpleStringProperty("N");
	};

	public URL getN() {
		return n;
	}

	public URL getY() {
		return y;
	}

	//	private StringProperty getProperty(Object param) throws Exception {
	//
	//		Field declaredField = param.getClass().getDeclaredField(columnName);
	//		if (declaredField != null) {
	//			declaredField.setAccessible(true);
	//			Object object = declaredField.get(param);
	//
	//			if (object instanceof StringProperty) {
	//				StringProperty se = (StringProperty) object;
	//				return se;
	//			} else if (object == null)
	//				return new SimpleStringProperty("N");
	//
	//			return new SimpleStringProperty("N");
	//		}
	//		return new SimpleStringProperty("N");
	//	}
}
