/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2017. 9. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.List;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.util.DbUtil;

/**
 * 
 * @author KYJ
 *
 */
public interface MacroTableGeneratorable {

	/**
	 * 테이블 생성 스크립트 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 18.
	 * @return
	 */
	public List<String> createTableScript();

	/**
	 * 테이블 생성 스크립트 실행 여부
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 18.
	 * @return
	 */
	public boolean isGenerateTable();

	/**
	 * db접속 정보를 리턴하는 인터페이스 클래스 리턴
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 18.
	 * @return
	 */
	public Supplier<Connection> connectionSupplier();

	/**
	 * 생성 쿼리문 실행
	 * 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 9. 18.
	 * @return
	 * @throws Exception
	 */
	public default boolean execute() throws Exception {

		if (isGenerateTable()) {
			Supplier<Connection> supplier = connectionSupplier();
			if (supplier == null)
				return false;

			try (Connection con = supplier.get()) {
				List<String> createTableScript = createTableScript();
				int transactionedScope = DbUtil.getTransactionedScope(con, createTableScript, sqls -> sqls);
				return transactionedScope >= 1;
			}

		}
		return false;

	}
}
