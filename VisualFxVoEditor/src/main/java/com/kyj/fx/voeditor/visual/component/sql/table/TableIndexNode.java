/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 1. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * @author KYJ
 *
 */
public class TableIndexNode {

	/**
	 * 자식노드
	 *
	 * @최초생성일 2016. 1. 4.
	 */
	private List<TableIndexNode> childrens;
	/**
	 * 인덱스 유형
	 *
	 * @최초생성일 2016. 1. 4.
	 */
	private StringProperty type;
	/**
	 * 인덱스명
	 *
	 * @최초생성일 2016. 1. 4.
	 */
	private StringProperty constraintName;

	/**
	 * 인덱스명
	 *
	 * @최초생성일 2016. 1. 4.
	 */
	private StringProperty columnNane;

	private BooleanProperty noneUnique;

	/**
	 * 인덱스 길이
	 *
	 * @최초생성일 2016. 1. 4.
	 */
	private IntegerProperty length;

	public TableIndexNode() {
		childrens = FXCollections.observableArrayList();
		this.columnNane = new SimpleStringProperty();
		this.constraintName = new SimpleStringProperty();
		this.type = new SimpleStringProperty();
		this.length = new SimpleIntegerProperty();
		this.noneUnique = new SimpleBooleanProperty();
	}

	public TableIndexNode(String type, String constraintName) {
		this();
		this.constraintName.set(constraintName);
		this.type.set(type);
	}

	public StringProperty constraintNameProperty() {
		return this.constraintName;
	}

	public java.lang.String getConstraintName() {
		return this.constraintNameProperty().get();
	}

	public void setConstraintName(final java.lang.String name) {
		this.constraintNameProperty().set(name);
	}

	public StringProperty typeProperty() {
		return this.type;
	}

	public java.lang.String getType() {
		return this.typeProperty().get();
	}

	public void setType(final java.lang.String type) {
		this.typeProperty().set(type);
	}

	public IntegerProperty lengthProperty() {
		return this.length;
	}

	public int getLength() {
		return this.lengthProperty().get();
	}

	public void setLength(final int length) {
		this.lengthProperty().set(length);
	}

	/**
	 * @return the childrens
	 */
	public List<TableIndexNode> getChildrens() {
		return childrens;
	}

	/**
	 * @param childrens
	 *            the childrens to set
	 */
	public void setChildrens(List<TableIndexNode> childrens) {
		this.childrens = childrens;
	}

	public final StringProperty columnNaneProperty() {
		return this.columnNane;
	}

	public final java.lang.String getColumnNane() {
		return this.columnNaneProperty().get();
	}

	public final void setColumnNane(final java.lang.String columnNane) {
		this.columnNaneProperty().set(columnNane);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return constraintName.get();
	}

	public final BooleanProperty noneUniqueProperty() {
		return this.noneUnique;
	}

	public final boolean isNoneUnique() {
		return this.noneUniqueProperty().get();
	}

	public final void setNoneUnique(final boolean noneUnique) {
		this.noneUniqueProperty().set(noneUnique);
	}

}
