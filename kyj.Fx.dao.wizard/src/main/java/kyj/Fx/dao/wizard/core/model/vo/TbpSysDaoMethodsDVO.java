/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 19.
 *	프로젝트 : VisualFxVoEditor
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.MethodMeta;

/**
 * @author KYJ
 *
 */
public class TbpSysDaoMethodsDVO extends MethodMeta {
	/**
	 * 메소드명
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty methodName;

	/**
	 * 메소드명
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty methodDesc;

	/**
	 * 클래스명
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty resultVoClass;

	/**
	 * Velocity 변수
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private List<TbpSysDaoFieldsDVO> tbpSysDaoFieldsDVOList;

	/**
	 * SQL조회결과 생성되는 컬럼리스트
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private List<TbpSysDaoColumnsDVO> tbpSysDaoColumnsDVOList;

	/**
	 * SQL 본문
	 * 
	 * @최초생성일 2015. 10. 21.
	 */
	private StringProperty sqlBody;

	public TbpSysDaoMethodsDVO(ClassMeta parent) {
		super(parent);
		this.setModifier(Modifier.PUBLIC);
		this.methodName = new SimpleStringProperty("");
		this.methodDesc = new SimpleStringProperty("");
		this.resultVoClass = new SimpleStringProperty("");
		this.sqlBody = new SimpleStringProperty("");
		this.tbpSysDaoFieldsDVOList = new ArrayList<>();
		this.tbpSysDaoColumnsDVOList = new ArrayList<>();
	}


	/**
	 * @return the resultVoClass
	 */
	public String getResultVoClass() {
		return resultVoClass.get();
	}

	/**
	 * @param resultVoClass
	 *            the resultVoClass to set
	 */
	public void setResultVoClass(String resultVoClass) {
		this.resultVoClass.set(resultVoClass);
	}

	/**
	 * @return the tbpSysDaoFieldsDVOList
	 */
	public List<TbpSysDaoFieldsDVO> getTbpSysDaoFieldsDVOList() {
		return tbpSysDaoFieldsDVOList;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 22.
	 * @return
	 */
	public StringProperty resultVoClassProperty() {
		return resultVoClass;
	}

	/**
	 * @param tbpSysDaoFieldsDVOList
	 *            the tbpSysDaoFieldsDVOList to set
	 */
	public void setTbpSysDaoFieldsDVOList(List<TbpSysDaoFieldsDVO> tbpSysDaoFieldsDVOList) {
		this.tbpSysDaoFieldsDVOList = tbpSysDaoFieldsDVOList;
	}

	/**
	 * @return the tbpSysDaoColumnsDVOList
	 */
	public List<TbpSysDaoColumnsDVO> getTbpSysDaoColumnsDVOList() {
		return tbpSysDaoColumnsDVOList;
	}

	/**
	 * @param tbpSysDaoColumnsDVOList
	 *            the tbpSysDaoColumnsDVOList to set
	 */
	public void setTbpSysDaoColumnsDVOList(List<TbpSysDaoColumnsDVO> tbpSysDaoColumnsDVOList) {
		this.tbpSysDaoColumnsDVOList = tbpSysDaoColumnsDVOList;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param desc
	 */
	public void setMethodDesc(String methodDesc) {
		this.methodDesc.set(methodDesc);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public String getMethodDesc() {
		return methodDesc.get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public StringProperty methodDescProperty() {
		return methodDesc;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName.set(methodName);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public String getMethodName() {
		return methodName.get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public StringProperty methodNameProperty() {
		return methodName;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @param sqlBody
	 */
	public void setSqlBody(String sqlBody) {
		this.sqlBody.set(sqlBody);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public String getSqlBody() {
		return sqlBody.get();
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2015. 10. 21.
	 * @return
	 */
	public StringProperty sqlBodyProperty() {
		return sqlBody;
	}
}
