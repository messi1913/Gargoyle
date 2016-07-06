/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : com.kyj.fx.voeditor.visual.words.spec.auto.msword.util
 *	작성일   : 2016. 2. 15.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.kyj.fx.voeditor.visual.exceptions.ProgramSpecFileNotFoundException;
import com.kyj.fx.voeditor.visual.exceptions.ProgramSpecSourceException;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.AbstractJavaProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.AbstractXframeProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.AppFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.BizFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.DEMFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.DQMFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.DVOFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.IProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.SVOFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.VOFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.filemodel.XframeJsFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ImportsDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ProgramSpecSVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.SourceAnalysisDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.UserSourceMetaDVO;

/**
 * @author KYJ
 *
 */
class ProgramSpecFileUtil
{

	public static final String App = "APP.JAVA";
	public static final String VO = "VO.JAVA";
	public static final String DEM = "DEM.JAVA";
	public static final String DQM = "DQM.JAVA";
	public static final String BIZ = "BIZ.JAVA";
	public static final String SVO = "SVO.JAVA";
	public static final String DVO = "DVO.JAVA";
	public static final String JS = ".JS";
	public static final String XML = ".XML";

	/**
	 * 프로그램 사양서 정의대로 비즈니스에 맞게 변환
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static IProgramSpecFile newInstance(File file) throws Exception
	{
		if (file != null && file.exists())
		{
			String name = file.getName().toUpperCase();
			if (name.endsWith(App))
			{
				return new AppFile(file);
			} else if (name.endsWith(BIZ))
			{
				return new BizFile(file);
			} else if (name.endsWith(DQM))
			{
				return new DQMFile(file);
			} else if (name.endsWith(DEM))
			{
				return new DEMFile(file);
			} else if (name.endsWith(DVO))
			{
				return new DVOFile(file);
			} else if (name.endsWith(SVO))
			{
				return new SVOFile(file);
			} else if (name.endsWith(VO))
			{
				return new VOFile(file);
			} else if (name.endsWith(JS))
			{
				return new XframeJsFile(file);
			} /*
				 * else if (name.endsWith(BaseUtil.XML)) {
				 *
				 * }
				 */
			else
			{
				// 그외의 경우는 비즈파일로 인식하게함.
				return new BizFile(file);
			}
		} else
		{
			throw new ProgramSpecFileNotFoundException("파일을 찾을 수 없습니다.");
		}
	}

}
