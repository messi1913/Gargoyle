import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class RootViewController {

	private static final String DOWNLOAD_COMPLETE_MESSAGE = "다운로드 완료.~";
	private static final String NEWFILE = "excel2.xls";
	private static final String ENCODE = "EUC-KR";
	private static final int START_NUMBER = 1;
	private static int LAST_NUMBER = 659;
	private static String MASTER_URL = "http://www.nlotto.co.kr/gameResult.do?method=allWinExel&nowPage=1&";
	private static String WHERE_URL = "drwNoStart=:startNumber&drwNoEnd=:endNumber";

	// http://www.nlotto.co.kr/gameResult.do?method=allWinExel&nowPage=1&drwNoStart=1&drwNoEnd=776
	@FXML
	MenuItem updateRottoNumber;

	@FXML
	public void updateRootoNumberMouseClick() throws Exception {

	}

	public static void main(String[] args) throws Exception {
		RootViewController controller = new RootViewController();
		// controller.getMaxNumber();
		// controller.download();
		controller.thisWeek();
	}

	private void thisWeek() throws Exception {
		download(getMaxNumber());

	}

	/**
	 * 로또 번호 갱신 2015. 7. 19.
	 * 
	 * @return 최신번호 KYJ
	 */
	private int getMaxNumber() {
		try {

			URL url = new URL("http://www.nlotto.co.kr/gameResult.do?method=allWin");
			Document doc = Jsoup.parse(url, 5000);
			Element parse = doc.getElementById("drwNoEnd");
			Elements elementsByTag = parse.getElementsByTag("option");
			Element element = elementsByTag.get(0);
			System.out.println("[start]changed text ##########");
			String text = element.text();
			if (text != null) {
				LAST_NUMBER = Integer.parseInt(text);
			}
			System.out.println(text);
			System.out.println("[end]changed text ##########");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return LAST_NUMBER;
	}

	private void download() throws Exception {
		download(START_NUMBER, getMaxNumber());
	}

	private void download(int startNumber) throws Exception {
		download(startNumber, getMaxNumber());
	}

	private void download(int startNumber, int endNumber) throws Exception {

		String url = MASTER_URL
				+ WHERE_URL.replace(":startNumber", String.valueOf(startNumber)).replace(":endNumber", String.valueOf(endNumber));

		System.out.printf("request url %s ,  %d %d\n", url, startNumber, endNumber);
		InputStream is = new URL(url).openStream();
		BufferedReader br = null;
		FileWriter fw = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			InputStreamReader isr = new InputStreamReader(is, ENCODE);
			br = new BufferedReader(isr);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(NEWFILE), ENCODE));
			String temp = null;

			while ((temp = br.readLine()) != null) {
				bw.write(temp);
				System.out.println(temp);
			}
			// BaseUtil.alert(DOWNLOAD_COMPLETE_MESSAGE);
		} finally {
			if (bw != null)
				bw.close();
			if (osw != null)
				osw.close();
			if (fw != null)
				fw.close();
			if (br != null)
				br.close();

		}

	}
}
