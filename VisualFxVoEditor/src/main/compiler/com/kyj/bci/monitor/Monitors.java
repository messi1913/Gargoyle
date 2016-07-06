/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 2. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import kyj.Fx.dao.wizard.core.util.ValueUtil;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;

/**
 * @author KYJ
 *
 */
public class Monitors {
	private static Logger LOGGER = LoggerFactory.getLogger(Monitors.class);
	private static MonitoredHost monitoredHost = null;

	private static List<MonitorListener> listeners = FXCollections.observableArrayList();

	private static HostListener hostListener = new HostListener() {

		@Override
		public void vmStatusChanged(VmStatusChangeEvent arg0) {
			started(arg0.getStarted());
			terminated(arg0.getTerminated());
		}

		@Override
		public void disconnected(HostEvent arg0) {
			LOGGER.debug("################## disconnected");
			LOGGER.debug(arg0.toString());
		}
	};

	static {
		try {
			monitoredHost = MonitoredHost.getMonitoredHost(new sun.tools.jps.Arguments(new String[] {}).hostId());
		} catch (MonitorException e1) {
			e1.printStackTrace();
			LOGGER.error("################## fail to load");
			System.exit(0);
		}

		try {

			monitoredHost.addHostListener(hostListener);

			if (monitoredHost != null) {
				// Listen 해지 작업
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						LOGGER.debug("Shutdown hook!");
						monitoredHost.removeHostListener(hostListener);
					} catch (MonitorException e) {
						LOGGER.error(e.toString());
					}
				}));
			}
		} catch (MonitorException e) {
			LOGGER.error(e.toString());
		}

	}

	public static void addListener(MonitorListener listener) {
		listeners.add(listener);
	}

	enum Status {
		started, terminated
	}

	private static void notifyMessage(Status status, ApplicationModel model) {
		for (MonitorListener listener : listeners) {
			switch (status) {
			case started:
				listener.onApplicationLoaded(model);
				break;

			case terminated:
				listener.onApplicationTerminated(model);
				break;
			}
		}

	}

	private static void started(Set<Integer> lvmids) {
		lvmids.forEach(v -> started("started", v));
	}

	private static void terminated(Set<Integer> lvmids) {
		lvmids.forEach(v -> terminated("terminated", v));
	}

	private static void terminated(String status, Integer lvmid) {
		System.out.println(String.format("status[%s] V PID : %d", status, lvmid));
		// ApplicationModel model = new ApplicationModel();
		// model.setProcessId(lvmid);
		ApplicationModel model = cache.get(lvmid.toString());
		notifyMessage(Status.terminated, model);
		cache.remove(lvmid.toString());
	}

	private static Map<String, ApplicationModel> cache = new ConcurrentHashMap<String, ApplicationModel>();

	private static void started(String status, Integer lvmid) {

		try {
			String vmidString = "//" + lvmid + "?mode=r";
			VmIdentifier id = new VmIdentifier(vmidString);
			MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);
			String mainArgs = MonitoredVmUtil.mainArgs(vm);
			String mainClassName = MonitoredVmUtil.mainClass(vm, true);
			String jvmArgs = MonitoredVmUtil.jvmArgs(vm);

			ApplicationModel model = new ApplicationModel();
			model.setProcessId(lvmid);
			model.setApplicationName(mainClassName);
			model.setArgument(mainArgs);
			model.setJvmArgs(jvmArgs);

			cache.put(lvmid.toString(), model);
			notifyMessage(Status.started, cache.get(lvmid.toString()));

			LOGGER.debug(String.format("status[%s] V PID : %d mainClassName :%s", status, lvmid, mainClassName));
			monitoredHost.detach(vm);
		}
		/*
		 * 수행이 빠른 프로그램인경우 모니터링이되지않고 중간에 에러가 발생하는 케이스가 있다.
		 */
		catch (sun.jvmstat.monitor.MonitorException e) {
			LOGGER.warn(ValueUtil.toString(e));
		} catch (Exception e) {
			LOGGER.error(ValueUtil.toString(e));
		}
	}
}
