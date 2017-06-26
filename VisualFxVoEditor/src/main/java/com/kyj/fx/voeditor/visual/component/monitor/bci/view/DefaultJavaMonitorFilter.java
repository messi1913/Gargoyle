/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 4. 5.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.monitor.bci.view;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.bci.monitor.ApplicationModel;

/**
 * @author KYJ
 *
 */
public class DefaultJavaMonitorFilter implements Predicate<ApplicationModel> {

	private static Logger LOGGER = LoggerFactory.getLogger(DefaultJavaMonitorFilter.class);

	@Override
	public boolean test(ApplicationModel model) {
		if (model != null) {
			String argument = model.getArgument();
			LOGGER.debug(argument);
			return true;
		}
		return false;
	}

}
