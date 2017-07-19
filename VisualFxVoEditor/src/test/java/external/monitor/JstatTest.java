/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.monitor
 *	작성일   : 2017. 6. 26.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package external.monitor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;

import com.kyj.bci.monitor.Monitors;
import com.kyj.bci.monitor.Monitors.SnapShotType;

import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;

/**
 * @author KYJ
 *
 */
public class JstatTest {

	/*
	
	S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT   
	34944.0 34944.0  0.0   6441.9 279744.0 269548.8  699072.0   198935.8  153688.0 143003.8 20568.0 17459.9     11    0.970  15      7.458    8.428
	34944.0 34944.0  0.0   6441.9 279744.0 276245.8  699072.0   198935.8  153688.0 143003.8 20568.0 17459.9     11    0.970  15      7.458    8.428
	34944.0 34944.0  0.0   6441.9 279744.0 276400.8  699072.0   198935.8  153688.0 143003.8 20568.0 17459.9     11    0.970  15      7.458    8.428
	34944.0 34944.0 34944.0  0.0   279744.0  2988.5   699072.0   208197.6  161624.0 150396.5 21592.0 18212.5     12    1.046  15      7.458    8.504
	
	
	S0C : Displays the current size of Survior0 area in KB
	S1C : Displays the current size of Suvivor1 area in KB
	S0U : Displays the current usage of Survivor0 area in KB
	S1U : Displays the current usage of Survivor1 area in KB
	EC : Displays the current size of Eden area in KB
	EU : Displays the current usage of Eden area in KB
	OC : Displays the current usage of Eden area in KB
	OU: Displays the current size of old area in KB
	PC : Displays the current size of permanent area in KB
	PU : Displays the current usage of permanent area in KB
	YGC : The number of GC event occured in young area
	YGCT : The accumulated time for GC operations of Young area
	FGC : The numberof full GC event occured
	FGCT: The acculated time for full GC operations.
	GCT : The total accmulated time for GC operations.
	 * 
	 */
	/**
	 * @작성자 : KYJ
	 * @작성일 : 2017. 6. 26. 
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @throws MonitorException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException, MonitorException {
		//		Jstat.main(new String[] { "-gc", "7396", "1000", "10" });
		//		String[] arguments = new String[] { "-gc", "12816", "1000", "10" };
		//
		//		AtomicBoolean stop = new AtomicBoolean(false);
		//		RuntimeClassUtil.pipe(arguments, stop, arg -> {
		//			System.out.println(arg);
		//		});
		//		Monitors.runStackTool(7396, System.out);

		PrintStream out = new PrintStream(System.out) {

		};

		//		Monitors.runMemoryDump(8632, out);

		for (SnapShotType type : Monitors.SnapShotType.values()) {

			System.out.printf("Monitor type : %s \n", type.name());
			Monitors.logSnapShot(type, 8632, out);
		}

	}

	public static void resetTogglingStores(String host, boolean enabled) throws Exception {

		MonitoredHost _host = MonitoredHost.getMonitoredHost(new HostIdentifier(host));

		for (Object pidObj : _host.activeVms()) {
			int pid = (Integer) pidObj;

			System.out.println("checking pid: " + pid);

			JMXServiceURL jmxUrl = null;
			com.sun.tools.attach.VirtualMachine vm = com.sun.tools.attach.VirtualMachine.attach(pid + "");

			try {
				// get the connector address
				String connectorAddress = vm.getAgentProperties().getProperty("localhost");
				// establish connection to connector server
				if (connectorAddress != null) {
					jmxUrl = new JMXServiceURL(connectorAddress);
				}
			} finally {
				vm.detach();
			}

			if (jmxUrl != null) {
				System.out.println("got jmx url: " + jmxUrl);

				// connect to jmx
				JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);

				connector.connect();

				MBeanServerConnection mbeanServer = connector.getMBeanServerConnection();

				// look for all beans in the d2 name space
				Set<ObjectInstance> objectInstances = mbeanServer.queryMBeans(new ObjectName("com.linkedin.d2:*"), null);

				for (ObjectInstance objectInstance : objectInstances) {
					System.err.println("checking object: " + objectInstance.getObjectName());

					// if we've found a toggling store, then toggle it
					if (objectInstance.getObjectName().toString().endsWith("TogglingStore")) {
						System.out.println("found toggling zk store, so toggling to: " + enabled);

						mbeanServer.invoke(objectInstance.getObjectName(), "setEnabled", new Object[] { enabled },
								new String[] { "boolean" });
					}
				}
			} else {
				System.out.println("pid is not a jmx process: " + pid);
			}
		}
	}

	@Test
	public void test() {

	}
}
