/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.bci.monitor
 *	작성일   : 2017. 6. 26.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;

/**
 * @author KYJ
 *
 */
public class JsonOutputFormat extends OptionOutputFormatter {

	private List<String> headers;
	private SnapShotListener listener;

	/**
	 * @param arg0
	 * @param arg1
	 * @throws MonitorException
	 */
	public JsonOutputFormat(MonitoredVm arg0, OptionFormat arg1, SnapShotListener listener) throws MonitorException {
		super(arg0, arg1);
		this.listener = listener;
	}

	@Override
	public String getHeader() throws MonitorException {
		String header = super.getHeader();
		String[] split = header.split(" ");
		headers = Stream.of(split).filter(v -> !v.isEmpty()).collect(Collectors.toList());

		return "";
	}

	@Override
	public String getRow() throws MonitorException {
		String r = super.getRow();
		long date = System.currentTimeMillis();
		List<String> dataArray = Stream.of(r.split(" ")).filter(v -> !v.isEmpty()).collect(Collectors.toList());

		JSONObject object = new JSONObject();
		for (int i = 0, max = headers.size(); i < max; i++) {
			object.put(headers.get(i), dataArray.get(i));
		}

		object.put("date", String.valueOf(date));

		if (listener != null) {
			listener.onListen(object);
		}

		return object.toString();
	}

}
