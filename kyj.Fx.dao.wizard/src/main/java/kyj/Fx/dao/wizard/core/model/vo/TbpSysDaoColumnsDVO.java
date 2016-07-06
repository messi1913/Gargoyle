/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 19.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

/**
 * @author KYJ
 *
 */
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

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

	public TbpSysDaoColumnsDVO() {
		this.columnName = new SimpleStringProperty();
		this.columnType = new SimpleStringProperty();
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
}
