/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.json
 *	작성일   : 2016. 4. 3.
 *	작성자   : KYJ
 *******************************/
package external.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * TODO 클래스 역할
 *
 * @author KYJ
 *
 */
public class JsonTest {

	@Test
	public void jsonTest() throws ParseException {
		JSONArray jsonArray = new JSONArray();

		{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("svn.url", "http://sample.net");
			jsonObject.put("svn.userId", "userId");
			jsonObject.put("svn.userPass", "userPass");
			jsonArray.add(jsonObject);
		}
		{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("svn.url", "http://sample.ssdsds.net");
			jsonObject.put("svn.userId", "userId");
			jsonObject.put("svn.userPass", "userPass");
			jsonArray.add(jsonObject);
		}

		System.out.println(jsonArray.toJSONString());




//		JSONArray jsonArray2 = new JSONArray();
		JSONArray parse = (JSONArray) new JSONParser().parse(jsonArray.toJSONString());
		parse.forEach(System.out::println);

	}
}
