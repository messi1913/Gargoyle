package kyj.Fx.scm.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.kyj.fx.voeditor.visual.util.DateUtil;

public class VidioDownloader {

	public static void main(String[] args) throws IOException {
		URL url = new URL(
				"http://video.nmv.naver.com/cafe/cafe_2016_09_09_15/8ebc4cb53633c1cb49d95cd72a10e7c7e92c_ugcvideo_1080P_01.mp4?key=MjEwMzExMjA1MDk3Mjg0OTAzMTE0MzE1MTAxOXZpZGVvLm5tdi5uYXZlci5jb20wODMvY2FmZS9jYWZlXzIwMTZfMDlfMDlfMTUvOGViYzRjYjUzNjMzYzFjYjQ5ZDk1Y2Q3MmExMGU3YzdlOTJjX3VnY3ZpZGVvXzEwODBQXzAxLm1wNDMxMjEzMTQyMDA3Y29uZXI3NDIzMjMxNzROSE5NVjAwMDAwMDQ4OTEwMDQ3NzY=&px-bps=2102420&px-bufahead=3&in_out_flag=0");
		URLConnection openConnection = url.openConnection();
		openConnection.connect();
		Map<String, List<String>> headerFields = openConnection.getHeaderFields();

		headerFields.entrySet().forEach(ent -> {
			System.out.println(ent.getKey() + " - " + ent.getValue());
		});

		
		List<String> list = headerFields.get("Content-Type");
		List<String> list2 = headerFields.get("Content-Length");
		
		
		String type = list.get(0);
		String length = list2.get(0);
		String fileSimpleName = DateUtil.getCurrentDateString(DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSSS);
		String fileExtension = ".mp4";
		File file = new File(fileSimpleName + fileExtension);
		if ("video/mp4".equals(type)) {
			fileExtension = ".mp4";
		} else {
			throw new RuntimeException("plz check file type : " + type);
		}
		
		

		long downloadedSize = 0;
		try (InputStream is = openConnection.getInputStream()) {
			byte[] b = new byte[1024 * 4];
			try (FileOutputStream out = new FileOutputStream(file)) {
				int read = -1;
				while ((read = is.read(b)) != -1) {
					downloadedSize += read;
					System.out.printf("total : %s downloaded : %d \n", length, downloadedSize);
					out.write(b);
				}
			}
		}

	}

}
