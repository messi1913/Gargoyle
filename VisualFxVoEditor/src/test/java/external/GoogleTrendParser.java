/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2016. 11. 1.
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 *
 * 변환된 JSON형태의 텍스트를 실제 JSON 객체로 변환해본다.
 * @author KYJ
 *
 */
public class GoogleTrendParser {

	String sample = "";

	@Before
	public void readSample() throws IOException {
		sample = ValueUtil.toString(GoogleTrendParser.class.getResourceAsStream("GoogleTrendSample.json"));
		System.out.println("######################################################");
		System.out.println("read string");
		System.out.println(sample);
		System.out.println("######################################################");
	}
	
	

	@Test
	public void test() throws ParseException {
		//		new org.json.JSONObject(sample);
		//		new JSONParser().parse(sample);

		JSONObject jsonObject = ValueUtil.toJSONObject(sample);
		System.out.println(jsonObject);

		System.out.println("table #########################");
		Map<String,Object> x = (Map<String, Object>) jsonObject.get("table");
		List<Map<String,Object>> cols = (List<Map<String, Object>>) x.get("cols");
		System.out.println(cols);
		System.out.println("rows #########################");


		System.out.println(x.get("rows"));
		//		System.out.println(jsonObject);
	}
}
