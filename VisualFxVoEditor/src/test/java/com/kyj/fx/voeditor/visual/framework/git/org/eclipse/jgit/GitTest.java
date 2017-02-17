/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.git
 *	작성일   : 2017. 2. 10.
 *	작성자   : KYJ
 *******************************/

/*
Rate limit
The Search API has a custom rate limit. For requests using Basic Authentication, OAuth, or client ID and secret, you can make up to 30 requests per minute. For unauthenticated requests, the rate limit allows you to make up to 10 requests per minute.
*/

package com.kyj.fx.voeditor.visual.framework.git.org.eclipse.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Before;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;
import com.kyj.fx.voeditor.visual.util.DateUtil;
import com.sun.star.uno.RuntimeException;

/**
 *
 *  org.kohsuke.github API는 project checkout 기능이 불가능한것으로 보이고
 *  일정 request이후 응답시간이 너무 김.
 * @author KYJ
 *
 */

public class GitTest {
	@Before
	public void environment() throws Exception {
		new ProxyInitializable().initialize();
	}

	private UsernamePasswordCredentialsProvider usernamePasswordCredentials;

	@Before
	public void credential() throws IOException {
		usernamePasswordCredentials = new UsernamePasswordCredentialsProvider("callakrsos@naver.com", "zkffk88");
		//		new File("");
	}

	/**
	 * 프로젝트 import 예제 기능 이상없음.
	 * @작성자 : KYJ
	 * @작성일 : 2017. 2. 15.
	 * @throws IOException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	@Test
	public void importProject() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		if (true)
			throw new RuntimeException("if you want test, plz remove this line.");

		CloneCommand cloneRepository = Git.cloneRepository();
		File directory = new File("GitCheckoutDir");
		directory.mkdirs();
		cloneRepository.setDirectory(directory);
		//코드를 다운받을지 유무
		cloneRepository.setNoCheckout(true);
		cloneRepository.setURI("https://github.com/callakrsos/Gargoyle");
		cloneRepository.setCredentialsProvider(usernamePasswordCredentials);
		Git call = cloneRepository.call();
		System.out.println(call);
	}

	@Test
	public void readContent() throws IOException {
	}

	@Test
	public void history() throws IOException, NoHeadException, GitAPIException {
		Git open = Git.open(new File("C:\\Users\\KYJ\\.git\\Gargoyle"));
		LogCommand log = open.log();
		log.call().forEach(coit -> {


			ObjectId id = coit.getId();
			String idName = id.getName();
			String fullMessage = coit.getFullMessage();
			String name = coit.getCommitterIdent().getName();
			Date when = coit.getCommitterIdent().getWhen();
			String date = DateUtil.toString(when, DateUtil.SYSTEM_DATEFORMAT_YYYY_MM_DD_HH_MM_SS);

			System.out.printf("%s \t\t %s \t\t %s\t\t %s\n", idName, date, name, fullMessage);


//			ObjectDatabase objectDatabase = log.getRepository().getObjectDatabase();
//			try {
//				ObjectLoader loader = objectDatabase.open(id);
//				ByteArrayOutputStream out = new ByteArrayOutputStream();
//				loader.copyTo(out);
//				System.out.println(out.toString());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

		});

	}
}
