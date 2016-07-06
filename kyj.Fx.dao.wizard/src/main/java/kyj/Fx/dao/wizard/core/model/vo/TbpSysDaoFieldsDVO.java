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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TbpSysDaoFieldsDVO {
	private StringProperty fieldName;
	private StringProperty type;
	private StringProperty testValue;

	public TbpSysDaoFieldsDVO() {
		this.fieldName = new SimpleStringProperty();
		this.testValue = new SimpleStringProperty();
		this.type = new SimpleStringProperty("Nomal");
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

	public void setFieldName(String fieldName) {
		this.fieldName.set(fieldName);
	}

	public String getFieldName() {
		return fieldName.get();
	}

	public StringProperty fieldNameProperty() {
		return fieldName;
	}

	public void setTestValue(String testValue) {
		this.testValue.set(testValue);
	}

	public String getTestValue() {
		return testValue.get();
	}

	public StringProperty testValueProperty() {
		return testValue;
	}
}
