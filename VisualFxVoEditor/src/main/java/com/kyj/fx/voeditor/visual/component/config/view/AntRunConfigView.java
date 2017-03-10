/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.config.view
 *	작성일   : 2017. 3. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.config.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.component.config.model.AntRunConfigItem;
import com.kyj.fx.voeditor.visual.component.console.SystemConsole;
import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;
import com.kyj.fx.voeditor.visual.framework.jdt.compiler.AntJavaCompiler;
import com.kyj.fx.voeditor.visual.util.FileUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "AntRunConfigView.fxml", isSelfController = true)
public class AntRunConfigView extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(AntRunConfigView.class);
	private File buildFile;

	@FXML
	private TableView<AntRunConfigItem> tbTargets;
	@FXML
	private TableColumn<AntRunConfigItem, Boolean> tcChkTarget;
	@FXML
	private TableColumn<AntRunConfigItem, String> tcTargetDesc;
	@FXML
	private TableColumn<AntRunConfigItem, String> tcTargetName;
	@FXML
	private TextArea txtTargetCont;
	@FXML
	private TextField txtBuildFileLocation, txtProjectName;
	@FXML
	private Button btnBuildFileOpen;
	@FXML
	private TabPane tabAntConfig;
	@FXML
	private Tab tabTargets;
	/**
	 * ant 타겟 대상목록
	 * @최초생성일 2017. 3. 10.
	 */
	private ObservableSet<AntRunConfigItem> tarCheckedList = FXCollections.observableSet(new HashSet<>());

	private AntJavaCompiler c;
	private Stage owner;

	public AntRunConfigView(Stage owner, File buildFile) {
		this.buildFile = buildFile;
		this.owner = owner;
		FxUtil.loadRoot(AntRunConfigView.class, this, err -> LOGGER.error(ValueUtil.toString(err)));

	}

	class ChangeListenerImpl implements ChangeListener<Boolean> {

		private AntRunConfigItem item;

		public ChangeListenerImpl(AntRunConfigItem item) {
			this.item = item;
		}

		/* (non-Javadoc)
		 * @see javafx.beans.value.ChangeListener#changed(javafx.beans.value.ObservableValue, java.lang.Object, java.lang.Object)
		 */
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			String cont = txtTargetCont.getText();
			if (newValue) {
				cont = cont.concat(item.getTargetName()).concat(",");
				tarCheckedList.add(item);
			} else {
				cont = cont.replace(item.getTargetName() + ",", "");
				tarCheckedList.remove(item);
			}
			txtTargetCont.setText(cont);
		}

	}

	@FxPostInitialize
	public void post() {
		tabAntConfig.getSelectionModel().select(tabTargets);
	}

	private BuildListener progressListener = new BuildListener() {

		@Override
		public void taskStarted(BuildEvent event) {

		}

		@Override
		public void taskFinished(BuildEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void targetStarted(BuildEvent event) {
			System.out.println("target start\n" + event);

		}

		@Override
		public void targetFinished(BuildEvent event) {
			System.out.println("target start\n" + event);

		}

		@Override
		public void messageLogged(BuildEvent event) {
			// TODO Auto-generated method stub

		}

		@Override
		public void buildStarted(BuildEvent event) {

		}

		@Override
		public void buildFinished(BuildEvent event) {
			// TODO Auto-generated method stub

		}
	};

	@FXML
	public void initialize() {
		c = new AntJavaCompiler(this.buildFile.getParentFile(), this.buildFile) {
			@Override
			protected BuildListener getBuildListener() {
				return progressListener;
			}
		};
		c.parse();

		this.txtBuildFileLocation.setText(this.buildFile.getAbsolutePath());
		this.txtProjectName.setText(c.getProjectName());

		//		tcTargetName.setCellFactory(value);
		tcChkTarget.setCellFactory(CheckBoxTableCell.forTableColumn(tcChkTarget));
		tcChkTarget.setCellValueFactory(param -> param.getValue().chkProperty());

		tcTargetName.setCellFactory(TextFieldTableCell.forTableColumn());
		tcTargetName.setCellValueFactory(param -> param.getValue().targetNameProperty());
		tcTargetDesc.setCellFactory(TextFieldTableCell.forTableColumn());
		tcTargetDesc.setCellValueFactory(param -> param.getValue().targetDescProperty());

		List<AntRunConfigItem> collect = c.getTargets().entrySet().stream().map(ent -> {
			String key = ent.getKey();
			if (ValueUtil.isEmpty(key))
				return null;

			String desc = ent.getValue().getDescription() == null ? "" : ent.getValue().getDescription();
			AntRunConfigItem item = new AntRunConfigItem(key, desc);
			item.chkProperty().addListener(new ChangeListenerImpl(item));

			boolean equals = key.equals(c.getDefaultTarget());
			item.setChk(equals);
			if (equals)
				item.setTargetDesc("[default]  ".concat(desc));

			return item;
		}).filter(v -> v != null).collect(Collectors.toList());

		tbTargets.getItems().addAll(collect);

		tbTargets.setEditable(true);
		tcChkTarget.setEditable(true);
		tcTargetName.setEditable(false);
		tcTargetDesc.setEditable(false);

		//		Platform.runLater(()->{
		//
		//		});

	}

	@FXML
	public void runOnAction() {
		if (c != null) {

			FxUtil.showLoading(owner, new Task<String>() {

				@Override
				protected String call() throws Exception {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					tarCheckedList.stream().findFirst().ifPresent(item -> {
						c.setTarget(item.getTargetName());
						c.setOut(out);

						c.setErr(out);
						c.run();
					});

					String string = out.toString();

					Platform.runLater(() -> {
						FxUtil.createSimpleTextAreaAndShow(string, stage -> {
							stage.initOwner(owner);
							stage.setWidth(1200d);
							stage.setHeight(800d);
							stage.setTitle("Build Result Message");
						});
					});

					return string;
				}
			});

		}
	}

	@FXML
	public void btnBuildFileOpenOnAction() {
		FileUtil.openFile(this.buildFile);
	}

	@FXML
	public void btnCloseOnAction() {
		if (owner != null)
			owner.close();
	}
}
