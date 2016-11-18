/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 11. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.awt.Font;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.kyj.fx.voeditor.visual.framework.RealtimeSearchItemVO;
import com.kyj.fx.voeditor.visual.framework.RealtimeSearchVO;
import com.kyj.fx.voeditor.visual.suppliers.NaverRealtimeSrchSupplier;
import com.kyj.fx.voeditor.visual.util.FxUtil;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/**
 * @author KYJ
 *
 */
public class NrchRealtimeSrchFlowComposite extends FlowCardComposite {

	private AtomicInteger atomicInteger = new AtomicInteger(0);

	private Function<RealtimeSearchVO, List<VBox>> nodeConverter = v -> {

		final int andIncrement = atomicInteger.getAndIncrement();

		List<VBox> collect = IntStream.iterate(0, t -> t + 1).limit(10).mapToObj(idx -> {
			RealtimeSearchItemVO obj = v.getItems().get(idx);

			Label lblCont = new Label(String.format("%s.- %s", obj.getRank(), obj.getKeyword()));
			lblCont.setMaxWidth(Double.MAX_VALUE);
			lblCont.setMaxHeight(Double.MAX_VALUE);
			lblCont.setAlignment(Pos.CENTER);
//			lblCont.setStyle("-fx-background-color : green");
			lblCont.setFont(FxUtil.getBoldFont());
			VBox.setVgrow(lblCont, Priority.ALWAYS);
			Label lblTitle = new Label(String.format("[%s]\n", v.getTitle()));
			lblTitle.setStyle("-fx-background-color : #E9E9E9 ; -fx-font-fill : white;");
//			lblTitle.setMaxWidth(Double.MAX_VALUE);
			VBox vBox = new VBox(lblTitle, lblCont);

//
			//			TextFlow textFlow = new TextFlow(vBox);

			//						Label label = new Label(String.format("주제 : [%s]\n%d. - %s", v.getTitle(), obj.getRank(), obj.getKeyword()));
			//						label.setWrapText(true);
			//						label.setAlignment(Pos.CENTER);
			vBox.setStyle("-fx-background-color: " + getDefaultColor(andIncrement % 12));
			//			label.setUserData(obj);
			//				label.setMinWidth(120d);
			vBox.setMaxWidth(500d);
			//				label.setPrefWidth(150d);C
			//				label.setMinHeight(70d);
			vBox.setMaxHeight(100d);
			//				label.setPrefHeight(80d);

			return vBox;
		}).collect(Collectors.toList());

		return collect;
	};

	/**
	 * @param nodeConverter
	 */
	public NrchRealtimeSrchFlowComposite() {
		super();
		init();
	}

	protected void init() {
		Platform.runLater(() -> {
			setLimitColumn(20);
			List<RealtimeSearchVO> meta = NaverRealtimeSrchSupplier.getInstance().getMeta();
			List<VBox> collect = meta.stream().map(nodeConverter::apply).flatMap(v -> v.stream()).collect(Collectors.toList());
			getFlowChildrens().addAll(collect);
		});
	}

	private String getDefaultColor(int i) {
		String color = "#B5E61D";
		switch (i) {
		case 0:
			color = "#EBCA2F"; //"#8F3F7E";
			break;
		case 1:
			color = "#B5305F";
			break;
		case 2:
			color = "#CE584A";
			break;
		case 3:
			color = "#DB8D5C";
			break;
		case 4:
			color = "#DA854E";
			break;
		case 5:
			color = "#E9AB44";
			break;
		case 6:
			color = "#FEE435";
			break;
		case 7:
			color = "#99C286";
			break;
		case 8:
			color = "#01A05E";
			break;
		case 9:
			color = "#4A8895";
			break;
		case 10:
			color = "#16669B";
			break;
		case 11:
			color = "#2F65A5";
			break;
		case 12:
			color = "#4E6A9C";
			break;
		default:
			break;
		}
		return color;
	}
}
