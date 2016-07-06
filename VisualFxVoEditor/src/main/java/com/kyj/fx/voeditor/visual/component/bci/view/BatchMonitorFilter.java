/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 4. 5.
 *	프로젝트 : BATCH 프로젝트
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.bci.view;

import java.util.function.Predicate;

import com.kyj.bci.monitor.ApplicationModel;
import com.kyj.fx.voeditor.visual.momory.MonitorProperties;

/**
 * @author KYJ
 *
 */
public class BatchMonitorFilter implements Predicate<ApplicationModel> {

	private static final String BATCH_MONITOR_ARGUMENT = MonitorProperties.getInstance().get(MonitorProperties.MONITOR_ARGUMENT);

	@Override
	public boolean test(ApplicationModel model) {
		if (model != null) {
			String argument = model.getArgument();
			if (argument != null && argument.contains(BATCH_MONITOR_ARGUMENT)) {
				return true;
			}
		}

		return false;
	}

}
