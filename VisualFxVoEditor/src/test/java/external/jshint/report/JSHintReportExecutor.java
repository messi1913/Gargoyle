/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.jshint.report
 *	작성일   : 2017. 12. 11.
 *	작성자   : KYJ
 *******************************/
package external.jshint.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.RuntimeClassUtil;
import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class JSHintReportExecutor {
	ExecutorService newFixedThreadPool;

	@Before
	public void before() throws InterruptedException {
		newFixedThreadPool = Executors.newFixedThreadPool(8);

	}

	@Test
	public void test() throws Exception {

		String target = "C:\\SVN_WORKSPACE\\wwwroot";
		BiConsumer<Integer, String> messageReceiver = (idx, str) -> {
			System.out.println(str);
		};

		// messageReceiver = null;

		String userHome = System.getProperty("user.home");
		System.out.println(userHome);
		File file = new File(target);

		ArrayList<Callable<Integer>> list = new ArrayList<>();
		File[] listFiles = file.listFiles();
		for (File f : listFiles) {
			if (f.isDirectory()) {

//				if (!"MaterialMovement".equals(f.getName()))
//					continue;

				Callable<Integer> task = new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						System.out.println(f.getName() + " job Start.");

						String simpleOutputName = f.getName() + "-report.html";
						File file2 = new File(file.getParentFile(), simpleOutputName);

						System.out.println(file2.getAbsolutePath());
						// String outputFileName = f.getName() + "-report.html";

						if (file2.exists())
							return 0;
						RuntimeClassUtil.exeSynch(Arrays.asList(
						/* command location */
						userHome + "\\node_modules\\.bin\\jshint.cmd"

						/**/
						, f.getAbsolutePath()

						/* exclude */
//						,"--exclude-path", ".jshintignore"

						 ,"--exclude" , "**/bootstrap*.js,**/*.min.js, **/jquery.min.js, jquery*.js , **/knockout*.js, "

						/* config */
//						,"--config" , "C:\\SVN_WORKSPACE\\jshintconfig.json",

						/* report template. */
						, "--reporter", userHome + "\\node_modules\\jshint-html-reporter\\reporter.js"

//								, "-verbose"

						// /* redirect */
						, ">", file2.getAbsolutePath()

						), "EUC-KR",

								new Consumer<ProcessBuilder>() {
									@Override
									public void accept(ProcessBuilder pb) {
										
										pb.directory(new File(target));

										System.out.println(ValueUtil.toString(pb.environment()));
									}
								}, messageReceiver, err -> {
									System.err.println(err);
									System.exit(-1);
								});

						if (file2.exists() && file2.length() == 12288) {
							System.out.println(file2 + " " + file2.length());
							file2.delete();
							// System.out.println(file.length());
						}
						System.out.println(f.getName() + " job Complete.");
						return 1;
					}

				};

				list.add(task);

			} else {
				System.out.println("skip : " + f + " is not dir. ");
			}

		}
		System.out.println("start invoke");
		List<Future<Integer>> invokeAll = newFixedThreadPool.invokeAll(list);
		System.out.println("end invoke");
		invokeAll.forEach(System.out::println);

		// newFixedThreadPool.awaitTermination(120, TimeUnit.SECONDS);
		// Thread.sleep(30000);
	}
}
