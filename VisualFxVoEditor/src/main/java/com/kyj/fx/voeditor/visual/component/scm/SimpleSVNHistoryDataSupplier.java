/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.component.scm
 *	작성일   : 2016. 7. 19.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.component.scm;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.kyj.fx.voeditor.visual.framework.model.GagoyleDate;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.kyj.fx.voeditor.visual.util.FxCollectors;
import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.svn.java.JavaSVNManager;

/**
 *
 * SVN과 연계된 API 제공.
 *
 * @author KYJ
 *
 */
class SimpleSVNHistoryDataSupplier extends AbstracrtSVNHistoryDataSupplier {

	private static Logger LOGGER = LoggerFactory.getLogger(SimpleSVNHistoryDataSupplier.class);
	public static final SimpleDateFormat YYYY_MM_DD_HH_MM_SS_PATTERN = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS,
			Locale.ENGLISH);
	public static final SimpleDateFormat YYYYMMDD_EEE_PATTERN = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD_EEE);
	public static final SimpleDateFormat EEE_PATTERN = new SimpleDateFormat(DateUtil.SYSTEM_DATEFORMAT_EEE);

	private Collection<SVNLogEntry> allLogs;
	private Map<String, List<GargoyleSVNLogEntryPath>> collectedTable;
	private GagoyleDate start;

	private GagoyleDate end;

	public SimpleSVNHistoryDataSupplier(JavaSVNManager manager, int weekSize, int rankSize) throws Exception {
		super(manager, weekSize, rankSize);
	}

	public SimpleSVNHistoryDataSupplier(JavaSVNManager manager) throws Exception {
		this(manager, 5, 25);
	}

	@Override
	protected void reload(int weekSize) throws SVNException {

		List<GagoyleDate> periodDaysByWeek = DateUtil.getPeriodDaysByWeek(weekSize);
		start = periodDaysByWeek.get(0);
		end = periodDaysByWeek.get(periodDaysByWeek.size() - 1);

		LOGGER.debug("start : {} end : {}", start, end);
		long startRevision = getManager().getRevision(start.toDate());
		long endRevision = getManager().getRevision(end.toDate());

		LOGGER.debug("start Revision: {} end Resivion: {}", startRevision, endRevision);
		allLogs = getManager().getAllLogs(startRevision, endRevision);
		collectedTable = createStream(allLogs).collect(Collectors.groupingBy(v -> v.getPath()));
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param parse
	 * @return
	 * @throws SVNException
	 */
	public Collection<SVNLogEntry> list(Date date) throws SVNException {
		String dateString = YYYYMMDD_EEE_PATTERN.format(date);
		return allLogs.stream().filter(v -> dateString.equals(YYYYMMDD_EEE_PATTERN.format(v.getDate()))).collect(Collectors.toList());
	}

	/**
	 * 데이터 조회.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param path
	 * @param revision
	 * @return
	 */
	public String cat(String path, String revision) {
		return getManager().cat(path, revision);
	}

	public String cat(String path, long revision) {
		return getManager().cat(path, String.valueOf(revision));
	}

	/********************************
	 * 작성일 : 2016. 7. 19. 작성자 : KYJ
	 *
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 ********************************/
	public boolean isExists(String path)  {
		try {
			return getManager().isExistsPath(path);
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
		return false;
	}

	/********************************
	 * 작성일 :  2016. 7. 19. 작성자 : KYJ
	 *
	 * 이력에 대한 정보 get
	 * @param path
	 * @param revision
	 * @param errorHandler
	 * @return
	 ********************************/
	public List<SVNLogEntry> log(String path, String revision, Consumer<Exception> errorHandler) {
		return getManager().log(path, revision, errorHandler);
	}

	/**
	 * @return the allLogs
	 */
	public final Collection<SVNLogEntry> getAllLogs() {
		return allLogs;
	}

	/**
	 * @return the start
	 */
	public final GagoyleDate getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public final GagoyleDate getEnd() {
		return end;
	}

	public String getRootUrl() {
		return getManager().getUrl();
	}

	public Map<String, List<GargoyleSVNLogEntryPath>> getCollectedTable() {
		return collectedTable;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 7. 19.
	 * @param allLogs
	 * @return
	 */
	public Stream<GargoyleSVNLogEntryPath> createStream(Collection<SVNLogEntry> allLogs) {
		Stream<GargoyleSVNLogEntryPath> filter = allLogs.stream().flatMap(v -> {
			Map<String, SVNLogEntryPath> changedPaths = v.getChangedPaths();
			long revision = v.getRevision();
			Date date = v.getDate();
			if (ValueUtil.isNotEmpty(changedPaths)) {
				Set<String> keySet = changedPaths.keySet();
				Iterator<String> iterator = keySet.iterator();

				Set<GargoyleSVNLogEntryPath> arrayList = new HashSet<>(changedPaths.size());
				while (iterator.hasNext()) {
					String next = iterator.next();
					SVNLogEntryPath svnLogEntryPath = changedPaths.get(next);
					SVNNodeKind kind = svnLogEntryPath.getKind();
					/*2016-09-02 File이면서 UNKNOWN으로 리턴되는 경우가 존재해서 추가.*/
					if (SVNNodeKind.FILE == kind || SVNNodeKind.UNKNOWN == kind ) {
						GargoyleSVNLogEntryPath e = new GargoyleSVNLogEntryPath(svnLogEntryPath.getPath(), svnLogEntryPath.getType(),
								svnLogEntryPath.getPath(), revision, kind);
						e.setDate(date);
						arrayList.add(e);
					}
				}
				return arrayList.stream();
			}
			return null;
		}).filter(v -> v != null).sorted((a1, a2) ->{
			return ~Long.compare(a1.getDate().getTime(), a2.getDate().getTime());
		}).distinct();

		return filter;
	}
}
