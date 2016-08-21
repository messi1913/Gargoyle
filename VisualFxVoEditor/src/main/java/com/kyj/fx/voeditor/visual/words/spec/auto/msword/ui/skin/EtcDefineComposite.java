/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.ui.skin
 *	작성일   : 2016. 8. 21.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.skin;

import java.util.ArrayList;
import java.util.List;

import com.kyj.fx.voeditor.visual.framework.annotation.FXMLController;
import com.kyj.fx.voeditor.visual.util.FxUtil;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.ui.tabs.AbstractSpecTab;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.EtcDefineDVO;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/***************************
 * 
 * 기타정의사항
 * 
 * @author KYJ
 *
 ***************************/
@FXMLController(value = "EtcDefineView.fxml", isSelfController = true)
public class EtcDefineComposite extends BorderPane {

	private AbstractSpecTab abtabPane;

	@FXML
	private TextArea txtCodeMasterTable, txtMessage, txtAuthChk, txtRelFct, txtModuleEffect, txtTestCaut, txtDeployCaut, txtEtc;
	private EtcDefineDVO etcDefineDVO = new EtcDefineDVO();

	public EtcDefineComposite(AbstractSpecTab abtabPane) throws Exception {
		this.abtabPane = abtabPane;
		FxUtil.loadRoot(EtcDefineComposite.class, this);
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 기타 정의사항 모델 리턴.
	 * 
	 * @return
	 ********************************/
	public EtcDefineDVO getEtcDefineDVO() {
		etcDefineDVO.setCodeMasterList(getTxtCodeMasterTable());
		etcDefineDVO.setMessageList(getTxtMessage());
		etcDefineDVO.setChkAuthList(getTxtAuthChk());
		etcDefineDVO.setRelativeFactoryList(getTxtRelFct());
		etcDefineDVO.setEffectModuleList(getTxtModuleEffect());
		etcDefineDVO.setCautionTestList(getTxtTestCaut());
		etcDefineDVO.setCautionDeployList(getTxtDeployCaut());
		etcDefineDVO.setEtcList(getTxtEtc());
		return etcDefineDVO;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 코드 마스터 정의
	 * 
	 * @return
	 ********************************/
	List<String> getTxtCodeMasterTable() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtCodeMasterTable.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 메세지 처리
	 * 
	 * @return
	 ********************************/
	List<String> getTxtMessage() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtMessage.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 권한 체크
	 * 
	 * @return
	 ********************************/
	List<String> getTxtAuthChk() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtAuthChk.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 관련 사업장
	 * 
	 * @return
	 ********************************/
	List<String> getTxtRelFct() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtRelFct.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 모듈 영향도
	 * 
	 * @return
	 ********************************/
	List<String> getTxtModuleEffect() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtModuleEffect.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 테스트시 주의사항
	 * 
	 * @return
	 ********************************/
	List<String> getTxtTestCaut() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtTestCaut.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 운영이관시 주의사항
	 * 
	 * @return
	 ********************************/
	List<String> getTxtDeployCaut() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtDeployCaut.getText());
		return arrayList;
	}

	/********************************
	 * 작성일 : 2016. 8. 21. 작성자 : KYJ
	 *
	 * 기타
	 * 
	 * @return
	 ********************************/
	List<String> getTxtEtc() {
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(txtEtc.getText());
		return arrayList;
	}

}
