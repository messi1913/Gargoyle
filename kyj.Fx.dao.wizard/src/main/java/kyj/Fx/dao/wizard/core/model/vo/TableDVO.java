/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.main.model.vo
 *	작성일   : 2015. 10. 16.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.dao.wizard.core.model.vo;

import java.util.List;

/**
 * @author KYJ
 *
 */
public class TableDVO {
	private TableMasterDVO tableMasterDVO;
	private List<TableModelDVO> tableModelDVOList;

	public TableDVO() {
		super();
	}
	public TableDVO(TableMasterDVO tableMasterDVO, List<TableModelDVO> tableModelDVOList) {
		super();
		this.tableMasterDVO = tableMasterDVO;
		this.tableModelDVOList = tableModelDVOList;
	}
	/**
	 * @return the tableMasterDVO
	 */
	public TableMasterDVO getTableMasterDVO() {
		return tableMasterDVO;
	}
	/**
	 * @param tableMasterDVO
	 *            the tableMasterDVO to set
	 */
	public void setTableMasterDVO(TableMasterDVO tableMasterDVO) {
		this.tableMasterDVO = tableMasterDVO;
	}
	/**
	 * @return the tableModelDVOList
	 */
	public List<TableModelDVO> getTableModelDVOList() {
		return tableModelDVOList;
	}
	/**
	 * @param tableModelDVOList
	 *            the tableModelDVOList to set
	 */
	public void setTableModelDVOList(List<TableModelDVO> tableModelDVOList) {
		this.tableModelDVOList = tableModelDVOList;
	}

}
