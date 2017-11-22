/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.util.List;

import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;

import javafx.scene.input.MouseEvent;

/**
 * 클래스 리소스정보를 로딩해온다.
 *
 * @author KYJ
 *
 */
public abstract class AbstractOpenClassResourceView<R> extends ResourceView<R> {

	private RESOURCE_TYPE resourceType;

	enum RESOURCE_TYPE {
		CLASS, SOURCE, SOURCE_CLASS
	}

	public AbstractOpenClassResourceView(String data) throws Exception {
		this(RESOURCE_TYPE.CLASS, data);
	}

	public AbstractOpenClassResourceView() throws Exception {
		this(RESOURCE_TYPE.CLASS, "");
	}

	public AbstractOpenClassResourceView(RESOURCE_TYPE resourceType) throws Exception {
		this(resourceType, "");
	}

	public AbstractOpenClassResourceView(RESOURCE_TYPE resourceType, String data) throws Exception {
		this.resourceType = resourceType;
		this.data.set(data);
		findResource();
	}

	protected void findResource() {
		switch (this.resourceType) {
		case CLASS:
			setResources(getClassesByLoader(SharedMemory.loadClasses()));
			break;
		case SOURCE:
			setResources(getClassesByLoader(SharedMemory.loadSources()));
			break;
		case SOURCE_CLASS:
			setResources(getClassesByLoader(SharedMemory.listSourcesConvertClassName()));
		}

//		lvResources.getItems().setAll(getResources());
		 lvResources.getItems().addAll(getResources());
	}

	public void btnRefleshOnMouseClick(MouseEvent event) {
		// setResources(getClassesByLoader(SharedMemory.loadClasses(true)));
		findResource();
		lvResources.getItems().addAll(getResources());
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
	public abstract List<R> getClassesByLoader(List<ProjectInfo> list);
}
