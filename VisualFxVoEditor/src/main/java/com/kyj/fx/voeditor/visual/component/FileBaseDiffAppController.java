/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.voeditor.visual.diff.ChunkWrapper;
import com.kyj.fx.voeditor.visual.diff.CompareResult;
import com.kyj.fx.voeditor.visual.diff.DiffComparable;
import com.kyj.fx.voeditor.visual.diff.FileBaseComparator;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "FileBaseDiffApp.fxml")
public class FileBaseDiffAppController {

	private static Logger LOGGER = LoggerFactory.getLogger(FileBaseDiffAppController.class);
	private static final String REVICED = "reviced";

	private static final String ORIGINAL = "original";

	@FXML
	private BorderPane borMain;
	@FXML
	private BorderPane borControls;
	@FXML
	private GridPane gpSnap;
	/**
	 * 원본
	 */
	@FXML
	private ListView<ChunkWrapper> lvOrinal;

	/**
	 *
	 */
	@FXML
	private ListView<ChunkWrapper> lvRevice;

	@FXML
	private AnchorPane anImaveWrapped;

	private ImageViewPane ivpReviced;
	private ImageView ivReviced;
	private ImageViewPane ivpOrigin;
	private ImageView ivOrigin;
	/**
	 * 비교처리 컴패어구현
	 */
	private DiffComparable<File> compare;

	/**
	 * 비교결과파일
	 */
	private ObjectProperty<CompareResult> fileCompareResultProperty = new SimpleObjectProperty<>();

	enum DeltaType {
		ORIGINAL, REVICED
	}

	public FileBaseDiffAppController() {
		compare = new FileBaseComparator();

	}

	/**
	 * @return the compare
	 */
	public DiffComparable<File> getCompare() {
		return compare;
	}

	/**
	 * @param compare
	 *            the compare to set
	 */
	public void setCompare(DiffComparable<File> compare) {
		this.compare = compare;
	}

	public void setDiffFile(File ordinalFile, File reviceFile) throws Exception {
		this.compare.setOriginal(ordinalFile);
		this.compare.setRevised(reviceFile);

		try {
			fileCompareResultProperty.set(this.compare.getChunkResult());
		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	@FXML
	public void initialize() {

		/* initControls */
		ivReviced = new ImageView();
		ivpReviced = new ImageViewPane(ivReviced);
		ivpReviced.setPrefWidth(200);
		ivpReviced.setPrefHeight(150);

		ivOrigin = new ImageView();
		ivpOrigin = new ImageViewPane(ivOrigin);
		ivpOrigin.setPrefWidth(200);
		ivpOrigin.setPrefHeight(150);

		gpSnap.add(ivpReviced, 0, 0);
		gpSnap.add(ivpOrigin, 1, 0);

		lvOrinal.setCellFactory(param -> new DefaultTextFieldListCell(ORIGINAL));
		lvRevice.setCellFactory(param -> new DefaultTextFieldListCell(REVICED));

		fileCompareResultProperty.addListener((oba, oldresult, newresult) -> {
			if (newresult == null)
				return;
			List<ChunkWrapper> wrapperedOrigin = extractedWrapperedChunk(DeltaType.ORIGINAL, newresult);
			List<ChunkWrapper> wrapperedReviced = extractedWrapperedChunk(DeltaType.REVICED, newresult);

			lvOrinal.getItems().addAll(wrapperedOrigin);
			lvRevice.getItems().addAll(wrapperedReviced);
		});

		lvRevice.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				int movePosition = -1;
				ChunkWrapper selectedItem = lvRevice.getSelectionModel().getSelectedItem();
				Delta delta = selectedItem.getDelta();
				if (delta != null && delta.getOriginal() != null) {
					movePosition = selectedItem.getPosition();
					lvOrinal.scrollTo(movePosition - 1);
					lvOrinal.getSelectionModel().select(movePosition);
				}
				// lvRevice.scrollTo(lvRevice.getSelectionModel().getSelectedIndex());

			}
		});

		lvOrinal.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				int movePosition = -1;
				ChunkWrapper selectedItem = lvOrinal.getSelectionModel().getSelectedItem();
				Delta delta = selectedItem.getDelta();
				if (delta != null && delta.getRevised() != null) {
					movePosition = delta.getRevised().getPosition();
					lvRevice.scrollTo(movePosition - 1);
					lvRevice.getSelectionModel().select(movePosition);
				}
			}

		});

		lvOrinal.addEventFilter(ScrollEvent.SCROLL, event -> {
			snappOriginShot();
		});

		lvRevice.addEventFilter(ScrollEvent.SCROLL, event -> {
			snappReviceShot();
		});

		snappOriginShot();
		snappReviceShot();
	}

	void snappOriginShot() {
		Platform.runLater(() -> lvOrinal.snapshot(param -> {
			WritableImage image = param.getImage();
			ivOrigin.setImage(image);
			return null;
		} , new SnapshotParameters(), null));
	}

	void snappReviceShot() {
		Platform.runLater(() -> lvRevice.snapshot(param -> {
			WritableImage image = param.getImage();
			ivReviced.setImage(image);
			return null;
		} , new SnapshotParameters(), null));
	}

	// public enum TYPE {
	// NOMAL, CHANGE, DELETE, INSERT
	// }

	/**
	 * chunk객체를 UI로 표현하기 위해 wrapping처리함.
	 *
	 * @param deltaType
	 * @param result
	 * @return
	 */
	private List<ChunkWrapper> extractedWrapperedChunk(final DeltaType deltaType, final CompareResult result) {
		List<String> readLines = null;
		if (DeltaType.ORIGINAL == deltaType) {
			readLines = result.getOriginalFileLines();
		} else {
			readLines = result.getRevisedFileLines();
		}
		return extractedWrapperedChunk(deltaType, result.getDeltas(), readLines);
	}

	/**
	 * chunk객체를 UI로 표현하기 위해 wrapping처리함.
	 *
	 * @param deltaType
	 * @param deltas
	 * @param readLines
	 * @return
	 */
	private List<ChunkWrapper> extractedWrapperedChunk(final DeltaType deltaType, final List<Delta> deltas, final List<String> readLines) {
		int size = readLines.size();

		ArrayList<ChunkWrapper> collect = deltas.stream().map(delta -> {

			Chunk chunk = null;
			if (DeltaType.ORIGINAL == deltaType) {
				chunk = delta.getOriginal();
			} else {
				chunk = delta.getRevised();
			}

			int position = chunk.getPosition();
			@SuppressWarnings("unchecked")
			List<String> lines = (List<String>) chunk.getLines();
			TYPE type = delta.getType();

			ChunkWrapper chunkWrapper = new ChunkWrapper();
			chunkWrapper.setType(type);
			chunkWrapper.setLines(lines);
			chunkWrapper.setChunk(chunk);
			chunkWrapper.setPosition(position);
			chunkWrapper.setDelta(delta);

			return chunkWrapper;
		}).collect(() -> {
			ArrayList<ChunkWrapper> newChunk = new ArrayList<>(size);
			for (int i = 0; i < readLines.size(); i++) {
				ChunkWrapper chunkWrapper = new ChunkWrapper();
				chunkWrapper.setStr(readLines.get(i));
				chunkWrapper.setPosition(i);
				newChunk.add(chunkWrapper);
			}
			return newChunk;
		} , (collection, item) -> {
			int position = item.getPosition();
			List<String> lines = item.getLines();
			TYPE type = item.getType();
			int tmpPosition = position;
			for (String str : lines) {
				ChunkWrapper chunkWrapper = new ChunkWrapper();
				chunkWrapper.setType(type);
				chunkWrapper.setLines(lines);
				chunkWrapper.setPosition(position);
				chunkWrapper.setChunk(item.getChunk());
				chunkWrapper.setStr(str);
				chunkWrapper.setDelta(item.getDelta());
				collection.set(tmpPosition++, chunkWrapper);
			}
		} , (collection1, collection2) -> collection1.addAll(collection2));

		return collect;
	}

	public void reduce() {

	}

	class DefaultTextFieldListCell extends ListCell<ChunkWrapper> {

		private String name;

		public DefaultTextFieldListCell(String name) {
			this.name = name;

			// setConverter(new StringConverter<ChunkWrapper>() {
			//
			// @Override
			// public String toString(ChunkWrapper object) {
			// return object.getStr();
			// }
			//
			// @Override
			// public ChunkWrapper fromString(String string) {
			// return null;
			// }
			// });
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * javafx.scene.control.cell.TextFieldListCell#updateItem(java.lang.
		 * Object, boolean)
		 */
		@Override
		public void updateItem(ChunkWrapper item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setGraphic(null);
			} else {
				HBox graphics = new HBox();

				int WIDTH = String.valueOf(getIndex() + 1).length() * 10;
				// [start]number text
				Label lblNumber = new Label(String.valueOf(getIndex() + 1));
				lblNumber.setPrefSize(WIDTH, 10);
				lblNumber.setMinWidth(20);
				lblNumber.setMaxWidth(100);
				lblNumber.setTextFill(Color.WHITE);
				lblNumber.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
				// [end]number text

				if (REVICED.equals(name)) {

					TYPE type = item.getType();
					if (type != null) {
						String mod = "";
						switch (type) {
						case CHANGE:
							mod = "*";
							graphics.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
							break;
						case DELETE:
							mod = "-";
							graphics.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
							break;
						case INSERT:
							mod = "+";
							graphics.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
							break;
						}

						graphics.getChildren().add(lblNumber);
						graphics.getChildren().add(new Label("[".concat(mod).concat("]")));

					} else {
						graphics.getChildren().add(lblNumber);
					}

					graphics.getChildren().add(new Label(item.getStr()));

					// [start] Tooltip
					Delta delta = item.getDelta();
					if (delta != null) {
						Chunk original = delta.getOriginal();
						Chunk revised = delta.getRevised();
						setTooltip(new Tooltip(String.format("position[%d] \n%s -> %s", revised.getPosition() + 1,
								revised.getLines().toString(), original.getLines().toString())));
					}
					// [end] Tooltip

				} else /* if (ORIGINAL.equals(name)) */ {

					TYPE type = item.getType();

					if (type != null) {
						// style와 setback처리중 어느게 더 효츌적일까?
						// setStyle("-fx-background-color:YELLOWGREEN");
						graphics.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, new Insets(0, 0, 0, 0))));
						// switch (type) {
						// case CHANGE:
						// graphics.setBackground(new Background(new
						// BackgroundFill(Color.YELLOWGREEN, null, null)));
						// break;
						// case DELETE:
						// graphics.setBackground(new Background(new
						// BackgroundFill(Color.YELLOWGREEN, null, null)));
						// break;
						// case INSERT:
						// graphics.setBackground(new Background(new
						// BackgroundFill(Color.YELLOWGREEN, null, null)));
						// break;
						// }

					}
					graphics.getChildren().addAll(lblNumber, new Label(item.getStr()));

					// [start] Tooltip
					Delta delta = item.getDelta();
					if (delta != null) {
						Chunk original = delta.getOriginal();
						Chunk revised = delta.getRevised();
						setTooltip(new Tooltip(String.format("position[%d] \n%s -> %s", revised.getPosition() + 1,
								original.getLines().toString(), revised.getLines().toString())));
					}
					// [end] Tooltip
				}
				setGraphic(graphics);
			}

		}

	}
}
