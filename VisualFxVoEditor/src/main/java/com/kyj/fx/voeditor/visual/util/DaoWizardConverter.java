/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.layout
 *	작성일   : 2015. 10. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.util;

import java.util.function.BiFunction;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;
import com.kyj.fx.voeditor.visual.framework.daowizard.GargoyleDaoWizardFactory;
import com.kyj.fx.voeditor.visual.framework.daowizard.GargoyleDaoWizardFactory.Wizardtype;
import com.kyj.fx.voeditor.visual.functions.DefaultGenerateDaoWizardFunction;
import com.kyj.fx.voeditor.visual.functions.TypeChangedGenerateDaoWizardFunction;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TbmSysDaoDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/**
 * @author KYJ
 *
 */
public class DaoWizardConverter {
	// TODO 수정할것 2015.11.02 TbmSysDaoDVO를 ClassMeta 유형으로 통합.
	private ClassMeta classMeta;
	private TbmSysDaoDVO daoDVO;

	private BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> converter;

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
	 *  Vo를 생성하기 위한 기본 모델을 반환
	 *
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 15.
	 * @return
	 *
	 *2016-08-26
	 * @deprecated TypeChangedGenerateDaoWizardFunction 함수 사용.
	 */
	@Deprecated
	private BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> defaultGenertateVoEditorBiConsumer() {
		return new DefaultGenerateDaoWizardFunction<ClassMeta, TbmSysDaoDVO, TbpSysDaoMethodsDVO, FieldMeta>();
	}

	/**
	 * 타입을 변환할 수 있는 기능을 제공하기위한 컨버터
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 26.
	 * @return
	 * @deprecated use createWizard(Wizardtype type)  function and call convert()
	 */
	@Deprecated
	private BiFunction<ClassMeta, TbmSysDaoDVO, DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta>> typeChangedGenertateVoEditorBiConsumer() {
		return new TypeChangedGenerateDaoWizardFunction<ClassMeta, TbmSysDaoDVO, TbpSysDaoMethodsDVO, FieldMeta>();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 10. 18.
	 * @param type
	 * @return
	 */
	public DaoWizardConverter createWizard(Wizardtype type) {
		this.converter = GargoyleDaoWizardFactory.get(type, classMeta, this.daoDVO);
		return this;
	}

	/**
	 * 변환처리
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 16.
	 * @return
	 */
	public DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta> convert() {
		return this.converter.apply(classMeta, daoDVO);
	}

}
