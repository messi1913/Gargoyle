/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 6. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.AbstractOpenClassResourceView.RESOURCE_TYPE;
import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.momory.SharedMemory;
import com.kyj.fx.voeditor.visual.util.FileUtil;

import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class GagoyleWorkspaceOpenResourceView {

	private AbstractOpenClassResourceView<File> delegator;

	public GagoyleWorkspaceOpenResourceView() throws Exception {
		delegator = new AbstractOpenClassResourceView<File>(RESOURCE_TYPE.SOURCE) {

			@Override
			public boolean isMatch(File value, String text) {

				//				return value.getAbsolutePath().equalsIgnoreCase(text);

				return value.getAbsolutePath().toUpperCase().indexOf(text.toUpperCase()) >= 0;
				//				return value.getAbsolutePath().indexOf(text) >= 0;

			}

			public void btnRefleshOnMouseClick(MouseEvent event) {
				//setResources(getClassesByLoader(SharedMemory.loadClasses(true)));

				setResources(getClassesByLoader(SharedMemory.loadSources(true)));
				lvResources.getItems().addAll(getResources());
			}

			@Override
			public List<File> getClassesByLoader(List<ProjectInfo> list) {

				if (list == null)
					return Collections.emptyList();

				List<File> arrayList = new ArrayList<>();

				list.stream().filter(v -> v != null).forEach(proj -> {

					List<File> collect = proj.getJavaSources().stream().filter(v -> v != null).map(c -> {

						// String filePath = c.replace('.', File.separatorChar);
						// filePath = filePath.concat(".class");
						File file = new File(c);
						return file;
					}).filter(/*파일이 워크스페이스내에서 삭제된경우 exists가 false가 리턴될 수있다.*/ v -> v.exists()).collect(Collectors.toList());

					arrayList.addAll(collect);
				});

				return arrayList;

			}

			@Override
			public StringConverter<File> stringConverter() {
				return new StringConverter<File>() {

					@Override
					public String toString(File object) {

						return String.format("%s   ///  [%s]", object.getName(), FileUtil.toRelativizeForGagoyle(object));
					}

					@Override
					public File fromString(String string) {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}

			@Override
			protected void init() {

			}

		};

		delegator.setTitle("Resource Search  [GagoyleWorkspaceOpenResourceView]");
	}

	public ResultDialog<File> show() {
		return delegator.show(null, true);
	}

}
