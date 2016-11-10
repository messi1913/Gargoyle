/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 15.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class TableMasterDVO {
	private StringProperty schemaName;;
	private StringProperty tableName;
	private StringProperty className;
	private StringProperty location;
	private StringProperty description;

	public TableMasterDVO() {
		this.schemaName = new SimpleStringProperty();
		this.tableName = new SimpleStringProperty();
		this.className = new SimpleStringProperty();
		this.location = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
	}

	public String getTableName() {
		return tableName.get();
	}

	public void setClassName(String className) {
		this.className.set(className);
	}

	public String getClassName() {
		return className.get();
	}

	public StringProperty classNameProperty() {
		return className;
	}

	public void setLocation(String location) {
		this.location.set(location);
	}

	public String getLocation() {
		return location.get();
	}

	public StringProperty locationProperty() {
		return location;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public final StringProperty schemaNameProperty() {
		return this.schemaName;
	}

	public final java.lang.String getSchemaName() {
		return this.schemaNameProperty().get();
	}

	public final void setSchemaName(final java.lang.String schemaName) {
		this.schemaNameProperty().set(schemaName);
	}

}
