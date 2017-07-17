/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.monitor.jstat.view
 *	작성일   : 2017. 7. 17.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.monitor.jstat.view;

import java.io.IOException;

import com.kyj.fx.voeditor.visual.main.layout.CloseableParent;

/**
 * @author KYJ
 *
 */

public class JStateCompositeWrapper extends CloseableParent<JStateComposite> {

	public JStateCompositeWrapper(int pid) {
		super(new JStateComposite(pid));
	}

	@Override
	public void close() throws IOException {
		getParent().close();
	}

}
