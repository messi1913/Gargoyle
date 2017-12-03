package com.kyj.fx.voeditor.visual.framework.keyboard;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 편의성을 위한 키 이벤트 체크 함수
 * 
 * @author KYJ
 *
 */
public class FxKey {

	public FxKey() {

	}

	/**
	 * 컨트롤 알트 쉬프트 모두 눌렀는지 확인 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 1.
	 * @param e
	 * @return
	 */
	public static boolean isControlShiftAltDown(KeyEvent e) {
		if (e.isControlDown() && e.isAltDown() && e.isShiftDown())
			return true;
		return false;
	}

	/**
	 * 컨트롤 알트 쉬프트 중 하나라도 누른 상태인지 확인 <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 1.
	 * @param e
	 * @return
	 */
	public static boolean isControlShiftAltOrDown(KeyEvent e) {
		if (e.isControlDown() || e.isAltDown() || e.isShiftDown())
			return true;
		return false;
	}

	/**
	 * 컨트롤 알트 쉬프트가 눌리지않으면서 checkCode만 눌렸으면 true <br/>
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 1.
	 * @param e
	 *            키 이벤트
	 * @param checkCode
	 *            눌렸는지 확인하고자하는 키 코드
	 * @return
	 * 
	 */
	public static boolean isSingleClick(KeyEvent e, KeyCode checkCode) {
		return isSingleClick(e, checkCode, false);
	}

	/**
	 * 컨트롤 알트 쉬프트가 눌리지않으면서 checkCode만 눌렸으면 true <br/>
	 * 
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 12. 1.
	 * @param e
	 *            키 이벤트
	 * @param checkCode
	 *            눌렸는지 확인하고자하는 키 코드
	 * @param checkConsumed
	 *            이 값이 true인경우 이전 이벤트에서 처리가 완료되었으면 처리하지않음.
	 * @return
	 */
	public static boolean isSingleClick(KeyEvent e, KeyCode checkCode, boolean checkConsumed) {
		if (FxKey.isControlShiftAltOrDown(e)) {
			if (checkConsumed && e.isConsumed())
				return false;
			if (e.getCode() == checkCode) {
				if (checkConsumed)
					e.consume();
				return true;
			}

		}
		return false;
	}
}
