/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 26.
 *	작성자   : KYJ
 * 변경내역
 *
 * 2016.06.11 계층구조를 쌓음.   AbstractOpenClassResourceView를 추상화 시켜 상속받아 구현하는 모델로 변경
 *
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.util.ArrayList;
import java.util.List;

import com.kyj.fx.voeditor.visual.loder.ProjectInfo;

import javafx.util.StringConverter;

/**
 * 클래스 리소스정보를 로딩해온다.
 *
 * @author KYJ
 *
 */
public class BaseOpenClassResourceView extends AbstractOpenClassResourceView<String> {

	/**
	 * 리소스 다이얼로그를 오픈한다.
	 *
	 * @throws Exception
	 */
	public BaseOpenClassResourceView() throws Exception {
		this("");
	}

	/**
	 * 리소스 다이얼로그를 오픈한다.
	 *
	 * @param data
	 *            다이얼로그를 오픈할때 텍스트필드에 기본값으로 셋팅할 텍스트
	 * @throws Exception
	 */
	public BaseOpenClassResourceView(String data) throws Exception {
		super(data);
	}

	/**
	 *
	 * 로더로부터 클래스목록을 반환받는다. 특화된 리소스로더 팝업이 필요한 경우는 이 함수를 오버라이드해서 구현하도록한다.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 27.
	 * @param list
	 * @return
	 */
	public List<String> getClassesByLoader(List<ProjectInfo> list) {
		List<String> arrayList = new ArrayList<String>();
		list.forEach(proj -> {
			arrayList.addAll(proj.getClasses());
		});
		return arrayList;
	}

	@Override
	public boolean isMatch(String value, String check) {
		return value.indexOf(check) >= 0;
	}

	@Override
	public StringConverter<String> stringConverter() {

		return new StringConverter<String>() {

			@Override
			public String toString(String object) {
				return object;
			}

			@Override
			public String fromString(String string) {
				return string;
			}
		};
	}

	@Override
	protected void init() {

	}

}
