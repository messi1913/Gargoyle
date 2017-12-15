/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2017. 12. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.fxloader.FXMLController;
import com.kyj.fx.fxloader.FxPostInitialize;
import com.kyj.fx.voeditor.visual.component.NumberTextField;
import com.kyj.fx.voeditor.visual.util.DialogUtil;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

/**
 * @author KYJ
 *
 */
@FXMLController(value = "SVNAdvanceView.fxml", isSelfController = true)
abstract class SVNAdvanceComposite extends BorderPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(SVNAdvanceComposite.class);

	@FXML
	private NumberTextField txtRevision;
	@FXML
	private TextArea txtMessage;
	@FXML
	private TextField txtAuthor;

	private ObjectProperty<SVNAdvanceOption> advanceOption = new SimpleObjectProperty<>();

	private SVNItem svnItem;

	/**
	 */
	public SVNAdvanceComposite(SVNItem svnItem) {
		this.svnItem = svnItem;
		FxUtil.loadRoot(SVNAdvanceComposite.class, this, err -> {
			LOGGER.error(ValueUtil.toString(err));
		});

	}

	@FXML
	public void initialize() {
		txtRevision.addEventHandler(KeyEvent.KEY_PRESSED, txtRevisionOnKeyPress);
	}

	@FxPostInitialize
	public void after() {
		try {
			long latestRevision = svnItem.getLatestRevision();
			txtRevision.setText(String.valueOf(latestRevision));
		} catch (SVNException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}

	private EventHandler<KeyEvent> txtRevisionOnKeyPress = ev -> {
		if (ev.getCode() == KeyCode.ENTER) {
			if (ev.isConsumed())
				return;
			ev.consume();
			int revision = Integer.parseInt(txtRevision.getText());
			try {
				List<SVNLogEntry> entries = svnItem.getManager().log(svnItem.getPath(), revision);
				txtMessage.setText(getMessage(revision, entries));
				txtAuthor.setText(getAuthor(revision, entries));
			} catch (Exception e) {
				DialogUtil.showExceptionDailog(e);
			}
		}
	};

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 */
	@FXML
	public void btnSaveOnAction() {

		int parseInt = Integer.parseInt(txtRevision.getText());

		SVNAdvanceOption value = new SVNAdvanceOption();
		value.setRevision(parseInt);
		value.setMessage(txtMessage.getText());
		advanceOption.set(value);
		
		onSave();

	}

	protected abstract String getMessage(int revision, List<SVNLogEntry> entries);

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @return
	 */
	public final SVNAdvanceOption getValue() {
		return advanceOption.get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @param revision
	 * @return
	 */
	protected abstract String getAuthor(int revision, List<SVNLogEntry> entries);

	/**
	 * save 버튼 클릭시 발생 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 15.
	 * @return
	 */
	protected abstract void onSave();

}
