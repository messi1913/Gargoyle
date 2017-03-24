package com.kyj.fx.voeditor.visual.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Hyesung Lee
 */
public class SSOAuth {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuth.class);

	/**
	 * Returns <code>null</code> when if error occured while getting SSO information.
	 * or returns an empty string that <code>""</code> when if the user not logged in.
	 * the default timeout of getting SSO information is 1000ms.
	 * if wanna change the timeout, you can call {@link #getSecureBox(long, TimeUnit)}.
	 *
	 * @since 2015. 10. 29
	 * @author Hyesung Lee
	 * @return
	 */
	public static String getSecureBox() {
		return getSecureBox(1000, TimeUnit.MILLISECONDS);
	}

	public static String getSecureBox(long timeout, TimeUnit unit) {
		LOGGER.debug(">> start of method.");

		String secureBox = null;

		RandomAccessFile pipe = null;
		File file = new File(System.getProperty("java.io.tmpdir"), "SSOAuth.exe");
		try {
			boolean result = true;
			try {
				// there is a possibility that already executed the SSOAuth.exe. so try first to IPC communication via named pipe.
				pipe = new RandomAccessFile("\\\\.\\pipe\\ssoauth", "rw");
			} catch (FileNotFoundException e) {
				result = false;
				// in case of this, not executed SSOAuth.exe

				// extract(file copy) to OS's tmp.dir and execute.
				Files.copy(SSOAuth.class.getClassLoader().getResourceAsStream("SSOAuth.exe"), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);

				ProcessBuilder processBuilder = new ProcessBuilder(file.getAbsolutePath());
				LOGGER.debug(" >> start wait for process.");
				processBuilder.start().waitFor(timeout, unit);
				LOGGER.debug(" << end wait for process.");
			} finally {
				// executed the SSOAuth.exe at catch block. so try again to IPC communication via named pipe.
				if (result == false) {
					pipe = new RandomAccessFile("\\\\.\\pipe\\ssoauth", "rw");
				}
			}

			pipe.writeBytes("GetSecureBox");
			secureBox = pipe.readLine().trim();

			LOGGER.debug(String.join("", "response result of secure box = ", String.valueOf(secureBox != null && secureBox.length() > 0)));
		} catch (Exception e) {
			// could not execute the SSOAuth.exe. in case of this, cannot SSO authentication.
		} finally {
			try {
				if (pipe != null) {
					pipe.close();
				}
			} catch (Exception e) {
				;
			}

			/**
			 * if below value is true it slows down b/c everytime looping next process. extract -> communicate -> kill -> delete.
			 * but you should know that below value is false it will always instanced-up since after first time communicated.
			 * i'll create this value as hard-coding b/c it is very important for performance. (about 10 seconds faster)
			 */
			boolean shouldKillAndDeleteAfterCommunication = false;
			if (shouldKillAndDeleteAfterCommunication) {
				try {


//					RuntimeClassUtil.exe("TASKKILL /F /IM SSOAuth.exe /T".split(" "));
					// kill to SSOAuth.exe.
//					SystemUtil.runCommand();
				} catch (Exception e) {
					;
				}

				try {
					if (file != null) {
						// there is a possibility that the file handle has not been unlocked. so delete the SSOAuth.exe after 100ms later.
						Thread.sleep(100);
						file.delete();
					}
				} catch (Exception e) {
					;
				}
			}
		}

		LOGGER.debug("<< end of method.");

		return secureBox;
	}

	/**
	 * Returns <tt>true</tt> if the SSO is alive and available. otherwise <tt>false</tt>.
	 *
	 * @since 2016. 09. 20
	 * @author Hyesung Lee
	 * @return
	 */
	public static boolean isAlive() {
		boolean isAlive = false;

		try {
			RandomAccessFile pipe = new RandomAccessFile("\\\\.\\pipe\\ssoauth", "rw");
			if (pipe != null) {
				try {
					pipe.writeBytes("GetSecureBox");
					isAlive = (pipe.readLine().trim().length() > 0 ? true : false);
				} catch (Exception e) {
					;
				} finally {
					try {
						pipe.close();
					} catch (Exception e) {
						;
					}
				}
			}
		} catch (Exception e) {
			;
		}

		return isAlive;
	}

	/**
	 * Returns <tt>true</tt> if the SSO is not alive or not available. otherwise <tt>false</tt>.
	 *
	 * @since 2016. 09. 20
	 * @author Hyesung Lee
	 * @return
	 */
	public static boolean isNotAlive() {
		return !isAlive();
	}
}
