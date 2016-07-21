/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2016. 7. 18.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Supplier;

import com.kyj.fx.voeditor.visual.exceptions.GagoyleRuntimeException;
import com.kyj.fx.voeditor.visual.exceptions.IGargoyleExceptionCode.ERROR_CODE;
import com.kyj.fx.voeditor.visual.momory.ConfigResourceLoader;
import com.kyj.fx.voeditor.visual.util.DbUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.ISCMUrl;

/***************************
 *
 * SVN WcDB파일을 연결하여 메타정보를 가져옴.
 *
 * @author KYJ
 *
 ***************************/
public class SVNWcDbClient implements ISCMUrl {

	private File wcdbFile;
	private String driver = ConfigResourceLoader.getInstance().get(ConfigResourceLoader.DBMS_SQLITE);
	private String dbUrl;
	private final String URL_FORMAT = "jdbc:sqlite:%s";

	private String repositoryId;
	private String svnUrl;

	private Supplier<Connection> connectionSupplier;

	/**
	 * SVN에 관한 메타정보를 파일로부터 얻어오기위한 처리.
	 *
	 * 특히 이 클래스는 파일로부터 SVN 서버와 관련된 메타처리.
	 *
	 * 더부어 SVN에 연결된 로컬 파일시스템에 대한 메타정보도 얻어 올 수 있음.
	 *
	 * @param wcdbFile
	 * @throws Exception
	 *             에러가 발생하는경우 SVN과 관련된 충분한 데이터가 없음.
	 */
	public SVNWcDbClient(File wcdbFile) throws Exception {
		if (wcdbFile == null)
			throw new GagoyleRuntimeException(ERROR_CODE.NULL);

		if (!wcdbFile.exists())
			throw new GagoyleRuntimeException(ERROR_CODE.FILE_NOT_FOUND);

		this.wcdbFile = wcdbFile;
		this.dbUrl = String.format(URL_FORMAT, wcdbFile.getAbsolutePath());
		this.connectionSupplier = new Supplier<Connection>() {

			@Override
			public Connection get() {
				try {
					Class.forName(driver);
					return DriverManager.getConnection(dbUrl);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		//데이터베이스로부터 SVN URL 정보를 리턴받음.
		try (Connection con = this.connectionSupplier.get()) {
			Map<String, Object> findOne = DbUtil.findOne(con, "select id, a.root || '/' || b.repos_path as root from repository a inner join nodes_base b on id = b.wc_id limit 1");


			Object svnUrl = findOne.get("root");
			if (ValueUtil.isNotEmpty(svnUrl)) {
				this.svnUrl = svnUrl.toString();
			}


			Object repositoryId = findOne.get("id");
			if (ValueUtil.isNotEmpty(svnUrl)) {
				this.repositoryId = repositoryId.toString();
			}

		}
	}

	@Override
	public String getUrl() {
		return svnUrl;
	}

}
