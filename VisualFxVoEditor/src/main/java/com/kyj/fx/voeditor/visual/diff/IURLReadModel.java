/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.util
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.diff;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author KYJ
 *
 */
public interface IURLReadModel extends IReadModel<URL, String> {

	/**
	 * 파일 읽기 모델 정의
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	List<String> readLines(URL url) throws IOException;
}
