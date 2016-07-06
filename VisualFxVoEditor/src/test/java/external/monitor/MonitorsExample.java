package external.monitor;

/********************************
 *	프로젝트 : spring_batch_tutorial
 *	패키지   : com.mkyong
 *	작성일   : 2016. 2. 24.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/

import java.util.Set;

/**
 * @author KYJ
 *
 */
public class MonitorsExample
{
//
//	private static MonitoredHost monitoredHost = null;
//	private static Arguments arguments = null;
//
//	/**
//	 * @작성자 : KYJ
//	 * @작성일 : 2016. 2. 24.
//	 * @param args
//	 * @throws MonitorException
//	 */
//	public static void main(String[] args) throws MonitorException
//	{
//		arguments = new sun.tools.jps.Arguments(args);
//
//		try
//		{
//			monitoredHost = MonitoredHost.getMonitoredHost(arguments.hostId());
//		} catch (MonitorException e1)
//		{
//
//			e1.printStackTrace();
//			System.exit(0);
//		}
//
//		monitoredHost.addHostListener(new HostListener()
//		{
//
//			@Override
//			public void vmStatusChanged(VmStatusChangeEvent arg0)
//			{
//				started(arg0.getStarted());
//				terminated(arg0.getTerminated());
//			}
//
//			@Override
//			public void disconnected(HostEvent arg0)
//			{
//				System.out.println(arg0);
//			}
//		});
//
//		for (;;)
//		{
//
//			try
//			{
//				Thread.sleep(1000);
//			} catch (InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	public static void started(Set<Integer> lvmids)
//	{
//		lvmids.forEach(v -> print("started", v));
//	}
//
//	public static void terminated(Set<Integer> lvmids)
//	{
//		lvmids.forEach(v -> printTerminated("terminated", v));
//	}
//
//	public static void printTerminated(String status, Integer lvmid)
//	{
//		System.out.println(String.format("status[%s] V PID : %d", status, lvmid));
//	}
//
//	public static void print(String status, Integer lvmid)
//	{
//
//		try
//		{
//			String vmidString = "//" + lvmid + "?mode=r";
//			VmIdentifier id = new VmIdentifier(vmidString);
//			MonitoredVm vm = monitoredHost.getMonitoredVm(id, 0);
//			boolean showLongPaths = arguments.showLongPaths();
//			System.out.println(showLongPaths);
//			String mainArgs = MonitoredVmUtil.mainArgs(vm);
//			System.out.println(mainArgs);
//
//			Arguments arguments = new Arguments(mainArgs.split(" "));
//			arguments.printUsage(arg0);
//			String mainClassName = MonitoredVmUtil.mainClass(vm, showLongPaths);
//			System.out.println(String.format("status[%s] V PID : %d mainClassName :%s", status, lvmid, mainClassName));
//			monitoredHost.detach(vm);
//		} catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//
//	}
}
