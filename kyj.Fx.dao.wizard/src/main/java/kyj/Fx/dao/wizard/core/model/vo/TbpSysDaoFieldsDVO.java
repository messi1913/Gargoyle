/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 19.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * @author KYJ
 *
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TbpSysDaoFieldsDVO {
	private StringProperty methodName;

	private StringProperty fieldName;

	private StringProperty type;

	private StringProperty testValue;
	/**
	 * 정렬순서
	 * 
	 * @최초생성일 2016. 8. 26.
	 */
	private IntegerProperty sortSeq;

	public TbpSysDaoFieldsDVO() {
		this.methodName = new SimpleStringProperty();
		this.fieldName = new SimpleStringProperty();
		this.testValue = new SimpleStringProperty();
		this.type = new SimpleStringProperty("Nomal");
		this.sortSeq = new SimpleIntegerProperty();
	}

	public void setMethodName(String methodName) {
		this.methodName.set(methodName);
	}

	public String getMethodName() {
		return methodName.get();
	}

	public StringProperty methodNameProperty() {
		return methodName;
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

	public final IntegerProperty sortSeqProperty() {
		return this.sortSeq;
	}

	public final int getSortSeq() {
		return this.sortSeqProperty().get();
	}

	public final void setSortSeq(final int sortSeq) {
		this.sortSeqProperty().set(sortSeq);
	}

}
