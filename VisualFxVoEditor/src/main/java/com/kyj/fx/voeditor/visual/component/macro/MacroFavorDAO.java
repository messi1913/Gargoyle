/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.macro
 *	작성일   : 2016. 9. 4.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.macro;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/***************************
 * 
 * @author KYJ
 *
 ***************************/
public class MacroFavorDAO extends AbstractMacroFavorDAO {

	public MacroFavorDAO(Supplier<Connection> connectionSupplier) {
		super(connectionSupplier);
	}

	/**
	 * 루트 데이터 리턴
	 * 
	 * @inheritDoc
	 */
	public List<MacroItemVO> getRoots() throws Exception {
		List<MacroItemVO> select = Collections.emptyList();
		try (Connection connection = getConnection()) {
			String sql = "select * from  meerkat.tbm_sys_sql_magt where parent_id is null";
			select = DbUtil.select(connection, sql, 10, new BeanPropertyRowMapper<>(MacroItemVO.class));
		}
		System.out.println(select);
		return select;
	}

	/**
	 * 자식 데이터 리턴
	 * 
	 * @inheritDoc
	 */
	public List<MacroItemVO> getChildrens(MacroItemVO vo) throws Exception {
		
		try (Connection connection = getConnection()) {
			Map<String, Object> map = ValueUtil.toMap(vo);

			//parentId가 파라미터로 넘어온 id인, 자식 노드 데이터들을 리턴한다.
			String sql = "select * from  meerkat.tbm_sys_sql_magt where parent_id = :id";
			String velocityToText = ValueUtil.getVelocityToText(sql, map, true);
			return DbUtil.select(connection, velocityToText, 10, new BeanPropertyRowMapper<>(MacroItemVO.class));
		}

	}

}
