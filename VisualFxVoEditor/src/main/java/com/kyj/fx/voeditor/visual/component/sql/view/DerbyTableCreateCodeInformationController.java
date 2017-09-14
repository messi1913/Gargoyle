/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.view
 *	작성일   : 2017. 6. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.view;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * Derby-DB 용 테이블 CREATE문 조회 컨트롤러
 *
 * @author KYJ
 *
 */
public class DerbyTableCreateCodeInformationController extends CommonTableCreateCodeInformationController {

	
	private static Logger LOGGER = LoggerFactory.getLogger(DerbyTableCreateCodeInformationController.class);

	public DerbyTableCreateCodeInformationController() throws Exception {
		super();
	}

	@Override
	public RowMapper<String> mapper() {
		return (rs, rowNum) -> {
			return rs.getString(1);
		};
	}

	@Override
	public String fromQuery(List<String> result) {
		Optional<String> reduce = result.stream().reduce((a, b) -> {
			return a.concat("\n").concat(b);
		});
		String d = "";
		if (reduce.isPresent())
			d = reduce.get();
		else
			d = super.fromQuery(result);

		return d.concat(";\n").concat(createIndexScript()).concat(";");
	}
	
	private static final int PK_INDEX = 4;
	
	/**
	 * Primary key 생성 스크립트 
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 28. 
	 * @return
	 */
	private String createIndexScript() {

		String databaseName = getDatabaseName();
		String tableName = getTableName();

		try (Connection con = getConnection()) {
			ResultSet primaryKeys = con.getMetaData().getPrimaryKeys("", databaseName, tableName);

			StringBuffer keys = new StringBuffer();
			while (primaryKeys.next()) {
				String pk = primaryKeys.getString(PK_INDEX);
				keys.append(pk).append(",");
			}

			if (keys.length() != 0) {
				keys.setLength(keys.length() - 1);

				String t = "";
				if (ValueUtil.isEmpty(databaseName))
					t = tableName;
				else
					t = String.format("%s.%s", databaseName, tableName);

				StringBuffer sb = new StringBuffer("alter table ").append(t).append(" add primary key (");
				sb.append(keys);
				sb.append(")");
				return sb.toString();
			}

		} catch (SQLException e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return "";

	}

}
