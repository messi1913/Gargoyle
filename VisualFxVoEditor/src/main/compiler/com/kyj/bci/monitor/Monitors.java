/********************************
 *	프로젝트 : batch-schedule
 *	패키지   : com.samsung.sds.sos.schedule.module.main.monitor
 *	작성일   : 2016. 2. 24.
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

import javafx.collections.FXCollections;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.tools.jstack.JStack;
import sun.tools.jstat.Arguments;
import sun.tools.jstat.JStatLogger;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionOutputFormatter;
import sun.tools.jstat.OutputFormatter;
import sun.tools.jstat.RawOutputFormatter;

/**
 * @author KYJ
 *
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
			//			actived(arg0.getActive());
			terminated(arg0.getTerminated());
		}

		@Override
		public void disconnected(HostEvent arg0) {
			LOGGER.debug("################## disconnected");
			LOGGER.debug(arg0.toString());
		}
	};

	static Thread removeMonitorThread = new Thread(() -> {
		try {
			LOGGER.debug("Shutdown hook!");
			monitoredHost.removeHostListener(hostListener);
		} catch (MonitorException e) {
			LOGGER.error(e.toString());
		}
	});

	static {
		try {
			monitoredHost = MonitoredHost.getMonitoredHost(new sun.tools.jps.Arguments(new String[] {}).hostId());
			monitoredHost.setInterval(1000);
		} catch (MonitorException e1) {
			e1.printStackTrace();
			LOGGER.error("################## fail to load");
			LOGGER.error(ValueUtil.toString(e1));
		}

		try {

			monitoredHost.addHostListener(hostListener);

			if (monitoredHost != null) {
				// Listen 해지 작업
				Runtime.getRuntime().addShutdownHook(removeMonitorThread);
			}
		} catch (MonitorException e) {
			LOGGER.error(e.toString());
		}

	}

	public static void addListener(MonitorListener listener) {
		listeners.add(listener);
	}

	/**
	 * 리스너 대상에서 제거.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 * @param javaProcessViewController
	 */
	public static boolean removeListener(MonitorListener listener) {

		return listeners.remove(listener);
	}

	enum Status {
		started, terminated, actived
	}

	private static void notifyMessage(Status status, ApplicationModel model) {
		for (MonitorListener listener : listeners) {
			switch (status) {

			case started:
				listener.onApplicationLoaded(model);
				break;

			case actived:
				//Nothing.
				//				listener.onApplicationLoaded(model);
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

	private static void actived(Set<Integer> lvmids) {
		lvmids.forEach(v -> actived("actived", v));
	}

	private static void terminated(Set<Integer> lvmids) {
		lvmids.forEach(v -> terminated("terminated", v));
	}

	private static void terminated(String status, Integer lvmid) {
		LOGGER.debug("status[{}] V PID : {}", status, lvmid);
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
			String vmVersion = MonitoredVmUtil.vmVersion(vm);
			String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
			String commandLine = MonitoredVmUtil.commandLine(vm);

			ApplicationModel model = new ApplicationModel();
			model.setProcessId(lvmid);
			model.setApplicationName(mainClassName);
			model.setArgument(mainArgs);
			model.setJvmArgs(jvmArgs);
			model.setVmVersion(vmVersion);
			model.setJvmFlags(jvmFlags);
			model.setCommandLine(commandLine);

			cache.put(lvmid.toString(), model);
			notifyMessage(Status.started, cache.get(lvmid.toString()));

			LOGGER.debug("status[{}] V PID : {} mainClassName : {} vmVersion : {}  jvmFlags : {} commandLine : {}", status, lvmid,
					mainClassName, vmVersion, jvmFlags, commandLine);
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

	/**
	 *  활성화된 자바 프로세스 갯수를 리턴.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 */
	public static int getActivedJavaProcessCount() {
		return cache.size();
	}

	/**
	 * 활성화된 자바 프로세스 정보를 리턴한다.
	 * @return
	 * @throws CloneNotSupportedException
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 */
	public static Collection<ApplicationModel> getActivedJavaProceses() throws CloneNotSupportedException {

		ArrayList<ApplicationModel> arrayList = new ArrayList<ApplicationModel>();
		for (ApplicationModel model : cache.values()) {
			arrayList.add((ApplicationModel) model.clone());
		}
		return arrayList;
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 20.
	 * @param string
	 * @param v
	 * @return
	 */
	private static void actived(String status, Integer lvmid) {

		try {
			String vmidString = "//" + lvmid + "?mode=r";
			VmIdentifier id = new VmIdentifier(vmidString);
			MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);

			String mainArgs = MonitoredVmUtil.mainArgs(vm);
			String mainClassName = MonitoredVmUtil.mainClass(vm, true);
			String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
			String vmVersion = MonitoredVmUtil.vmVersion(vm);
			String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
			String commandLine = MonitoredVmUtil.commandLine(vm);

			ApplicationModel model = new ApplicationModel();
			model.setProcessId(lvmid);
			model.setApplicationName(mainClassName);
			model.setArgument(mainArgs);
			model.setJvmArgs(jvmArgs);
			model.setVmVersion(vmVersion);
			model.setJvmFlags(jvmFlags);
			model.setCommandLine(commandLine);

			cache.put(lvmid.toString(), model);
			notifyMessage(Status.actived, cache.get(lvmid.toString()));

			LOGGER.debug("status[{}] V PID : {} mainClassName : {} vmVersion : {}  jvmFlags : {} commandLine : {}", status, lvmid,
					mainClassName, vmVersion, jvmFlags, commandLine);

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

	/**
	 * Java PID에 해당하는 스레드 덥프를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 1. 23.
	 * @param pid
	 * @param out
	 */
	public static void runStackTool(final int pid, OutputStream out) {
		ThreadDumpUtil.runStackTool(pid, out);
	}

	/**
	 * Java PID에 해당하는 메모리 덤프를 리턴
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 26. 
	 * @param pid
	 * @param out
	 * @throws MonitorException 
	 */
	public static void runMemoryDump(final int pid, OutputStream out) {
		//		MemoryUtil.printInformation(pid, out);
		MemoryUtil.snapShotCapacity(pid, out);
	}

	public void closeMonitor() {

	}

	/**
	 * 스레드 덤프출력과 관련된 유틸리티 클래스
	 * @author KYJ
	 *
	 */
	private static class ThreadDumpUtil {

		// Returns sun.jvm.hotspot.tools.JStack if available, otherwise null.
		static Class loadSAClass() {
			//
			// Attempt to load JStack class - we specify the system class
			// loader so as to cater for development environments where
			// this class is on the boot class path but sa-jdi.jar is on
			// the system class path. Once the JDK is deployed then both
			// tools.jar and sa-jdi.jar are on the system class path.
			//
			try {
				return JStack.class; //Class.forName("sun.jvm.hotspot.tools.JStack" /*, true, ClassLoader.getSystemClassLoader()*/);
			} catch (Exception x) {
				x.printStackTrace();
			}
			return null;
		}

		// SA JStack tool
		static void runJStackTool(boolean mixed, boolean locks, String args[]) throws Exception {
			Class<?> cl = loadSAClass();

			if (cl == null) {
				usage(); // SA not available
			}

			// JStack tool also takes -m and -l arguments
			if (mixed) {
				args = prepend("-m", args);
			}
			if (locks) {
				args = prepend("-l", args);
			}

			Class[] argTypes = { String[].class };
			Method m = cl.getDeclaredMethod("main", argTypes);

			Object[] invokeArgs = { args };
			m.invoke(null, invokeArgs);
		}

		/**
		 * pid에 해당하는 java프로세스의 덤프를 OutputStream에 출력. </br>
		 *
		 * @작성자 : KYJ
		 * @작성일 : 2017. 1. 23.
		 * @param pid
		 *   	java process pid
		 * @param out
		 *  	output
		 */
		public static void runStackTool(final int pid, OutputStream out) {

			try (PrintStream ps = new PrintStream(out)) {

				//스트림을 반환받고하하는 타입으로 변경
				System.setOut(ps);
				try {
					runJStackTool(false, true, new String[] { String.valueOf(pid) });
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} finally {
				//원래의 스트림으로 대체.
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			}

		}

		// return a new string array with arg as the first element
		private static String[] prepend(String arg, String args[]) {
			String[] newargs = new String[args.length + 1];
			newargs[0] = arg;
			System.arraycopy(args, 0, newargs, 1, args.length);
			return newargs;
		}

		// print usage message
		private static void usage() {
			System.out.println("Usage:");
			System.out.println("    jstack [-l] <pid>");
			System.out.println("        (to connect to running process)");

			if (loadSAClass() != null) {
				System.out.println("    jstack -F [-m] [-l] <pid>");
				System.out.println("        (to connect to a hung process)");
				System.out.println("    jstack [-m] [-l] <executable> <core>");
				System.out.println("        (to connect to a core file)");
				System.out.println("    jstack [-m] [-l] [server_id@]<remote server IP or hostname>");
				System.out.println("        (to connect to a remote debug server)");
			}

			System.out.println("");
			System.out.println("Options:");

			if (loadSAClass() != null) {
				System.out.println("    -F  to force a thread dump. Use when jstack <pid> does not respond" + " (process is hung)");
				System.out.println("    -m  to print both java and native frames (mixed mode)");
			}

			System.out.println("    -l  long listing. Prints additional information about locks");
			System.out.println("    -h or -help to print this help message");
			System.exit(1);
		}
	}

	private static class MemoryUtil {

		static enum Type {
			
			gcutil("-gcutil"), 
			gc("-gc");
			
			private String name;

			Type(String name) {
				this.name = name;
			}
		}

		/**
		 * 메모리 사용량의 데이터를 표시한다. KB
		 * @작성자 : KYJ
		 * @작성일 : 2017. 6. 26. 
		 * @param pid
		 * @param out
		 */
		public static void snapShotCapacity(int pid, OutputStream out) {
			logSnapShot(Type.gc, pid, 0, out);
		}

		/**
		 * 차지하는 사용량의 데이터를 퍼센트로 표시한다.
		 * @작성자 : KYJ
		 * @작성일 : 2017. 6. 26. 
		 * @param pid
		 * @param out
		 */
		public static void snapShotPercentage(int pid, OutputStream out) {
			logSnapShot(Type.gcutil, pid, 0, out);
		}

		static void logSnapShot(Type type, int pid, int intervalSectound, OutputStream out) {
			logSnapShot(type, pid, intervalSectound, 1, out);
		}

		static void logSnapShot(Type type, int pid, int intervalSectound, int repeat, OutputStream out) {
			String[] args = new String[] { type.name , String.valueOf(pid), String.valueOf(intervalSectound), String.valueOf(repeat) };
			logSnapShot(new Arguments(args), out);
		}

		static void logSnapShot(Arguments arguments, OutputStream out) {
			logSnapShot(arguments, new PrintStream(out));
		}

		static void logSnapShot(Arguments arguments, PrintStream out) {
			logSamples(arguments, out);
		}

		static class TerminateListener implements HostListener {
			VmIdentifier vmId;
			JStatLogger logger;

			public TerminateListener(VmIdentifier vmId, JStatLogger logger) {
				this.vmId = vmId;
				this.logger = logger;
			}

			public void vmStatusChanged(VmStatusChangeEvent ev) {
				Integer lvmid = new Integer(vmId.getLocalVmId());
				if (ev.getTerminated().contains(lvmid)) {
					logger.stopLogging();
				} else if (!ev.getActive().contains(lvmid)) {
					logger.stopLogging();
				}
			}

			public void disconnected(HostEvent ev) {
				if (monitoredHost == ev.getMonitoredHost()) {
					logger.stopLogging();
				}
			}
		}

		static void logSamples(Arguments arguments, PrintStream out) {
			VmIdentifier vmId = arguments.vmId();
			int interval = arguments.sampleInterval();
			MonitoredVm monitoredVm = null;
			JStatLogger logger = null;
			HostListener terminator = null;
			try {
				monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
				logger = new JStatLogger(monitoredVm);
				terminator = new TerminateListener(vmId, logger);
			} catch (MonitorException e1) {

				try {
					if (monitoredVm != null)
						monitoredHost.detach(monitoredVm);
				} catch (MonitorException e) {
				}

				return;
			}

			try {
				OutputFormatter formatter = null;

				//				if (arguments.isSpecialOption()) {
				OptionFormat format = arguments.optionFormat();
				formatter = new JstatOutputFormat(monitoredVm, format);
				//				} 

				/****** Not Support. this program**********************************************************
				else {
					List<Monitor> logged = monitoredVm.findByPattern(arguments.counterNames());
					Collections.sort(logged, arguments.comparator());
					List<Monitor> constants = new ArrayList<Monitor>();
				
					for (Iterator i = logged.iterator(); i.hasNext();) {
						Monitor m = (Monitor) i.next();
						if (!(m.isSupported() || arguments.showUnsupported())) {
							i.remove();
							continue;
						}
						sun.jvmstat.monitor.Variability variability = m.getVariability();
						if (variability == sun.jvmstat.monitor.Variability.CONSTANT) {
							i.remove();
							if (arguments.printConstants())
								constants.add(m);
						} else if ((m.getUnits() == sun.jvmstat.monitor.Units.STRING) && !arguments.printStrings()) {
							i.remove();
						}
					}
				
					if (logged.isEmpty()) {
						monitoredHost.detach(monitoredVm);
						return;
					}
				
					formatter = new RawOutputFormatter(logged, arguments.printStrings());
				}
				***************************************************************/
				// handle user termination requests by stopping sampling loops
				//				Runtime.getRuntime().addShutdownHook(new Thread() {
				//					public void run() {
				//						logger.stopLogging();
				//					}
				//				});

				// handle target termination events for targets other than ourself

				if (vmId.getLocalVmId() != 0) {
					monitoredHost.addHostListener(terminator);
				}

				StringBuffer sb = new StringBuffer();
				/**print help.*/
				sb.append("/**  Help *********************************\n");
				sb.append("S0C : Displays the current size of Survior0 area in KB\n");
				sb.append("S1C : Displays the current size of Suvivor1 area in KB\n");
				sb.append("S0U : Displays the current usage of Survivor0 area in KB\n");
				sb.append("S1U : Displays the current usage of Survivor1 area in KB\n");
				sb.append("EC : Displays the current size of Eden area in KB\n");
				sb.append("EU : Displays the current usage of Eden area in KB\n");
				sb.append("OC : Current old space capacity KB \n");
				sb.append("OU: Displays the current size of old area in KB\n");
				sb.append("PC : Displays the current size of permanent area in KB\n");
				sb.append("PU : Displays the current usage of permanent area in KB\n");
				sb.append("YGC : The number of GC event occured in young area\n");
				sb.append("YGCT : The accumulated time for GC operations of Young area\n");
				sb.append("FGC : The numberof full GC event occured\n");
				sb.append("FGCT: The acculated time for full GC operations.\n");
				sb.append("GCT : The total accmulated time for GC operations.\n");
				sb.append("*****************************************/\n\n\n");
				try {
					out.write(sb.toString().getBytes());
				} catch (IOException e) {
				}

				logger.logSamples(formatter, arguments.headerRate(), arguments.sampleInterval(), arguments.sampleCount(), out);

				// detach from host events and from the monitored target jvm
				if (terminator != null) {
					monitoredHost.removeHostListener(terminator);
				}

				monitoredHost.detach(monitoredVm);
			} catch (MonitorException e) {
				e.printStackTrace();
			} finally {
				if (logger != null) {
					logger.stopLogging();
				}
			}

		}
	}
}
