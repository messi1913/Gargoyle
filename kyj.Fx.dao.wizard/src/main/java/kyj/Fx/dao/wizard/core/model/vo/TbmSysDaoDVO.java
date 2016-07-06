/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 19.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;

/**
 * @author KYJ
 *
 */
public class TbmSysDaoDVO extends ClassMeta {

	/**
	 * 패키지
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty packageName;
	/**
	 * 클래스명
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty className;
	/**
	 * 클래스가 존재하는 물리적 위치
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty location;
	/**
	 * 클래스에 대한 설명
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty classDesc;

	/**
	 * 테이블명
	 */
	private StringProperty tableName;

	private StringProperty extendsClassName;

	/**
	 * 클래스가 갖는 메소드 목록
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private List<TbpSysDaoMethodsDVO> tbpSysDaoMethodsDVOList;

	public TbmSysDaoDVO() {
		super("");
		this.packageName = new SimpleStringProperty();
		this.className = new SimpleStringProperty();
		this.location = new SimpleStringProperty();
		this.classDesc = new SimpleStringProperty();
		this.tableName = new SimpleStringProperty();
		this.tbpSysDaoMethodsDVOList = FXCollections.observableArrayList();
		this.extendsClassName = new SimpleStringProperty();
	}

	public void setExtendsClassName(String extendsClassName) {
		this.extendsClassName.set(extendsClassName);
	}

	public String getExtendsClassName() {
		return extendsClassName.get();
	}

	public StringProperty extendsClassNameProperty() {
		return extendsClassName;
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
	}

	public String getTableName() {
		return tableName.get();
	}

	public StringProperty tableNameProperty() {
		return tableName;
	}

	public void setPackageName(String packageName) {
		this.packageName.set(packageName);
	}

	public String getPackageName() {
		return packageName.get();
	}

	public StringProperty packageNameProperty() {
		return packageName;
	}

	public void setClassName(String className) {
		super.setName(className);
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

	public void setClassDesc(String desc) {
		this.classDesc.set(desc);
	}

	public String getClassDesc() {
		return classDesc.get();
	}

	public StringProperty classDescProperty() {
		return classDesc;
	}

	/**
	 * @return the tbpSysDaoMethodsDVOList
	 */
	public List<TbpSysDaoMethodsDVO> getTbpSysDaoMethodsDVOList() {
		return tbpSysDaoMethodsDVOList;
	}

	/**
	 * @param tbpSysDaoMethodsDVOList
	 *            the tbpSysDaoMethodsDVOList to set
	 */
	public void setTbpSysDaoMethodsDVOList(List<TbpSysDaoMethodsDVO> tbpSysDaoMethodsDVOList) {
		this.tbpSysDaoMethodsDVOList = tbpSysDaoMethodsDVOList;
	}

}
