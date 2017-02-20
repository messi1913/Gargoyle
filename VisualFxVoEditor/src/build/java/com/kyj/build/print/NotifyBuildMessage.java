/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.build.print
 *	작성일   : 2017. 2. 17.
 *	작성자   : KYJ
 *******************************/
package com.kyj.build.print;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;

/**
 *
 * ant를 이용한 build.xml파일을 빌드할때
 *
 * 메세지 처리를 구현.
 * @author KYJ
 *
 */
public class NotifyBuildMessage {

	public static void main(String[] args) throws URISyntaxException {

		URL resource = ClassLoader.getSystemClassLoader().getResource("");
		File parentFile = new File(resource.toURI()).getParentFile().getParentFile().getParentFile();
		System.out.println(parentFile.getAbsolutePath());
		if (args == null || args.length == 0) {
			System.out.println("Notify.");
			return;
		}

		String version = args[0];
		System.out.println(String.format("Gargoyle %s is success!", version));

		//Database Write

		Iterator<Entry<Object, Object>> iterator = ResourceLoader.getInstance().getEntry().iterator();
		while (iterator.hasNext()) {
			Entry<Object, Object> next = iterator.next();
			System.out.println(String.format("%s %s", next.getKey(), next.getValue()));
		}

		ResourceLoader.getInstance().put("write.exam", "test val");

		System.out.println(ResourceLoader.getInstance().get("write.exam"));

		//		ValueUtil.toString(new RuntimeException("zz"));
	}

	//	private static final Supplier<Connection> supplier = new Supplier<Connection>() {
	//
	//		@Override
	//		public Connection get() {
	//			try {
	//				String url = "jdbc:sqlite:" + ValueUtil.getBaseDir() + File.separator + "gargoyle.db";
	//				return DbUtil.getConnection("org.sqlite.JDBC", url, new Properties());
	//			} catch (ClassNotFoundException e) {
	//				e.printStackTrace();
	//			} catch (Exception e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//
	//			return null;
	//		}
	//	};
	//
	//	void write() throws Exception {
	//
	//		try (Connection con = supplier.get()) {
	//			List<Map<String, Object>> select = DbUtil.select(con, "select * from tbm_sys_envsd");
	//			select.forEach(System.out::println);
	//		}
	//
	//	}
}
