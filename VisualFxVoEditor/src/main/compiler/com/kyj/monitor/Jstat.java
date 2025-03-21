/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.monitor
 *	작성일   : 2017. 6. 26.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.monitor;

/**
 * @author KYJ
 *
 */
/*
 * Copyright (c) 2004, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.management.counter.Units;
import sun.management.counter.Variability;
import sun.tools.jstat.Arguments;
import sun.tools.jstat.JStatLogger;
import sun.tools.jstat.OptionFormat;
import sun.tools.jstat.OptionLister;
import sun.tools.jstat.OptionOutputFormatter;
import sun.tools.jstat.OutputFormatter;
import sun.tools.jstat.RawOutputFormatter;

/**
 * Application to output jvmstat statistics exported by a target Java
 * Virtual Machine. The jstat tool gets its inspiration from the suite
 * of 'stat' tools, such as vmstat, iostat, mpstat, etc., available in
 * various UNIX platforms.
 *
 * @author Brian Doherty
 * @since 1.5
 */
public class Jstat {
	private static Arguments arguments;

	public static void main(String[] args) {
		try {
			arguments = new Arguments(args);
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			Arguments.printUsage(System.err);
			System.exit(1);
		}

		if (arguments.isHelp()) {
			Arguments.printUsage(System.out);
			System.exit(0);
		}

		if (arguments.isOptions()) {
			OptionLister ol = new OptionLister(arguments.optionsSources());
			ol.print(System.out);
			System.exit(0);
		}

		try {
			if (arguments.isList()) {
				logNames();
			} else if (arguments.isSnap()) {
				logSnapShot();
			} else {
				logSamples();
			}
		} catch (MonitorException e) {
			if (e.getMessage() != null) {
				System.err.println(e.getMessage());
			} else {
				Throwable cause = e.getCause();
				if ((cause != null) && (cause.getMessage() != null)) {
					System.err.println(cause.getMessage());
				} else {
					e.printStackTrace();
				}
			}
			System.exit(1);
		}
		System.exit(0);
	}

	static void logNames() throws MonitorException {
		VmIdentifier vmId = arguments.vmId();
		int interval = arguments.sampleInterval();
		MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
		MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
		JStatLogger logger = new JStatLogger(monitoredVm);
		logger.printNames(arguments.counterNames(), arguments.comparator(), arguments.showUnsupported(), System.out);
		monitoredHost.detach(monitoredVm);
	}

	static void logSnapShot() throws MonitorException {
		VmIdentifier vmId = arguments.vmId();
		int interval = arguments.sampleInterval();
		MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
		MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
		JStatLogger logger = new JStatLogger(monitoredVm);
		logger.printSnapShot(arguments.counterNames(), arguments.comparator(), arguments.isVerbose(), arguments.showUnsupported(),
				System.out);
		monitoredHost.detach(monitoredVm);
	}

	static void logSamples() throws MonitorException {
		final VmIdentifier vmId = arguments.vmId();
		int interval = arguments.sampleInterval();
		final MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(vmId);
		MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, interval);
		final JStatLogger logger = new JStatLogger(monitoredVm);
		OutputFormatter formatter = null;

		if (arguments.isSpecialOption()) {
			OptionFormat format = arguments.optionFormat();
			formatter = new OptionOutputFormatter(monitoredVm, format);
		} else {
			List<Monitor> logged = monitoredVm.findByPattern(arguments.counterNames());
			Collections.sort(logged, arguments.comparator());
			List<Monitor> constants = new ArrayList<Monitor>();

			for (Iterator i = logged.iterator(); i.hasNext(); /* empty */) {
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

			if (!constants.isEmpty()) {
				logger.printList(constants, arguments.isVerbose(), arguments.showUnsupported(), System.out);
				if (!logged.isEmpty()) {
					System.out.println();
				}
			}

			if (logged.isEmpty()) {
				monitoredHost.detach(monitoredVm);
				return;
			}

			formatter = new RawOutputFormatter(logged, arguments.printStrings());
		}

		// handle user termination requests by stopping sampling loops
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.stopLogging();
			}
		});

		// handle target termination events for targets other than ourself
		HostListener terminator = new HostListener() {
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
		};

		if (vmId.getLocalVmId() != 0) {
			monitoredHost.addHostListener(terminator);
		}

		logger.logSamples(formatter, arguments.headerRate(), arguments.sampleInterval(), arguments.sampleCount(), System.out);

		// detach from host events and from the monitored target jvm
		if (terminator != null) {
			monitoredHost.removeHostListener(terminator);
		}
		monitoredHost.detach(monitoredVm);
	}
}