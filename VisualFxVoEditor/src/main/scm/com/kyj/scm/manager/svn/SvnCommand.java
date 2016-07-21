/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.visual.util.ValueUtil;
import com.kyj.scm.manager.core.commons.SCMListener;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * 프로세스 동작
 *
 * @author KYJ
 *
 */
@Deprecated
class SvnCommand {

	private static Logger LOGGER = LoggerFactory.getLogger(SvnCommand.class);

	private static SvnCommand sigleton;

	/**
	 * 메세지내용을 바로 수신하고자하는경우 사용.
	 *
	 * @최초생성일 2016. 3. 24.
	 */
	private ObservableList<SCMListener> listners = FXCollections.observableArrayList();

	public static SvnCommand getInstance() {

		if (sigleton == null)
			sigleton = new SvnCommand();
		return sigleton;
	}

	SvnCommand() {
		listners.addListener(new ListChangeListener<SCMListener>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends SCMListener> c) {
				if (c.next()) {
					if (c.wasAdded()) {
						LOGGER.debug("Listener Added....");
					}
					if (c.wasRemoved()) {
						LOGGER.debug("Listener Removed....");
					}
					if (c.wasPermutated()) {
						LOGGER.debug("Listener Permutated....");
					}
				}
			}
		});
	}

	/**
	 * 리스너 등록
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param registListener
	 */
	public void registListener(SCMListener registListener) {
		this.listners.add(registListener);
	}

	/**
	 * 수신을 해제함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param registListener
	 */
	public void removeListener(SCMListener registListener) {
		this.listners.remove(registListener);
	}

	/**
	 * 수신자들에게 메세지를 통보함.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 3. 24.
	 * @param message
	 */
	private void notifyMessage(String message) {
		for (SCMListener l : listners) {
			l.listen(message);
		}

	}

	public List<String> exe(String encode, List<String> userArgus) throws Exception {
		ArrayList<String> arguments = new ArrayList<String>(userArgus);
		List<String> resultList = new ArrayList<String>();
		ProcessBuilder pb = new ProcessBuilder(arguments);
		pb = pb.redirectErrorStream(true);
		InputStream i = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Process p = null;

		try {
			p = pb.start();
			p.waitFor(2000, TimeUnit.SECONDS);
			i = p.getInputStream();

			isr = new InputStreamReader(i, encode);
			br = new BufferedReader(isr);

			String temp = null;

			// BufferedReader errorReader = new BufferedReader(new
			// InputStreamReader(p.getErrorStream()));

			// while ((temp = errorReader.readLine()) != null)
			// System.err.println(temp);

			while ((temp = br.readLine()) != null) {

				String message = ValueUtil.rightTrim(temp);
				notifyMessage(message);
				resultList.add(message + "\n");

				if (resultList.size() > 99999) {

					resultList.add("내용이 너무 길어 중간에 중단." + "\n");
					LOGGER.debug("내용이 너무 길어 중간에 중단.");
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							LOGGER.error(ValueUtil.toString(e));
						}
					}
					throw new RuntimeException("내용이 너무 길어 중간에 중단." + "\n");

				}

			}

			br.close();
			isr.close();
			i.close();
			p.destroy();
			p.exitValue();

		} catch (IOException e) {
			LOGGER.error(ValueUtil.toString(e));
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}
			if (i != null) {
				try {
					i.close();
				} catch (IOException e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error(ValueUtil.toString(e));
				}
			}
			if (p != null) {
				p.destroy();
				LOGGER.debug("exit value : %s\n", p.exitValue());

			}

		}

		resultList.forEach(a -> LOGGER.debug("result data ::: {}", a));

		return resultList;
	}

	public String exeReuturnString(String encode, List<String> args) {

		StringBuffer sb = new StringBuffer();
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		InputStream i = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Process p = null;
		try {
			p = pb.start();
			i = p.getInputStream();
			isr = new InputStreamReader(i, "");
			br = new BufferedReader(isr);

			String temp = null;

			while ((temp = br.readLine()) != null) {
				sb.append(ValueUtil.rightTrim(temp)).append("\n");
			}

			i = p.getErrorStream();
			isr = new InputStreamReader(i, encode);
			br = new BufferedReader(isr);
			while ((temp = br.readLine()) != null) {
				String message = ValueUtil.rightTrim(temp);
				notifyMessage(message);
				sb.append(message).append("\n");
			}
			LOGGER.debug("exit value : {}\n", p.exitValue());

			br.close();
			isr.close();
			i.close();
			p.destroy();
			p.exitValue();

		} catch (IOException e) {
			
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}
			if (i != null) {
				try {
					i.close();
				} catch (IOException e1) {
					LOGGER.error(ValueUtil.toString(e1));
				}
			}

		} finally {
			if (p != null) {
				p.destroy();
				p.exitValue();
			}

		}

		return sb.toString();
	}

	// static class ValueUtil {
	//
	// public static void print(String format, Object... args) {
	// LOGGER.debug(String.format(format, args));
	// }
	//
	// public static void print(List<String> obj) {
	// LOGGER.debug(obj.toString());
	// }
	//
	// public static void print(Exception e) {
	// e.printStackTrace();
	// }
	//
	// public static void println(String obj) {
	// LOGGER.debug(obj);
	// }
	//
	// public static boolean isNotEmpty(Object obj) {
	// boolean flag = false;
	// if (obj == null) {
	// return true;
	// }
	//
	// if (obj instanceof String) {
	// String valueOf = String.valueOf(obj);
	// if (valueOf.length() == 0 || valueOf.trim().equals("") ||
	// valueOf.equals("null")) {
	// flag = true;
	// }
	// } else if (obj instanceof Collection) {
	// List<?> list = (List<?>) obj;
	// flag = list.isEmpty();
	// } else if (obj instanceof Map) {
	//
	// if (obj == null)
	// return false;
	//
	// Map<?, ?> map = (Map<?, ?>) obj;
	// flag = map.isEmpty();
	// }
	//
	// return flag;
	// }
	//

	//
	// }
}
