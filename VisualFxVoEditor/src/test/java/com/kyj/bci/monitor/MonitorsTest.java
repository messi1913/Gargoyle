/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.bci.monitor
 *	작성일   : 2017. 1. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.sun.star.uno.RuntimeException;

/**
 * @author KYJ
 *
 */
public class MonitorsTest {

	int pid = -1;

	@Before
	public void setting() {
		/// Change your java pid
		pid = 6972;

		if (pid == -1)
			throw new RuntimeException(" Input java pid ");
	}

	@Test
	public final void test() throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Monitors.runStackTool(pid, out);
		System.out.println(out.toString());
	}

}
