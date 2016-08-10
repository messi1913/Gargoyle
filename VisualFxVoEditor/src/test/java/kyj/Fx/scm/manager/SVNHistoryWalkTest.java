/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 7. 14.
 *	작성자   : KYJ
 *******************************/
package kyj.Fx.scm.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;
import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 * @author KYJ
 *
 */
public class SVNHistoryWalkTest {

	JavaSVNManager localServerManager;
	JavaSVNManager localServerManager2;

	@Before
	public void setting() throws Exception {
		new ProxyInitializable().initialize();

		{
			Properties properties = new Properties();
			properties.put(JavaSVNManager.SVN_URL, "svn://localhost/svn/sos/trunk/");
			properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
			properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

			localServerManager = new JavaSVNManager(properties);
		}

		{
			Properties properties = new Properties();
			properties.put(JavaSVNManager.SVN_URL, "svn://localhost/svn/");
			properties.put(JavaSVNManager.SVN_USER_ID, "kyjun.kim");
			properties.put(JavaSVNManager.SVN_USER_PASS, "kyjun.kim");

			localServerManager2 = new JavaSVNManager(properties);
		}

	}

	/**
	 * 날짜별로 리비젼 번호를 리턴함.
	 *
	 * 일치하는 날짜가 없어도 리비젼이 리턴되는지를 확인함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @throws SVNException
	 */
	@Test
	public void walkRevisionByDate() throws SVNException {

		List<GagoyleDate> periodDays = DateUtil.getPeriodDays(2016, 7);
		for (GagoyleDate d : periodDays) {
			System.out.printf("%s %d \n", d.toDateString(), localServerManager.getRevision(d.toDate()));
		}

	}

	/**
	 * 특정파일에 대한 모든 로그를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @throws SVNException
	 */
	@Test
	public void historyWalkTest() throws SVNException {
		System.out.println(localServerManager.getAllLogs("test/test_1468459457997.txt"));
	}

	/**
	 * 특정파일에 대한 모든 로그를 리턴.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @throws SVNException
	 */
	@Test
	public void historyWalkTest2() throws SVNException {
		System.out.println(localServerManager.getAllLogs(""));
	}

	@Test
	public void historyWalkTest3() throws SVNException {
		Calendar instance = Calendar.getInstance();
		instance.set(2016, 6, 8);
		System.out.println(localServerManager2.getAllLogs(instance.getTime()));
	}

	/**
	 * 현재 일자에 최신 커밋내역을 조회.
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 14.
	 * @throws SVNException
	 */
	@Test
	public void historyWalkAnalysisTest() throws SVNException {

		List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek();

		GagoyleDate start = periodDaysByWeek.get(0);
		GagoyleDate end = periodDaysByWeek.get(periodDaysByWeek.size() - 1);

		long startRevision = localServerManager2.getRevision(start.toDate());
		long endRevision = localServerManager2.getRevision(end.toDate());

		System.out.println("start " + start.toDateString() + " end : " + end.toDateString());
		System.out.println("startRevision " + startRevision + " endRevision : " + endRevision);
		Collection<SVNLogEntry> allLogs = localServerManager2.getAllLogs(startRevision, endRevision);
		allLogs.stream().map(localServerManager2.fromPrettySVNLogConverter()).forEach(System.out::println);

	}

	@Test
	public void historyWalkAnalysisGroupingTest() throws SVNException {

		List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek();

		GagoyleDate start = periodDaysByWeek.get(0);
		GagoyleDate end = periodDaysByWeek.get(periodDaysByWeek.size() - 1);

		Calendar instance = Calendar.getInstance();
		instance.set(2016, 6, 1);
		Date time = instance.getTime();

		long startRevision = localServerManager2.getRevision(/*start.toDate()*/time);
		long endRevision = localServerManager2.getRevision(end.toDate());

		System.out.println("start " + start.toDateString() + " end : " + end.toDateString());
		System.out.println("startRevision " + startRevision + " endRevision : " + endRevision);
		Collection<SVNLogEntry> allLogs = localServerManager2.getAllLogs(startRevision, endRevision);
		//		allLogs.

		SimpleDateFormat format = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD);

		TreeMap<String, Long> collect = allLogs.stream().collect(
				Collectors.groupingBy(v -> format.format(v.getDate()), TreeMap::new, Collectors.mapping(v -> 1, Collectors.counting())));

		//		Map<String, Long> collect = allLogs.stream()
		//				.collect(Collectors.groupingBy(v -> format.format(v.getDate()), Collectors.mapping(v -> 1, Collectors.counting())));

		System.out.println(collect);

	}

}
