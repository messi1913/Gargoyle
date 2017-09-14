/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.sql.table
 *	작성일   : 2016. 11. 28.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.sql.table;

import com.sun.btrace.BTraceUtils.Strings;

/**
 * 테이블 참조 키에 대한 데이터 관리 모델.
 * @author KYJ
 *
 */
public class ReferenceKey implements Comparable<ReferenceKey> {

	private String pkColumnName;
	private String pkTableName;
	private String fkColumnName;
	private String fkTableName;

	/**
	 * @return the pkColumnName
	 */
	public final String getPkColumnName() {
		return pkColumnName;
	}

	/**
	 * @return the pkTableName
	 */
	public final String getPkTableName() {
		return pkTableName;
	}

	/**
	 * @return the fkColumnName
	 */
	public final String getFkColumnName() {
		return fkColumnName;
	}

	/**
	 * @return the fkTableName
	 */
	public final String getFkTableName() {
		return fkTableName;
	}

	/**
	 * @param pkColumnName the pkColumnName to set
	 */
	public final void setPkColumnName(String pkColumnName) {
		this.pkColumnName = pkColumnName;
	}

	/**
	 * @param pkTableName the pkTableName to set
	 */
	public final void setPkTableName(String pkTableName) {
		this.pkTableName = pkTableName;
	}

	/**
	 * @param fkColumnName the fkColumnName to set
	 */
	public final void setFkColumnName(String fkColumnName) {
		this.fkColumnName = fkColumnName;
	}

	/**
	 * @param fkTableName the fkTableName to set
	 */
	public final void setFkTableName(String fkTableName) {
		this.fkTableName = fkTableName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fkColumnName == null) ? 0 : fkColumnName.hashCode());
		result = prime * result + ((fkTableName == null) ? 0 : fkTableName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceKey other = (ReferenceKey) obj;
		if (fkColumnName == null) {
			if (other.fkColumnName != null)
				return false;
		} else if (!fkColumnName.equals(other.fkColumnName))
			return false;
		if (fkTableName == null) {
			if (other.fkTableName != null)
				return false;
		} else if (!fkTableName.equals(other.fkTableName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ReferenceKey o) {

		if (o == null)
			return -1;

		String fkTableName = o.getFkTableName();
		String fkColumnName = o.getFkColumnName();
		if (Strings.compareTo(this.fkTableName, fkTableName) == 0) {
			return Strings.compareTo(this.fkColumnName, fkColumnName);
		}
		return Strings.compareTo(this.fkTableName, fkTableName);
	}

}
