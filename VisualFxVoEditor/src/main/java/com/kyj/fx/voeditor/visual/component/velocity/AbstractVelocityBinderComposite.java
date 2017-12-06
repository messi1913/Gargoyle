/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.velocity
 *	작성일   : 2017. 12. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.velocity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxLoader;
import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;
import com.kyj.fx.voeditor.visual.framework.KeyNode;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "VelocityBinderView.fxml", css = "VelocityBinderView.fxml", isSelfController = true)
public abstract class AbstractVelocityBinderComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVelocityBinderComposite.class);
	@FXML
	private CodeArea txtContext;

	@FXML
	private TabPane tabpane;

	private StringProperty content = new SimpleStringProperty();

	private ObjectProperty<Map<String, Object>> param = new SimpleObjectProperty<>(new HashMap<>());

	public AbstractVelocityBinderComposite() {
		super();

		FxLoader.loadRoot(AbstractVelocityBinderComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});
	}

	@FXML
	public void initialize() {

		List<KeyNode> contents = tabContents();
		if (contents == null)
			throw new GagoyleRuntimeException("Invalide Override Exception.");

		contents.forEach(node -> {
			tabpane.getTabs().add(new Tab(node.getKey(), node.getValue()));
		});

		// 컨텐츠 변경 이벤트
		content.addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue == null)
					return;

				Tab selectedItem = tabpane.getSelectionModel().getSelectedItem();
				Node content = selectedItem.getContent();
				onTextLoad(content, newValue);
			}
		});

		// Tab 활성화 이벤트
		tabpane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				if (newValue != null) {
					String cont = content.get();
					if (cont != null)
						onTextLoad(newValue.getContent(), cont);
				}
			}
		});

	}

	/**
	 * 탭팬이 활성화 될때 처리할 항목 정의 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 4.
	 * @param content
	 * @param cont
	 */
	public abstract void onTextLoad(Node content, String cont);

	/**
	 * tab Content 구현 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 4.
	 * @return
	 */
	public abstract List<KeyNode> tabContents();

	/**
	 * set velocity Text <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 4.
	 * @param velocity
	 */
	public void setContext(String velocity) {
		this.txtContext.replaceText(velocity);
	}

	private ObjectProperty<List<Map<String, Object>>> data = new SimpleObjectProperty<>();

	public void setData(List<Map<String, Object>> data) {
		this.data.set(data);
	}

	/**
	 * btn 클릭 함수 내용 정의 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 4.
	 */
	@FXML
	public void btnExecuteOnAction() {
		Map<String, Object> map = param.get();

		try {
			map.put("data", data.get());
			String velocityToText = ValueUtil.getVelocityToText(txtContext.getText(), map, false);
			content.set(velocityToText);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}

	}

	/**
	 * 더미 데이터 매핑 팝업 열기
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 6.
	 */
	@FXML
	public void showItemOnAction() {
		//TODO
	}

}
