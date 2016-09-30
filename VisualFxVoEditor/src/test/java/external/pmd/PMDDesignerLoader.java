/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.pmd
 *	작성일   : 2016. 9. 30.
 *	작성자   : KYJ
 *******************************/
package external.pmd;

import com.kyj.fx.voeditor.visual.component.pmd.DesignerFxComposite;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class PMDDesignerLoader extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		//		SwingNode swingContainerSplitPane = new SwingNode();
		//		SwingNode swingMenuBar = new SwingNode();
		BorderPane root = new DesignerFxComposite();

		//		root.setBottom(new Label("javafx"));

		//		createSwingContent(swingContainerSplitPane, );
		//		createSwingContent(swingMenuBar, designerFx.getJMenuBar());

		Scene value = new Scene(root, 1200, 800);

		primaryStage.setScene(value);
		primaryStage.show();
	}
}
