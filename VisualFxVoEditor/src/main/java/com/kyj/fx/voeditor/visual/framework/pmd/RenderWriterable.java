/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.pmd
 *	작성일   : 2016. 10. 31.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.pmd;

import java.io.Writer;

/**
 * @author KYJ
 *
 */
public interface RenderWriterable {

	Writer getWriter();

	String getResultString();

	void close();
}
