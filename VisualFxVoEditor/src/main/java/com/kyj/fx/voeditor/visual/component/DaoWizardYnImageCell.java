/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2015. 10. 21.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

import java.io.IOException;
import java.net.URL;

import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableObjectValue;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * 
 * 데이터값이 Y,혹은 N이냐에 따라 다른 이미지를 보여주기 위한 셀.
 * 
 * @author KYJ
 * @param <S>
 * @param <T>
 *
 */
public class DaoWizardYnImageCell<S> extends TableCell<S, String> {

	private static final double WIDTH = 30;
	private static final double HEIGHT = 30;

	private ImageView yimageView;
	private Image yimage;

	private ImageView nimageView;
	private Image nimage;

	private URL y;
	private URL n;

	public DaoWizardYnImageCell(URL y, URL n) {
		this.y = y;
		this.n = n;
		try {

			setStyle(getStyleText());

			addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
				if (ev.getButton() == MouseButton.PRIMARY) {

					if (getTableColumn().isEditable()) {
						if ("Y".equals(getItem())) {
							if (callback != null) {

								String newVal = callback.call("Y");
								super.setItem(newVal);
								updateItem(newVal, false);
								updateRealItem("Y");

							}

						} else {

							if (callback != null) {
								String newVal = callback.call("N");
								super.setItem(newVal);
								updateItem(newVal, false);
								updateRealItem("N");

							}

						}

						ev.consume();
					}

				}

			});

			if (y != null)
				ycreateImageField();
			if (n != null)
				ncreateImageField();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/********************************
	 * 작성일 : 2016. 8. 29. 작성자 : KYJ
	 *
	 * 실제 Observable Value값을 바꾼다.
	 * 
	 * @param value
	 ********************************/
	void updateRealItem(String value) {
		ObservableValue<String> cellObservableValue = getTableColumn().getCellObservableValue(getIndex());
		if (cellObservableValue instanceof WritableObjectValue) {
			@SuppressWarnings("unchecked")
			WritableObjectValue<String> wo = (WritableObjectValue<String>) cellObservableValue;
			wo.set(value);
		}
	}

	/********************************
	 * 작성일 : 2016. 8. 27. 작성자 : KYJ
	 *
	 * TableCell에 기본적으로 들어가게 처리할 style을 정의.
	 * 
	 * @return
	 ********************************/
	private String getStyleText() {
		return "-fx-alignment:center";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javafx.scene.control.TableCell#commitEdit(java.lang.Object)
	 */
	@Override
	public void commitEdit(String newValue) {
		super.commitEdit(newValue);
	}

	@Override
	public void updateItem(String _item, boolean empty) {
		super.updateItem(_item, empty);

		if (!empty) {

			if ("Y".equals(_item)) {
				setGraphic(yimageView);
			} else {
				setGraphic(nimageView);

			}

		} else {
			setGraphic(null);
		}
	}

	/**
	 * 기본으로 주어지는 처리 기능은 Y로 데이터가 입력되면 N, N이 입력되면 Y로 값을 바꿔주어 상태변화가 나타나게한다.
	 * 
	 * @최초생성일 2016. 8. 27.
	 */
	private Callback<String, String> callback = param -> "Y".equals(param) ? "N" : "Y";

	/********************************
	 * 작성일 : 2016. 8. 27. 작성자 : KYJ
	 *
	 * 값변환 처리 콜백 처리 기술. 주어진값의 결과에 따라 서로 다른 이미지가 보여지게됨.
	 * 
	 * @param callback
	 ********************************/
	public final void setOnValueChage(Callback<String, String> callback) {
		this.callback = callback;
	}

	/********************************
	 * 작성일 : 2016. 8. 27. 작성자 : KYJ
	 *
	 * 이미지생성1
	 * 
	 * @throws IOException
	 ********************************/
	private void ycreateImageField() throws IOException {
		if (y != null) {
			yimage = new Image(y.openStream(), WIDTH, HEIGHT, true, true);
			yimageView = new ImageView(yimage);

		}
	}

	/********************************
	 * 작성일 : 2016. 8. 27. 작성자 : KYJ
	 *
	 * 이미지생성2
	 * 
	 * @throws IOException
	 ********************************/
	private void ncreateImageField() throws IOException {
		if (n != null) {
			nimage = new Image(n.openStream(), WIDTH, HEIGHT, true, true);
			nimageView = new ImageView(nimage);

		}

	}

}
