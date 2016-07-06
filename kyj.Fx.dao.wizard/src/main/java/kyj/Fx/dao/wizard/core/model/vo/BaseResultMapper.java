/********************************
 *	프로젝트 : kyj.Fx.dao.wizard
 *	패키지   : kyj.Fx.dao.wizard.core.model.vo
 *	작성일   : 2015. 10. 29.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

/**
 * DAO를 ResultSet부분을 생성하기 위한 베이스 모델
 * 
 * @author KYJ
 *
 */
public class BaseResultMapper<M extends TbpSysDaoMethodsDVO> {

	/**
	 * StringBuffer 변수명
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String stringBufferVarName;

	/**
	 * 파라미터로 입력된 Map의 변수명
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String parameterMapVarName;

	/**
	 * RowMapper 변수명
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String resultSetVarName;

	/**
	 * RowNumber 변수명
	 * 
	 * @최초생성일 2015. 10. 29.
	 */
	private String rowNumVarName;

	private FxDao fxDao;
	private M t;

	public BaseResultMapper(FxDao vo, M t) {
		this.fxDao = vo;
		this.t = t;
		this.resultSetVarName = "rs";
		this.rowNumVarName = "rowNum";
	}

	/**
	 * @return the stringBufferVarName
	 */
	public String getStringBufferVarName() {
		return stringBufferVarName;
	}

	/**
	 * @param stringBufferVarName
	 *            the stringBufferVarName to set
	 */
	public void setStringBufferVarName(String stringBufferVarName) {
		this.stringBufferVarName = stringBufferVarName;
	}

	/**
	 * @return the parameterMapVarName
	 */
	public String getParameterMapVarName() {
		return parameterMapVarName;
	}

	/**
	 * @param parameterMapVarName
	 *            the parameterMapVarName to set
	 */
	public void setParameterMapVarName(String parameterMapVarName) {
		this.parameterMapVarName = parameterMapVarName;
	}

	/**
	 * @return the rowNumVarName
	 */
	public String getRowNumVarName() {
		return rowNumVarName;
	}

	/**
	 * @param rowNumVarName
	 *            the rowNumVarName to set
	 */
	public void setRowNumVarName(String rowNumVarName) {
		this.rowNumVarName = rowNumVarName;
	}

	/**
	 * @return the vo
	 */
	public FxDao getFxDao() {
		return fxDao;
	}

	/**
	 * @param fxDao
	 *            the vo to set
	 */
	public void setFxDao(FxDao fxDao) {
		this.fxDao = fxDao;
	}

	/**
	 * @return the t
	 */
	public M getT() {
		return t;
	}

	/**
	 * @param t
	 *            the t to set
	 */
	public void setT(M t) {
		this.t = t;
	}

	/**
	 * @return the resultSetVarName
	 */
	public String getResultSetVarName() {
		return resultSetVarName;
	}

	/**
	 * @param resultSetVarName
	 *            the resultSetVarName to set
	 */
	public void setResultSetVarName(String resultSetVarName) {
		this.resultSetVarName = resultSetVarName;
	}

}
