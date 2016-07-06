/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import com.kyj.fx.voeditor.visual.diff.CompareResult;
import com.kyj.fx.voeditor.visual.diff.RelocateFileComparator;

import difflib.Chunk;

/**
 * @author KYJ
 *
 */
public class DiffAppController {
	private static Logger LOGGER = LoggerFactory.getLogger(DiffAppController.class);
	private static final String REVICED = "reviced";

	private static final String ORIGINAL = "original";

	/**
	 * 원본
	 */
	@FXML
	private ListView<String> lvOrinal;

	/**
	 *
	 */
	@FXML
	private ListView<String> lvRevice;

	private RelocateFileComparator compare;

	private ObjectProperty<CompareResult> fileCompareResultProperty = new SimpleObjectProperty<>();

	public DiffAppController() {
		compare = new RelocateFileComparator();
	}

	/**
	 * @return the compare
	 */
	public RelocateFileComparator getCompare() {
		return compare;
	}

	/**
	 * @param compare
	 *            the compare to set
	 */
	public void setCompare(RelocateFileComparator compare) {
		this.compare = compare;
	}

	public void setDiffFile(File ordinalFile, File reviceFile) throws Exception {
		this.compare.setOriginal(ordinalFile);
		this.compare.setRevised(reviceFile);

		try {
			fileCompareResultProperty.set(this.compare.getChunkResult());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize() {

		lvOrinal.setCellFactory(param -> new DefaultTextFieldListCell(ORIGINAL));
		lvRevice.setCellFactory(param -> new DefaultTextFieldListCell(REVICED));

		fileCompareResultProperty.addListener((oba, oldresult, newresult) -> {
			if (newresult == null)
				return;
			List<Chunk> listOfOriginChanges = newresult.getListOfOriginChanges();
			List<Chunk> listOfReviceChanges = newresult.getListOfReviceChanges();

			List<String> originalFileLines = newresult.getOriginalFileLines();
			List<String> revisedFileLines = newresult.getRevisedFileLines();

			LOGGER.debug("### origin");
			listOfOriginChanges.forEach(c -> {
				LOGGER.debug(c.toString());
			});
			LOGGER.debug("### revice");
			listOfReviceChanges.forEach(c -> {
				LOGGER.debug(c.toString());
			});

			lvOrinal.getItems().addAll(originalFileLines);
			lvRevice.getItems().addAll(revisedFileLines);
		});
	}

	class DefaultTextFieldListCell extends TextFieldListCell<String> {

		private String name;

		public DefaultTextFieldListCell(String name) {
			this.name = name;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javafx.scene.control.IndexedCell#updateIndex(int)
		 */
		@Override
		public void updateIndex(int i) {
			super.updateIndex(i);

			Label value = new Label(String.valueOf(getIndex() + 1));
			value.setPrefSize(20, 10);
			value.setMinWidth(20);
			value.setMaxWidth(20);
			value.setTextFill(Color.WHITE);
			value.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
			setGraphic(value);
			if (ORIGINAL.equals(name)) {
				if (i == -1)
					return;
				ObservableList<String> items = lvRevice.getItems();
				if (i >= items.size())
					return;
				String text2 = getText();
				String string = items.get(i);
				boolean equals = Objects.equals(text2, string);
				// LOGGER.debug(String.format("%d %b [%s] <%s>", i,
				// equals, text2, string));
				if (!equals)
					setTextFill(Color.ORANGE);
			} else {
				if (i == -1)
					return;
				ObservableList<String> items = lvOrinal.getItems();
				if (i >= items.size())
					return;
				String text2 = getText();
				String string = items.get(i);
				boolean equals = Objects.equals(text2, string);

				// LOGGER.debug(String.format("%d %b [%s] <%s>", i,
				// equals, text2, string));
				if (!equals)
					setTextFill(Color.ORANGE);
			}

		}

	}
}
