/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main
 *	작성일   : 2017. 10. 9.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.main;

import java.util.Properties;

/**
 * @author KYJ
 *
 */
public class GargoyleArgumentParser {

	protected String[] args;

	private Properties prop = new Properties();

	public GargoyleArgumentParser(String[] args) {
		this.args = args;
		parse();
	}

	public void parse() {
		if (this.args == null)
			return;
		for (int i = 0, max = args.length - 1; i < max; i++) {
			String arg = args[i];
			boolean isCommand = isCommand(arg);
			String key = arg.substring(1);
			String value = null;
			if (isCommand) {

				if (i + 1 > max) {
					break;
				}

				if (isCommand(args[i + 1])) {
					// continue;
				} else {
					value = args[i + 1];
				}

			}
			
			if(value == null)
				value = "";
			prop.put(key, value);
		}
	}

	private boolean isCommand(String arg) {
		if (arg.startsWith("-")) {
			return true;
		}
		return false;
	}

	/**
	 * key : -duplCheck value : true/false
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 9.
	 * @return
	 */
	public boolean checkAppDupl() {
		return "true".equals(prop.get("enableAppDupledCheck"));
	}
	
	/**
	 * return version
	 * @작성자 : KYJ
	 * @작성일 : 2017. 10. 9. 
	 * @return
	 */
	public String getVersion() {
		return prop.getProperty("version", "eclipse-run");
	}
}
