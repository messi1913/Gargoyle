/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.popup
 *	작성일   : 2016. 6. 11.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.popup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyj.fx.voeditor.visual.component.ResultDialog;
import com.kyj.fx.voeditor.visual.component.popup.AbstractOpenClassResourceView.RESOURCE_TYPE;
import com.kyj.fx.voeditor.visual.loder.ProjectInfo;
import com.kyj.fx.voeditor.visual.util.FileUtil;

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

			@Override
			public List<File> getClassesByLoader(List<ProjectInfo> list) {

				List<File> arrayList = new ArrayList<>();

				list.forEach(proj -> {

					List<File> collect = proj.getJavaSources().stream().map(c -> {

						// String filePath = c.replace('.', File.separatorChar);
						// filePath = filePath.concat(".class");

						File file = new File(c);

						return file;
					}).collect(Collectors.toList());

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
