/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.kyj.Fx.dao.wizard
 *	작성일   : 2016. 4. 16.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.kyj.Fx.dao.wizard;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;

import kyj.Fx.dao.wizard.DaoWizard;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoColumnsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoFieldsDVO;
import kyj.Fx.dao.wizard.core.model.vo.TbpSysDaoMethodsDVO;

/***************************
 *
 * @author KYJ
 *
 ***************************/
public class DaoWizardTest {

	/********************************
	 * 작성일 : 2016. 4. 16. 작성자 : KYJ
	 *
	 *
	 * DAO Wizard Test Case
	 *
	 * @throws Exception
	 ********************************/
	@Test
	public void test() throws Exception {

		// *******클래스 정보
		ClassMeta classMeta = new ClassMeta("Hello");

		// ******메소드 정보
		TbpSysDaoMethodsDVO methodMeta = new TbpSysDaoMethodsDVO(classMeta);
		// 메소드명
		methodMeta.setMethodName("simple");
		// SQL문
		methodMeta.setSqlBody("select 1 as ss from dual where name = :name");

		// 데이터베이스 타입에 대한정보
		TbpSysDaoColumnsDVO tbpSysDaoColumnsDVO = new TbpSysDaoColumnsDVO();
		tbpSysDaoColumnsDVO.setColumnName("name");
		tbpSysDaoColumnsDVO.setColumnType("VARCHAR");
		methodMeta.setTbpSysDaoColumnsDVOList(Arrays.asList(tbpSysDaoColumnsDVO));

		// 테스트 필드에 대한정보.. (이부분은 크게 중요치는않음.)
		TbpSysDaoFieldsDVO tbpSysDaoFieldsDVO = new TbpSysDaoFieldsDVO();
		tbpSysDaoFieldsDVO.setFieldName("name");
		tbpSysDaoFieldsDVO.setTestValue("kyj");
		tbpSysDaoFieldsDVO.setType("java.lang.String");
		methodMeta.setTbpSysDaoFieldsDVOList(Arrays.asList(tbpSysDaoFieldsDVO));

		// 리턴타입에 대한정보
		methodMeta.setResultVoClass("com.sample.Hello");

		// DAO생성을 위한준비
		DaoWizard<ClassMeta, TbpSysDaoMethodsDVO, FieldMeta> wizard = new DaoWizard(classMeta, Arrays.asList(methodMeta));

		// 반드시호출.
		wizard.build();

		// 확인
		System.out.println(wizard.toText());

		Assert.assertNotNull(wizard.toText());
	}
}
