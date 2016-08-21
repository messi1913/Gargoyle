/**
 *
 */
package com.kyj.fx.voeditor.visual.words.spec.auto.msword.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.words.spec.auto.msword.biz.InspectorBiz;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.biz.InspectorSourceMeta;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.model.AbstractJavaProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.model.AbstractXframeProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.model.IProgramSpecFile;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.template.ProgramSpecWordTemplate;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.MethodDVO.MethodMetaDVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.ProgramSpecSVO;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.vo.SourceAnalysisDVO;

/**
 * @author KYJ
 *
 */
public class ProgramSpecFileUtilTest
{

	/**
	 * 프로그램 사양서 정의대로 비즈니스에 맞게 변환
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@Test
	public void simpleWrite() throws Exception
	{

		String filePath = "ProgramSpecFileUtilTest.java";
		File file = new File(filePath);
		IProgramSpecFile newInstance = ProgramSpecFileUtil.newInstance(file);

		ProgramSpecSVO svo = new ProgramSpecSVO();

		if (newInstance instanceof AbstractJavaProgramSpecFile)
		{
			svo = ProgramSpecUtil.doJavaFile("sampleJavaProject", file.getName(), (AbstractJavaProgramSpecFile) newInstance);
		} else if (newInstance instanceof AbstractXframeProgramSpecFile)
		{
			svo = ProgramSpecUtil.doJsFile("sampleXframeProject", file.getName(), (AbstractXframeProgramSpecFile) newInstance);
		}
//		InspectorSourceMeta meta = newInstance.getInspectorSourceMeta();

//		List<SourceAnalysisDVO> methodList = InspectorBiz.getInstance().methodList(meta);
//		methodList.forEach(method -> {
//			String methodName = method.getMethodName();
//			System.out.println(methodName);
//			System.out.println(method.getStartLine());
//			System.out.println(method.getEndLine());
//		});

//		System.out.println(meta.listBlock());
		// System.out.println(meta.getSourceCodeList());
//		svo.getMethodDVOList().forEach(method -> {
//
//			MethodMetaDVO methodMetaDVO = method.getMethodMetaDVO();
//			System.out.println(method.getMethodName());
//
//			System.out.println(method.getDescription());
//
//			System.out.println(method.getMainFunction());
//		});

		IProgramSpecFile abPFile = svo.getFile();
		// 확장자까지 포함된 파일명
		String simpleFileName = svo.getUserSourceMetaDVO().getSimpleFileName();
		// 확장자를 제거하고 워드문서가 생성될 파일명 작성
		String fileName = "(" + abPFile.getSourceFileType().toString() + ")(사양서)" + simpleFileName.substring(0, simpleFileName.indexOf('.'))
				+ ".docx";
		// 풀 경로 설정.( 유저의 데스크탑에 저장됨.)
		String docFile = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + fileName;

		try
		{
			ProgramSpecWordTemplate word = new ProgramSpecWordTemplate(docFile, svo);
			word.write();
			word.close();
			System.out.println("사양서 생성 완료.[ " + file + " ]");

		} catch (IOException e1)
		{
			throw e1;
		}

	}
}
