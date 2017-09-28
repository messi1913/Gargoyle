/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.initalize
 *	작성일   : 2017. 9. 26.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main.initalize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.DbExecListener;
import com.kyj.fx.voeditor.visual.util.DbUtil;

/**
 * @author KYJ
 *
 */
@GagoyleInitializable
public class DbUtilLoggingInitalizer implements Initializable, DbExecListener {

	private static final Logger LOGGER = LoggerFactory.getLogger("DB - LOGGER");

	@Override
	public void initialize() throws Exception {
		DbUtil.registQuertyListener(this);

	}

	@Override
	public void onQuerying(String query) {
		LOGGER.debug(query);
	}

}
