/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2015. 10. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.scene.input.MouseEvent;

/**
 * 클래스 리소스정보를 로딩해온다.
 *
 * 객체 이름에 DVO, SVO으로 끝나는 이름들을 필터링하여 보여준다.
 *
 * @author KYJ
 *
 */
public class MeerketAbstractVoOpenClassResourceView extends BaseOpenClassResourceView {

	/**
	 * 파일명 끝부분에 적용할 필터링 부분
	 *
	 * @최초생성일 2015. 10. 27.
	 */
	private static List<String> fileNameFilter;

	static {
		String VO_RESOURCE_FILTERING_NAME = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.VO_RESOURCE_FILTERING_NAME);
		fileNameFilter = Arrays
				.asList(ValueUtil.decode(VO_RESOURCE_FILTERING_NAME, VO_RESOURCE_FILTERING_NAME, "DVO;SVO;VO;Vo").toString().split(";"));
	}

	/**
	 * @throws Exception
	 */
	public MeerketAbstractVoOpenClassResourceView() throws Exception {
		super(RESOURCE_TYPE.SOURCE_CLASS);

	}

	@Override
	public void btnRefleshOnMouseClick(MouseEvent event) {
		setResources(getClassesByLoader(SharedMemory.loadSourcesConvertClassName(true)));
		lvResources.getItems().setAll(getResources());
	}

	/**
	 * @param data
	 *            필드에 기본적으로 입력할 텍스트
	 * @throws Exception
	 */
	public MeerketAbstractVoOpenClassResourceView(String data) throws Exception {
		super(RESOURCE_TYPE.SOURCE_CLASS, data);
	}

	/**
	 * 로더로부터 클래스목록을 반환받는다.
	 */
	@Override
	public List<String> getClassesByLoader(List<ProjectInfo> list) {
		List<String> arrayList = new ArrayList<String>();
		list.forEach(proj -> {
			List<String> sources = proj.getJavaSources();
//			List<String> sources = proj.getClasses();

			// FileUtil.toRelativizeForGagoyle(userDir);
			List<String> collect = sources.parallelStream()
					// .map(name -> FileUtil.getFileNameWithoutExtension(name))
					.filter(clazz -> {
						for (String end : fileNameFilter) {
							if (clazz.lastIndexOf(end) >= 0)
								return true;
						}
						return false;
					})
					// .map(name ->{
					//
					// Path projPath = Paths.get(proj.getProjectDir());
					//
					// Path path = Paths.get(name);
					// })
					.collect(Collectors.toList());
			arrayList.addAll(collect);
		});
		return arrayList;
	}
}
