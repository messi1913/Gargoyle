/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 10. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.function.BiFunction;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.functions.DefaultGenerateDaoWizardFunction;

/**
 * @author KYJ
 *
 */
public class DaoWizardConverter {
	// TODO 수정할것 2015.11.02 TbmSysDaoDVO를 ClassMeta 유형으로 통합.
	private ClassMeta classMeta;
	private TbmSysDaoDVO daoDVO;

	public DaoWizardConverter(TbmSysDaoDVO tbmSysDaoDVO) {
		this.classMeta = tbmSysDaoDVO;
		this.daoDVO = tbmSysDaoDVO;
	}

	/**
	 * 생성자
	 * 
	 * @param classMeta2
	 * @param tbmSysDaoDVO
	 */
	public DaoWizardConverter(ClassMeta classMeta, TbmSysDaoDVO tbmSysDaoDVO) {
		this.classMeta = classMeta;
		this.daoDVO = tbmSysDaoDVO;

	}

	/**
	 * Vo를 생성하기 위한 기본 모델을 반환
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 * @return
	 */
	private BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> defaultGenertateVoEditorBiConsumer() {
		return new DefaultGenerateDaoWizardFunction<ClassMeta, TbmSysDaoDVO, TbpSysDaoMethodsDVO, FieldMeta>();
	}

	/**
	 * 변환처리
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 16.
	 * @return
	 */
	public DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta> convert() {
		return defaultGenertateVoEditorBiConsumer().apply(classMeta, daoDVO);
	}

}
