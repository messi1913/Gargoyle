/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.sun.javafx.scene.control.skin
 *	작성일   : 2017. 7. 5.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.javafx.scene.control.skin;

import java.util.BitSet;
import java.util.List;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;

import com.sun.javafx.scene.control.skin.TableViewSkin;

import javafx.collections.ListChangeListener;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

/**
 * @author KYJ
 *
 */
@SuppressWarnings("restriction")
public class CTableViewSkin<T> extends TableViewSkin<T> {

	private CTableView<T> tableView;
	private CVirtualFlow cflow;

	public CTableViewSkin(CTableView<T> tableView) {
		super(tableView);
		this.tableView = tableView;
		this.cflow = (CVirtualFlow) super.flow;

		hBarValue = new BitSet(tableView.getItems().size());
		tableView.getFixedColumns().addListener(fixedColumnsListener);
	}

	@Override
	protected CTableHeaderRow createTableHeaderRow() {
		return new CTableHeaderRow(this);
	}

	@Override
	protected CVirtualFlow createVirtualFlow() {
		CVirtualFlow<IndexedCell> cVirtualFlow = new CVirtualFlow<>();
		return cVirtualFlow;
	}

	/** {@inheritDoc} */
	@Override
	public TableRow<T> createCell() {
		TableRow<T> cell;

		if (tableView.getRowFactory() != null) {
			cell = tableView.getRowFactory().call(tableView);
		} else {
			cell = new CTableRow<T>();
		}

		cell.updateTableView(tableView);
		return cell;
	}

	protected void onSelectLeftCell() {
		scrollHorizontally();
	}

	protected void onSelectRightCell() {
		scrollHorizontally();
	}

	public ScrollBar getHBar() {
		if (getFlow() != null) {
			return getFlow().getHorizontalBar();
		}
		return null;
	}

	public ScrollBar getVBar() {
		return getFlow().getVerticalbar();
	}

	CVirtualFlow<?> getFlow() {
		return (CVirtualFlow<?>) flow;
	}

	/**
	 * These variable try to optimize the layout of the rows in order not to layout
	 * every time every row.
	 * 
	 * So rowToLayout contains the rows that really needs layout(contain span or fixed).
	 * 
	 * And hBarValue is an indicator for the VirtualFlow. When the Hbar is touched, this BitSet
	 * is set to false. And when a row is drawing, it flips its value in this BitSet. 
	 * So that we know when scrolling up or down whether a row has taken into account
	 * that the HBar was moved (otherwise, blank area may appear).
	 */
	BitSet hBarValue;

	/**
	 * We listen on the FixedColumns in order to do the modification in the
	 * VirtualFlow.
	 */
	private final ListChangeListener<TableColumn<T, ?>> fixedColumnsListener = new ListChangeListener<TableColumn<T, ?>>() {
		@Override
		public void onChanged(Change<? extends TableColumn<T, ?>> c) {
			hBarValue.clear();
			getFlow().requestLayout();
		}
	};
	

	double fixedColumnWidth = 0;


	//	protected void horizontalScroll() {
	//
	//		super.horizontalScroll();
	//		if (getSkinnable().getFixedCellSize() > 0) {
	//			cflow.requestCellLayout();
	//		}

	//		CTableHeaderRow tableHeaderRow = (CTableHeaderRow) getTableHeaderRow();
	//		ObservableList<TableColumn<?, ?>> fixedColumns = tableHeaderRow.getFixedColumns();

	//		ObservableList<TableColumn<?, ?>> fixedColumns = FXCollections.observableArrayList(getColumns().get(0), getColumns().get(1));
	//
	//		fixedColumnWidth = 0;
	//		double start = 0;// scrollX;
	//		for (int i = 0, max = fixedColumns.size(); i < max; ++i) {
	//			TableColumn<T, ?> fixedColumn = getColumns().get(i);
	//			if (fixedColumn == null || !fixedColumn.isVisible())
	//				continue;
	//			fixedColumnWidth += fixedColumn.getWidth();
	//			start += fixedColumn.getWidth();
	//		}
	//		double end = start;
	//		for (int i = fixedColumns.size(), max = getColumns().size(); i < max; i++) {
	//			TableColumn<T, ?> column = getColumns().get(i);
	//			if (column == null || !column.isVisible())
	//				continue;
	//			end += column.getWidth();
	//		}
	//
	//		//		final double end = start + col.getWidth();
	//
	//		// determine the visible width of the table
	//
	//		//		flow.getVbar()
	//		final double headerWidth = getSkinnable().getWidth() - snappedLeftInset() - snappedRightInset() - getTableHeaderRow().getWidth();//verticalHeader.getVerticalHeaderWidth();
	//
	//		final double pos = cflow.getHorizentalbar().getValue(); //flow.getPosition();//flow.getHbar().valueProperty().get();
	//
	//		// determine by how much we need to translate the table to ensure that
	//		// the start position of this column lines up with the left edge of the
	//		// tableview, and also that the columns don't become detached from the
	//		// right edge of the table
	//
	//		final double max = cflow.getHorizentalbar().getMax();
	//		double newPos;
	//
	//		System.out.println(pos + " : " + max);
	//		/**
	//		 * If the starting position of our column if inferior to the left egde
	//		 * (of tableView or fixed columns), then we need to scroll.
	//		 */
	//		if (start < pos + fixedColumnWidth && start >= 0 && start >= fixedColumnWidth) {
	//			newPos = start - fixedColumnWidth < 0 ? start : start - fixedColumnWidth;
	//			cflow.getHorizentalbar().setValue(newPos);
	//			//If the starting point is not visible on the right.    
	//		} else if (start > pos + headerWidth) {
	//			final double delta = start < 0 || end > headerWidth ? start - pos - fixedColumnWidth : 0;
	//			newPos = pos + delta > max ? max : pos + delta;
	//			System.out.println(newPos);
	//			cflow.setPosition(newPos); //flow.getHbar().setValue(newPos);
	//		}

	//	}

	//	protected void scrollHorizontally(TableColumn<T, ?> col) {
	//		if (col == null || !col.isVisible())
	//			return;
	//
	//		final Control control = getSkinnable();
	//		TableHeaderRow tableHeaderRow = getTableHeaderRow();
	//		// RT-37060 - if we are trying to scroll to a column that has not
	//		// yet even been rendered, we must wait until the layout pass has
	//		// happened and then do the scroll. The laziest way to do this is to
	//		// queue up the task to run later, at which point we will have hopefully
	//		// fully run the column through layout and css.
	//		TableColumnHeader header = tableHeaderRow.getColumnHeaderFor(col);
	//		if (header == null || header.getWidth() <= 0) {
	//			Platform.runLater(() -> scrollHorizontally(col));
	//			return;
	//		}
	//
	//		// work out where this column header is, and it's width (start -> end)
	//		double start = 0;
	//		for (TableColumn<T, ?> c : getVisibleLeafColumns()) {
	//			if (c.equals(col))
	//				break;
	//			start += c.getWidth();
	//		}
	//		double end = start + col.getWidth();
	//
	//		// determine the visible width of the table
	//		double headerWidth = control.getWidth() - snappedLeftInset() - snappedRightInset();
	//
	//		// determine by how much we need to translate the table to ensure that
	//		// the start position of this column lines up with the left edge of the
	//		// tableview, and also that the columns don't become detached from the
	//		// right edge of the table
	//		//		cflow.getHorizentalbar()
	//		double pos = cflow.getHorizentalbar().getValue();
	//		double max = cflow.getHorizentalbar().getMax();
	//		double newPos;
	//
	//		if (start < pos && start >= 0) {
	//			newPos = start;
	//		} else {
	//			double delta = start < 0 || end > headerWidth ? start - pos : 0;
	//			newPos = pos + delta > max ? max : pos + delta;
	//		}
	//
	//		// FIXME we should add API in VirtualFlow so we don't end up going
	//		// direct to the hbar.
	//		// actually shift the flow - this will result in the header moving
	//		// as well
	//		cflow.getHorizentalbar().setValue(newPos);
	//	}

	@Override
	public void scrollHorizontally() {
		super.scrollHorizontally();
	}

	@Override
	protected void scrollHorizontally(TableColumn<T, ?> col) {
		if (col == null || !col.isVisible()) {
			return;
		}
		/**
		 * We modified this function so that we ensure that any selected cells
		 * will not be below a fixed column. Because when there's some fixed
		 * columns, the "left border" is not the table anymore, but the right
		 * side of the last fixed columns.
		 *
		 * Moreover, we need to re-compute the fixedColumnWidth because the
		 * layout of the rows hasn't been done yet and the value is not right.
		 * So we might end up below a fixedColumns.
		 */

		fixedColumnWidth = 0;
		final double pos = cflow.getHorizontalBar().getValue();
		int index = getColumns().indexOf(col);
		double start = 0;// scrollX;

		//		ObservableList<TableColumn<T, ?>> fixedColumns = FXCollections.observableArrayList(getColumns().get(0), getColumns().get(1));

		for (int i = 0; i < index; ++i) {
			
			TableColumn<T, ?> column = getColumns().get(i);
			if (i <= 2) {
				fixedColumnWidth += column.getWidth();
				//			}
				start += column.getWidth();
			}

			final double end = start + col.getWidth();

			// determine the visible width of the table
			//		final double headerWidth = handle.getView().getWidth() - snappedLeftInset() - snappedRightInset()
			//				- verticalHeader.getVerticalHeaderWidth();
			final double headerWidth = getSkinnable().getWidth() - snappedLeftInset() - snappedRightInset()
					- getTableHeaderRow().getWidth();//verticalHeader.getVerticalHeaderWidth();

			// determine by how much we need to translate the table to ensure that
			// the start position of this column lines up with the left edge of the
			// tableview, and also that the columns don't become detached from the
			// right edge of the table
			double max = cflow.getHorizontalBar().getMax();
			double newPos;
			//			cflow.getHorizontalBar().setMin(start);
			/**
			 * If the starting position of our column if inferior to the left egde
			 * (of tableView or fixed columns), then we need to scroll.
			 */
			if (start < pos + fixedColumnWidth && start >= 0 && start >= fixedColumnWidth) {
				newPos = start - fixedColumnWidth < 0 ? start : start - fixedColumnWidth;
				cflow.getHorizontalBar().setValue(newPos);
				//If the starting point is not visible on the right.    
			} else if (start > pos + headerWidth) {
				final double delta = start < 0 || end > headerWidth ? start - pos - fixedColumnWidth : 0;
				newPos = pos + delta > max ? max : pos + delta;
				cflow.getHorizontalBar().setValue(newPos);
			}

			/**
			 * In all other cases, it means the cell is visible so no scroll needed,
			 * because otherwise we may end up with a continous scroll that always
			 * place the selected cell in the center of the screen.
			 */
		}
	}

}
