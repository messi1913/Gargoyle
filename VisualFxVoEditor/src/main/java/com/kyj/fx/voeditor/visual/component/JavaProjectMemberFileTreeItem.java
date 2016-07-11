/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component
 *	작성일   : 2016. 07. 11
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component;

/**
 * 자바프로젝트안에 속하는 멤버파일인경우 사용.
 *
 * @author KYJ
 *
 */
public class JavaProjectMemberFileTreeItem extends FileTreeItem {

	public JavaProjectMemberFileTreeItem(FileWrapper fileWrapper) {
		super(fileWrapper);
	}

	@Override
	protected String getMetadata() {
		return "";
	}

}