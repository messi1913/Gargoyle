/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.core.commons
 *	작성일   : 2017. 2.16.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.core.commons;

/**
 * Dimmension 커넥트와 관련된 관련된 상수 정의
 *
 * @author KYJ
 *
 */
public interface DimKeywords {

	public static final String DIM_USER_ID = "dim.userId";
	public static final String DIM_USER_PASS = "dim.userPass";
	public static final String DIM_URL = "dim.url";
	public static final String DIM_PATH = "dim.path";
	public static final String DIM_PATH_WINDOW = "dim.path.window";
	public static final String DIM_REPOSITORIES = "dim.repositores";



	public static final String DIM_DB_CONN = "dim.db.conn";
	public static final String DIM_DB_NAME = "dim.db.name";

	/**
	 * 디멘전에서 관리되는 루트 프로젝트를 의미함.
	 * @최초생성일 2017. 3. 14.
	 */
	public static final String PROJECT_SPEC = "proj.spec";

	/**
	 * 기본설정값으로 사용할 디폴트 PROJECT_SPEC에 대한 값을 정의하는 키
	 * @최초생성일 2017. 3. 14.
	 */
	public static final String DEFAULT_PROJECT_SPEC = "def.proj.spec";




}
