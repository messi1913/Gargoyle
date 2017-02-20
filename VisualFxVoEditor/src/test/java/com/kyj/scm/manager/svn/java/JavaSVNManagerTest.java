/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.scm.manager.svn.java
 *	작성일   : 2017. 2. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn.java;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;

import com.kyj.fx.voeditor.visual.momory.ResourceLoader;
import com.kyj.fx.voeditor.visual.util.EncrypUtil;

/**
 * SVN 접속 테스트 코드
 * @author KYJ
 *
 */
public class JavaSVNManagerTest {

	JavaSVNManager manager;

	JSONObject jsonObj;

	@Before
	public void setting() throws ParseException {

		JSONArray jsonArray = ResourceLoader.getInstance().getJsonArray("svn.repositores");
		if (jsonArray.isEmpty()) {
			Assert.fail("properties is empty.");
		}
		jsonObj = (JSONObject) jsonArray.get(0);
		if (jsonObj.isEmpty()) {
			Assert.fail("properties is empty.");
		}

		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		Iterator iterator = jsonObj.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			Object val = jsonObj.get(key);

			try {
				String string = key.toString();
				if (JavaSVNManager.SVN_USER_PASS.equals(string))
					hashMap.put(string, EncrypUtil.decryp(val.toString()));
				else
					hashMap.put(string, val.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Properties properties = new Properties();
		properties.put(JavaSVNManager.SVN_URL, hashMap.get(JavaSVNManager.SVN_URL));
		properties.put(JavaSVNManager.SVN_USER_ID, hashMap.get(JavaSVNManager.SVN_USER_ID));
		properties.put(JavaSVNManager.SVN_USER_PASS, hashMap.get(JavaSVNManager.SVN_USER_PASS));
		manager = new JavaSVNManager(properties);
	}

	/**
	 * Test method for {@link com.kyj.scm.manager.svn.java.JavaSVNManager#listEntry(java.lang.String)}.
	 */
	@Test
	public final void testList() {
		List<String> list = manager.list("/sos/batch-core");
		list.forEach(System.out::println);
	}

	@Test
	public final void testListEntry() throws Exception {
		manager.listEntry("/sos/batch-core", new SVNDirHandler() {

			@Override
			public boolean test(SVNDirEntry entry) {
				return entry.getRevision() >= 619073L;
			}

			@Override
			public void accept(SVNDirEntry entry) {
				if (entry.getRevision() >= 619073L) {
					SVNNodeKind kind = entry.getKind();
					if (SVNNodeKind.DIR != kind) {
						System.out.println("## File");
						System.out.println(entry);
						System.out.println("## Content");
						System.out.println(manager.cat(entry.getURL()));
					}
				}

			}
		});

	}

	/**
	 * Test method for {@link com.kyj.scm.manager.svn.java.JavaSVNManager#listEntry(java.lang.String, java.util.function.Consumer)}.
	 */
	@Test
	public final void testListEntryStringConsumerOfException() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.kyj.scm.manager.svn.java.JavaSVNManager#listEntry(java.lang.String, java.lang.String, java.util.function.Consumer)}.
	 */
	@Test
	public final void testListEntryStringStringConsumerOfException() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.kyj.scm.manager.svn.java.JavaSVNManager#listEntry(java.lang.String, java.lang.String, boolean, java.util.function.Consumer)}.
	 */
	@Test
	public final void testListEntryStringStringBooleanConsumerOfException() {
		fail("Not yet implemented"); // TODO
	}

}
