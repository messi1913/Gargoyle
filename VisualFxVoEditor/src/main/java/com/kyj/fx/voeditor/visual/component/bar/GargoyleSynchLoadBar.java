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

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
public class GargoyleSynchLoadBar<V> extends Service<V> {

	public static final ExecutorService newSingleThreadExecutor = ExecutorDemons.newSingleThreadExecutor();

	private Task<V> task;
	private Stage stage;
	private Window owner;

	public enum Type {
		synch, asynch
	}

	private Type type;

	public GargoyleSynchLoadBar(Window owner, Task<V> task) {
		this(owner, task, Type.synch);
	}

	public GargoyleSynchLoadBar(Window owner, Task<V> task, Type type) {
		this.task = task;
		this.owner = owner;
		this.type = type;

		stage = new Stage();

		StackPane stackPane = new StackPane();
		double radius = owner.getWidth() / 10;
		for (double r = radius; r >= (radius - 40) && radius >= 0; r -= 10) {
			JFXSpinner spinner = new JFXSpinner();
			spinner.setRadius(r);
			stackPane.getChildren().add(spinner);
		}

		stage.setScene(new Scene(stackPane, stackPane.prefWidth(0), stackPane.prefHeight(0), Color.TRANSPARENT));

		stage.setX(owner.getX() + (owner.getWidth() / 2) - (stage.getScene().getWidth() / 2));
		stage.setY(owner.getY() + (owner.getHeight() / 2) - (stage.getScene().getHeight() / 2));

		//		stage.setScene(new Scene(root));
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setAlwaysOnTop(true);
		stage.initOwner(owner);

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
		switch (this.type) {
		case synch:
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
			break;
		case asynch:
			stage.show();
			break;
		}

		super.start();

	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#cancelled()
	 */
	@Override
	protected void cancelled() {
		super.cancelled();
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#succeeded()
	 */
	@Override
	protected void succeeded() {
		super.succeeded();
		stage.close();
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
		super.failed();
		stage.close();
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#executeTask(javafx.concurrent.Task)
	 */
	@Override
	protected void executeTask(Task<V> task) {
		super.executeTask(task);

	}

}
