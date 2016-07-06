/**
 * KYJ
 * 2015. 10. 14.
 */
package kyj.Fx.dao.wizard.core.model.vo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author KYJ
 *
 */
public class TableModelDVO {
	private StringProperty name;
	private StringProperty size;
	private StringProperty type;
	private StringProperty desc;
	private StringProperty pk;
	private StringProperty databaseTypeName;
	private StringProperty databaseColumnName;

	public TableModelDVO() {
		name = new SimpleStringProperty();
		size = new SimpleStringProperty();
		type = new SimpleStringProperty();
		desc = new SimpleStringProperty();
		pk = new SimpleStringProperty();
		databaseTypeName = new SimpleStringProperty();
		databaseColumnName = new SimpleStringProperty();
	}

	/**
	 * @return the pk
	 */
	public String getPk() {
		return pk.get();
	}

	/**
	 * @param pk
	 *            the pk to set
	 */
	public void setPk(String pk) {
		this.pk.set(pk);
	}

	public StringProperty pkProperty() {
		return pk;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setSize(String size) {
		this.size.set(size);
	}

	public String getSize() {
		return size.get();
	}

	public StringProperty sizeProperty() {
		return size;
	}

	public void setType(String type) {
		this.type.set(type);
	}

	public String getType() {
		return type.get();
	}

	public StringProperty typeProperty() {
		return type;
	}

	public void setDesc(String desc) {
		this.desc.set(desc);
	}

	public String getDesc() {
		return desc.get();
	}

	public StringProperty descProperty() {
		return desc;
	}

	public void setDabaseTypeName(String databaseTypeName) {
		this.databaseTypeName.set(databaseTypeName);
	}

	public String getDatabaseTypeName() {
		return databaseTypeName.get();
	}

	public StringProperty databaseTypeNameProperty() {
		return databaseTypeName;
	}

	public void setDatabaseColumnName(String databaseColumnName) {
		this.databaseColumnName.set(databaseColumnName);
	}

	public String getDatabaseColumnName() {
		return databaseColumnName.get();
	}

	public StringProperty databaseColumnNameProperty() {
		return databaseColumnName;
	}

}
