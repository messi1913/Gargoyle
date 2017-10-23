/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.tree
 *	작성일   : 2017. 10. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.example;

import com.kyj.fx.voeditor.visual.component.tree.XMLTreeView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author KYJ
 *
 */
public class XMLTreeViewExam extends Application {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 23.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		launch(args);

	}

	

	@Override
	public void start(Stage primaryStage) throws Exception {

		XMLTreeView tvXml = new XMLTreeView();
		
		

		StringBuffer sb = new StringBuffer();
		sb.append(
				"<TransactionRequest><Header><PlantId>P100</PlantId><TransactionId>1100000809</TransactionId><SourceSystem>ERP</SourceSystem><DestinationSystem>MES</DestinationSystem><TransactionType>A04</TransactionType></Header><Data><Plant>P120</Plant><ProductionOrderNo>1013480</ProductionOrderNo><ProductNo>A1CM08</ProductNo><ProductQuantity>3.300</ProductQuantity><BOMBaseQuantity>3.300</BOMBaseQuantity><ProductUOM>KG</ProductUOM><BatchNo>3104729</BatchNo><BatchStartDateTime>20170920000000</BatchStartDateTime><BatchEndDateTime>20171226000000</BatchEndDateTime><OperationStartDateTime>20171226000000</OperationStartDateTime><OperationEndDateTime>20171226000000</OperationEndDateTime><BOMAlternativeNo>01</BOMAlternativeNo><ReservationNo>8399381</ReservationNo><OperationNo>0010</OperationNo><OperationDescription>A1C3L100mMMSX</OperationDescription><MBRDocumentNo>A1CM08</MBRDocumentNo><Components><Component><ReservationItemNo>5</ReservationItemNo><ComponentCode>1000159</ComponentCode></Component><Component><ReservationItemNo>6</ReservationItemNo><ComponentCode>1000161</ComponentCode></Component><Component><ReservationItemNo>8</ReservationItemNo><ComponentCode>9000118</ComponentCode></Component><Component><ReservationItemNo>7</ReservationItemNo><ComponentCode>9002474</ComponentCode></Component></Components></Data></TransactionRequest>");
		String xml = sb.toString();
		
		
		tvXml.setXml(xml);

		primaryStage.setScene(new Scene(tvXml));
		primaryStage.show();

	}

}
