/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.bar
 *	작성일   : 2016. 10. 7.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bar;

import java.util.concurrent.ExecutorService;

import com.jfoenix.controls.JFXSpinner;
import com.kyj.fx.voeditor.visual.framework.thread.ExecutorDemons;
import com.sun.javafx.stage.StageHelper;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * 로딩바
 * @author KYJ
 *
 */
public abstract class GargoyleLoadBar<V> extends Service<V> {

	//기본 Executors로 변경
	public static final ExecutorService newSingleThreadExecutor = ExecutorDemons.getGargoyleSystemExecutorSerivce();

	private Task<V> task;
	private Stage stage;
	private Window owner;

	public enum Type {
		synch, asynch
	}

	public abstract Type getType();

	private ChangeListener<? super Number> xListener = (oba, o, n) -> {
		stage.setX(n.doubleValue() + (owner.getWidth() / 2) - (stage.getScene().getWidth() / 2));
	};
	private ChangeListener<? super Number> yListener = (oba, o, n) -> {
		stage.setY(n.doubleValue() + (owner.getHeight() / 2) - (stage.getScene().getHeight() / 2));
	};

	public GargoyleLoadBar(Window owner, Task<V> task) {
		this.task = task;
		this.owner = owner;

		if (owner == null) {
			ObservableList<Stage> stages = StageHelper.getStages();
			this.owner = stages.stream().findFirst().get();
		}

		stage = new Stage();

		StackPane stackPane = new StackPane();
		double radius = this.owner.getWidth() / 10;
		for (double r = radius; r >= (radius - 40) && radius >= 0; r -= 10) {
			JFXSpinner spinner = new JFXSpinner();
			//			spinner.setStyle("-fx-background-color:transparent ; -fx-fill : transparent");
			spinner.setRadius(r);
			stackPane.getChildren().add(spinner);
		}
		//		stackPane.setBackground(Background.EMPTY);
		stackPane.getStyleClass().add("loading-bar");
		stackPane.setStyle("-fx-background-color: transparent ;");
		//		scene.getStylesheets().add(SkinManager.getInstance().getSkin());

		Scene scene = new Scene(stackPane, stackPane.prefWidth(0), stackPane.prefHeight(0));
		scene.setFill(null);
		stage.setScene(scene);

		stage.setX(this.owner.getX() + (this.owner.getWidth() / 2) - (stage.getScene().getWidth() / 2));
		stage.setY(this.owner.getY() + (this.owner.getHeight() / 2) - (stage.getScene().getHeight() / 2));

		this.owner.xProperty().addListener(xListener);
		this.owner.yProperty().addListener(yListener);

		//		stage.setScene(new Scene(root));
		stage.initStyle(StageStyle.TRANSPARENT);

		stage.setAlwaysOnTop(false);
		stage.initOwner(this.owner);
		stage.addEventHandler(KeyEvent.KEY_PRESSED, this::stageOnKeyPress);

	}

	public void stageOnKeyPress(KeyEvent e) {
		if (KeyCode.ESCAPE == e.getCode()) {
			this.task.cancel();
			this.cancel();
		}

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<V> createTask() {
		return task;
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#start()
	 */
	@Override
	public void start() {
		switch (getType()) {
		case synch:
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			break;
		case asynch:
			stage.show();
			break;
		default:
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
		}

		super.start();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#cancel()
	 */
	@Override
	public boolean cancel() {
		stage.xProperty().removeListener(xListener);
		stage.yProperty().removeListener(yListener);
		stage.close();
		return super.cancel();
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#cancelled()
	 */
	@Override
	protected void cancelled() {
		stage.xProperty().removeListener(xListener);
		stage.yProperty().removeListener(yListener);
		stage.close();
		super.cancelled();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#succeeded()
	 */
	@Override
	protected void succeeded() {
		stage.xProperty().removeListener(xListener);
		stage.yProperty().removeListener(yListener);
		stage.close();
		super.succeeded();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#running()
	 */
	@Override
	protected void running() {
		super.running();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#failed()
	 */
	@Override
	protected void failed() {
		stage.xProperty().removeListener(xListener);
		stage.yProperty().removeListener(yListener);
		stage.close();
		super.failed();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#executeTask(javafx.concurrent.Task)
	 */
	@Override
	protected void executeTask(Task<V> task) {
		super.executeTask(task);
	}

}
