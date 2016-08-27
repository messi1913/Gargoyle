/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 19.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import javafx.beans.property.SimpleStringProperty;
/**
 * @author KYJ
 *
 */
import javafx.beans.property.StringProperty;

public class TbpSysDaoColumnsDVO {
	private StringProperty columnName;
	/**
	 * 데이터베이스 디비 타입
	 *
	 * databse.type.mapping 파일참조.
	 *
	 * @최초생성일 2016. 4. 16.
	 */
	private StringProperty columnType;

	/**
	 * 프로그램 데이터 타입
	 * @최초생성일 2016. 8. 26.
	 */
	private StringProperty programType;

	private StringProperty lockYn;

	public TbpSysDaoColumnsDVO() {
		this.columnName = new SimpleStringProperty();
		this.columnType = new SimpleStringProperty();
		this.programType = new SimpleStringProperty();
		this.lockYn = new SimpleStringProperty();
	}

	public void setColumnName(String columnName) {
		this.columnName.set(columnName);
	}

	public String getColumnName() {
		return columnName.get();
	}

	public StringProperty columnNameProperty() {
		return columnName;
	}

	/********************************
	 * 작성일 : 2016. 4. 16. 작성자 : KYJ 데이터베이스 디비타입.
	 *
	 * @param columnType
	 ********************************/
	public void setColumnType(String columnType) {
		this.columnType.set(columnType);
	}

	public String getColumnType() {
		return columnType.get();
	}

	public StringProperty columnTypeProperty() {
		return columnType;
	}

	public final StringProperty programTypeProperty() {
		return this.programType;
	}

	public final java.lang.String getProgramType() {
		return this.programTypeProperty().get();
	}

	public final void setProgramType(final java.lang.String programType) {
		this.programTypeProperty().set(programType);
	}

	public final StringProperty lockYnProperty() {
		return this.lockYn;
	}

	public final java.lang.String getLockYn() {
		return this.lockYnProperty().get();
	}

	public final void setLockYn(final java.lang.String lockYn) {
		this.lockYnProperty().set(lockYn);
	}

}
