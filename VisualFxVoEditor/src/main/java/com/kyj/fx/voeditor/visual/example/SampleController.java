/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.example
 *	작성일   : 2016. 5. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.framework.annotation.FxPostInitialize;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

/***************************
 *
 * FxUtil.load 메소드를 활용하여
 *
 * @FXMLController 어노테이션과 @FxPostInitialize 을 활용해보는 example
 *
 * @author KYJ
 *
 ***************************/
@FXMLController("ColumnExam3.fxml")
public class SampleController {

	@SuppressWarnings("rawtypes")
	@FXML
	public TableColumn colCode;

	public SampleController() {
		System.out.println("construct method");
	}

	@FXML
	public void initialize() {
		System.out.println("initialize method");
	}

	@FxPostInitialize
	public void hello() {
		System.out.println("postConstrunt annotation");
	}

}
