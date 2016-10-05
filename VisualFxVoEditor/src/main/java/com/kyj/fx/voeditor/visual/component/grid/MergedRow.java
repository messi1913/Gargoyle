package com.kyj.fx.voeditor.visual.component.grid;

import com.sun.javafx.scene.control.skin.TableRowSkin;

import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class MergedRow<T, S> extends TableRow<T> {

	private TableView<T> tableView;
	private TableColumn<T, S> tableColumn;
	private static final String DEFAULT_STYLE_CLASS = "table-row-merge-cell";

	public MergedRow(TableView<T> tableView, TableColumn<T, S> tableColumn) {
		super();
		this.tableView = tableView;
		updateTableView(this.tableView);
		this.tableColumn = tableColumn;
		getStyleClass().addAll(DEFAULT_STYLE_CLASS);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TableRowSkin<>(this);
	}

	public TableColumn<T, S> getTableColumn() {
		return tableColumn;
	}

}
