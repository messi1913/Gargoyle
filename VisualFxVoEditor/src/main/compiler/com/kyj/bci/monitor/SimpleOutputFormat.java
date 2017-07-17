/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.bci.monitor
 *	작성일   : 2017. 6. 26.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * @author KYJ
 *
 */
class SimpleOutputFormat extends OptionOutputFormatter {

	/**
	 * @param arg0
	 * @param arg1
	 * @throws MonitorException
	 */
	public SimpleOutputFormat(MonitoredVm arg0, OptionFormat arg1) throws MonitorException {
		super(arg0, arg1);
	}

	@Override
	public String getHeader() throws MonitorException {
		return "";
	}

	@Override
	public String getRow() throws MonitorException {
		String r = super.getRow();
		return r;
	}

}
