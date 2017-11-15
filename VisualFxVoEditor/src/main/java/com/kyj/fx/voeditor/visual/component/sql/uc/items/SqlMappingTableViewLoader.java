/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.builder
 *	작성일   : 2017. 9. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.uc.items;

import java.io.IOException;

import com.kyj.fx.voeditor.visual.framework.loader.core.RegistItem;
import com.kyj.fx.voeditor.visual.framework.loader.ui.MakeBusinessFrameComposite;

/**
 * @author KYJ
 *
 */
public class SqlMappingTableViewLoader extends MakeBusinessFrameComposite {

	public SqlMappingTableViewLoader() throws IOException {
		super();
		try {
			addItem(new RegistItem("A0001", "Database", new SqlMappingTableViewRegister()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
