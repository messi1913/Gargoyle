/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.external.banner
 *	작성일   : 2016. 8. 2.
 *	작성자   : KYJ
 *******************************/
package com.kyj.external.banner;

import java.io.PrintWriter;

public class Application {

	private static final String DEFAULT_MAX_WIDTH = "72";
	private static final String DEFAULT_ASPECT_RATIO = "0.5";

	public static void main(String[] args) {

		
		ImageBanner banner = new ImageBanner(Application.class.getResource("api.jpg"));
		String bannerStr = banner.printBanner(Integer.parseInt(DEFAULT_MAX_WIDTH), Double.parseDouble(DEFAULT_ASPECT_RATIO), true, false);
//		System.out.println(bannerStr);

		System.out.println(bannerStr.replace("${AnsiColor.DEFAULT}", "\u001B[39m").replace("${AnsiColor.BLACK}", "\u001B[30m")
				.replace("${AnsiColor.RED}", "\u001B[31m").replace("${AnsiColor.GREEN}", "\u001B[32m")
				.replace("${AnsiColor.YELLOW}", "\u001B[33m").replace("${AnsiColor.BLUE}", "\u001B[34m")
				.replace("${AnsiColor.MAGENTA}", "\u001B[35m").replace("${AnsiColor.CYAN}", "\u001B[36m")
				.replace("${AnsiColor.WHITE}", "\u001B[37m").replace("${AnsiColor.BRIGHT_BLACK}", "\u001B[90m")
				.replace("${AnsiColor.BRIGHT_RED}", "\u001B[91m").replace("${AnsiColor.BRIGHT_GREEN}", "\u001B[92m")
				.replace("${AnsiColor.BRIGHT_YELLOW}", "\u001B[93m").replace("${AnsiColor.BRIGHT_BLUE}", "\u001B[94m")
				.replace("${AnsiColor.BRIGHT_MAGENTA}", "\u001B[95m").replace("${AnsiColor.BRIGHT_CYAN}", "\u001B[96m")
				.replace("${AnsiColor.BRIGHT_WHITE}", "\u001B[97m").replace("${AnsiBackground.BLACK}", "\u001B[40m")
				.replace("${AnsiBackground.DEFAULT}", "\u001B[49m"));
	}

}