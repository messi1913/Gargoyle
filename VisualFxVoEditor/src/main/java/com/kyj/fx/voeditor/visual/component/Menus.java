/********************************
 *	프로젝트 : sos-client
 *	패키지   : com.samsung.sds.sos.client.component.grid
 *	작성일   : 2015. 10. 14.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

/**
 * 버튼 활성화 정보
 *
 * @author KYJ
 *
 */
public class Menus {

	public static final int ADD = 0x001;
	public static final int DELETE = 0x002;

	public static final int UP = 0x004;
	public static final int DOWN = 0x008;
	/**
	 * 추가, 삭제, 저장, 수정 버튼
	 *
	 * @최초생성일 2015. 10. 14.
	 */
	private static final int ADD_DELETE = Menus.ADD | Menus.DELETE;

	public static final boolean isAdd(int mod) {
		return (mod & ADD) != 0;
	}

	public static final boolean isDelete(int mod) {
		return (mod & DELETE) != 0;
	}

	public static final boolean isUp(int mod) {
		return (mod & UP) != 0;
	}

	public static final boolean isDown(int mod) {
		return (mod & DOWN) != 0;
	}

	public static final int useCudButtons() {
		return ADD_DELETE;
	}

}
