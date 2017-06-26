/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.bci.monitor
 *	작성일   : 2017. 6. 26.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import java.util.stream.Stream;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * @author KYJ
 *
 */
public class JstatOutputFormat extends OptionOutputFormatter {

	/**
	 * @param arg0
	 * @param arg1
	 * @throws MonitorException
	 */
	public JstatOutputFormat(MonitoredVm arg0, OptionFormat arg1) throws MonitorException {
		super(arg0, arg1);
		
	}

	@Override
	public String getHeader() throws MonitorException {
		String header = super.getHeader();
		String[] split = header.split(" ");
		return "\t".concat(Stream.of(split).filter(v -> !v.isEmpty()).reduce((str1,str2) -> str1.concat("\t").concat(str2)).get());
//		return header;
	}

	@Override
	public String getRow() throws MonitorException {
		
		String r = super.getRow();
		String[] split = r.split(" ");
		
		
		return "KB\t".concat(Stream.of(split).filter(v -> !v.isEmpty()).reduce((str1,str2) -> str1.concat("\t").concat(str2)).get()); 
		
		
		
		
//		return super.getRow();
	}

}
