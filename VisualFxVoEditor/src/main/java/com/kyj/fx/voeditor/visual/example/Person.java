/********************************
 *	프로젝트 : FxTemplate
 *	패키지   : com.samsung.sds.sos.client.component.grid
 *	작성일   : 2015. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.grid.AbstractDVO;
import com.kyj.fx.voeditor.visual.component.grid.NonEditable;
import com.kyj.fx.voeditor.visual.component.grid.NotNull;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 샘플 VO
 *
 * @author KYJ
 *
 */
public class Person extends AbstractDVO {

	@NotNull
	@NonEditable
	private StringProperty name;

	private StringProperty age;

	private StringProperty address;

	private StringProperty phoneNumber;

	private StringProperty marrayYn;


	public Person() {
		name = new SimpleStringProperty();
		age = new SimpleStringProperty();
		address = new SimpleStringProperty();
		phoneNumber = new SimpleStringProperty();
		marrayYn = new SimpleStringProperty();
	}

	public StringProperty marrayYnProperty() {
		return marrayYn;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty ageProperty() {
		return age;
	}

	public StringProperty addressProperty() {
		return address;
	}

	public StringProperty phoneNumberProperty() {
		return phoneNumber;
	}

	public String getMarrayYn() {
		return marrayYn.get();
	}

	public void setMarrayYn(String marrayYn) {
		this.marrayYn.set(marrayYn);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getAge() {
		return age.get();
	}

	public void setAge(String age) {
		this.age.set(age);
	}

	public String getAddress() {
		return address.get();
	}

	public void setAddress(String address) {
		this.address.set(address);
	}

	public String getPhoneNumber() {
		return phoneNumber.get();
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber.set(phoneNumber);
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", address=" + address + ", phoneNumber=" + phoneNumber + "]";
	}

}