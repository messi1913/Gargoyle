import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sun.tools.javac.util.Assert;
import org.junit.Test;
/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2016. 8. 4.
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class JdbcProgrammingTest {

	/**
	 * @최초생성일 2016. 8. 4.
	 */
	private static final String PASSWORD = "SAMSUNG_SDS";
	/**
	 * @최초생성일 2016. 8. 4.
	 */
	private static final String USERNAME = "SOS_DBA";
	/**
	 * @최초생성일 2016. 8. 4.
	 */
	private static final String URL = "jdbc:postgresql://localhost/SOS";

	private static final String SQL = "select * from tbm_sys_dimmension_his ";

	/**
	 * 데이터를 조회하기 위한 기본 처리.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 4.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private ResultSet getResultSet() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		PreparedStatement statement = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		System.out.printf("print execute before fetch size : %d \n", statement.getFetchSize());

		//가져올수 있는 데이터의 최대 크기를 지정
		//		statement.setMaxRows(5);

		ResultSet resultSet = statement.executeQuery();

		//어떻게 startRow는 지정할 수 있을까 ?

		System.out.printf("print execute than fetch size : %d \n", resultSet.getFetchSize());

		return resultSet;
	}


	/**
	 * 최대로우 수를 벗어난 값을 조회함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 4.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Test
	public void testEmpty() throws ClassNotFoundException, SQLException {

		ResultSet resultSet = getResultSet();

		//커서를 이동시킨다.
		resultSet.absolute(5);
		if (resultSet.next()) {
			String cursorName = resultSet.getCursorName();
			System.out.printf("print current row : %d \n ", resultSet.getRow());
			System.out.printf("print first column data : %s \n", resultSet.getString(1));

		} else {
			System.out.println("empty.");

		}

	}

	/**
	 * 최대 로우의 마지막 로우를 구함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 4.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Test
	public void testAbsolute4() throws ClassNotFoundException, SQLException {

		ResultSet resultSet = getResultSet();

		//커서를 이동시킨다.
		resultSet.absolute(4);
		if (resultSet.next()) {
			String cursorName = resultSet.getCursorName();
			System.out.printf("print current row : %d \n ", resultSet.getRow());
			System.out.printf("print first column data : %s \n", resultSet.getString(1));
			System.out.println(cursorName);

		} else {
			System.out.println("empty.");

		}

	}

	/**
	 * 최대 로우의 마지막 로우를 구함.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 8. 4.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@Test
	public void testHasNextRow() throws ClassNotFoundException, SQLException {

		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		PreparedStatement statement = connection.prepareStatement(SQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

		//		statement.setFetchSize(1000);
		System.out.printf("print execute before fetch size : %d \n", statement.getFetchSize());

		//가져올수 있는 데이터의 최대 크기를 지정
		//		statement.setMaxRows(6);

		ResultSet resultSet = statement.executeQuery();

		//어떻게 startRow는 지정할 수 있을까 ?

		System.out.printf("print execute than fetch size : %d \n", resultSet.getFetchSize());

		//커서를 이동시킨다.
		resultSet.absolute(0);

		//		if (resultSet.next())
		//			System.out.println(statement.getMoreResults());
		while (resultSet.next()) {
			System.out.printf("print execute than fetch size : %d \n", resultSet.getFetchSize());
			System.out.printf("print current row : %d \n ", resultSet.getRow());
			System.out.printf("print first column data : %s \n", resultSet.getString(1));

		}

	}

	@Test
	public void testHasNextRow2() throws ClassNotFoundException, SQLException {

		Class.forName("org.postgresql.Driver");
		Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		PreparedStatement statement = connection.prepareStatement(SQL);

		//		statement.setFetchSize(1000);
		System.out.printf("print execute before fetch size : %d \n", statement.getFetchSize());

		//가져올수 있는 데이터의 최대 크기를 지정
		//		statement.setMaxRows(6);

		ResultSet resultSet = statement.executeQuery();

		//어떻게 startRow는 지정할 수 있을까 ?

		System.out.printf("print execute than fetch size : %d \n", resultSet.getFetchSize());

		//커서를 이동시킨다.
//		resultSet.absolute(0);

		//		if (resultSet.next())
		//			System.out.println(statement.getMoreResults());
		while (resultSet.next()) {
			System.out.printf("print execute than fetch size : %d \n", resultSet.getFetchSize());
			System.out.printf("print current row : %d \n ", resultSet.getRow());
			System.out.printf("print first column data : %s \n", resultSet.getString(1));

		}

	}

}
