/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.utube
 *	작성일   : 2017. 5. 31.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.utube;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.FileUtil;

/**
 * 
 *  유튜브 다운로더를 통해생성된 파일 양식에 맞는 
 *  포멧의 html문을 생성해줌.
 *  
 * @author KYJ
 *
 */
class WebViewWriter implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebViewWriter.class);

	private File webm, mp4;
	private String result;

	public WebViewWriter(File webm, File mp4) {
		this.webm = webm;
		this.mp4 = mp4;
	}

	@Override
	public void run() {

		StringBuffer head = new StringBuffer();
		StringBuffer foot = new StringBuffer();
		StringBuffer vidioHtml = new StringBuffer();
		StringBuffer script = new StringBuffer();

		head.append("<!DOCTYPE html>\n");
		head.append("<html>\n");
		head.append("<body>\n");

		if ((webm != null && webm.exists()) && (mp4 != null && mp4.exists())) {

			String vidio = webm.getAbsolutePath();
			String audio = mp4.getAbsolutePath();

			vidioHtml.append("<video id=\"videoInHTML\" width=\"320\" height=\"240\" controls>\n");
			vidioHtml.append("  <source src=\"" + vidio + "\" type=\"video/webm\">\n");
			vidioHtml.append("  Your browser does not support the video tag.\n");
			vidioHtml.append("</video>\n");
			vidioHtml.append("<video id=\"audioInHTML\" >\n");
			vidioHtml.append("	<source src=\"" + audio + "\" type=\"video/mp4\">\n");
			vidioHtml.append("</video>\n");

			script.append("<script>\n");
			script.append(" //Javascript\n");
			script.append("var yourVideoElement = document.getElementById(\"videoInHTML\");\n");
			script.append("yourVideoElement.addEventListener('play', videoPausePlayHandler, false);\n");
			script.append("yourVideoElement.addEventListener('pause', videoPausePlayHandler, false);\n");
			script.append("\n");
			script.append("function videoPausePlayHandler(e) {\n");
			script.append("  if (e.type == 'play') {\n");
			script.append("    // play your audio\n");
			script.append("	document.getElementById(\"audioInHTML\").play();\n");
			script.append("  } else if (e.type == 'pause') {\n");
			script.append("    // pause your audio\n");
			script.append("	document.getElementById(\"audioInHTML\").pause();\n");
			script.append("  }\n");
			script.append("}\n");
			script.append("</script>\n");
		}

		else // if (!webm.exists() || !mp4.exists()) {
		{

			if (webm == null && mp4 == null) {

			} else {

				String media = "";
				if (webm == null)
					media = mp4.getAbsolutePath();
				else
					media = webm.getAbsolutePath();

				vidioHtml.append("<video id=\"videoInHTML\" width=\"320\" height=\"240\" controls>\n");
				vidioHtml.append("  <source src=\"" + media + "\" type=\"video/mp4\">\n");
				vidioHtml.append("  Your browser does not support the video tag.\n");
				vidioHtml.append("</video>\n");
			}

		}

		foot.append("</body>\n");
		foot.append("\n");

		foot.append("</html>\n");

		result = new StringBuffer().append(head.toString()).append(vidioHtml.toString()).append(script.toString()).append(foot.toString())
				.toString();

	}

	public String getString() {
		return this.result;
	}

	public File outputFile(String filePathName) throws IOException {
		File file = new File(filePathName);
		FileUtil.writeFile(file, getString(), Charset.forName("UTF-8"));
		return file;
	}

}
