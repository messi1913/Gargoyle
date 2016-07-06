import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.kyj.fx.voeditor.visual.util.DbUtil;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 11. 6.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class VelocityTest {

	@Test
	public void test() throws SQLException, Exception {
		String sql = "SELECT table_name FROM information_schema.tables WHERE 1=1    AND  table_name in (:tableName)   limit 50";


		MapSqlParameterSource source = new MapSqlParameterSource();
		List<String> asList = Arrays.asList("tables", "columns");
		source.addValue("tableName",  asList /*"kyjun.kim"*/);
		List<HashMap<String, Object>> select = DbUtil.select(sql, source, (rs, r) -> {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("table_name", rs.getString("table_name"));
			return hashMap;
		});

		System.out.println(select);
	}
}
