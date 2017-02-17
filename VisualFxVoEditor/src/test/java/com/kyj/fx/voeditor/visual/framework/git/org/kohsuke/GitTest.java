/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.git
 *	작성일   : 2017. 2. 10.
 *	작성자   : KYJ
 *******************************/

/*
Rate limit
The Search API has a custom rate limit. For requests using Basic Authentication,
OAuth, or client ID and secret, you can make up to 30 requests per minute.
For unauthenticated requests, the rate limit allows you to make up to 10 requests per minute.
*/

package com.kyj.fx.voeditor.visual.framework.git.org.kohsuke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;

import com.kyj.fx.voeditor.visual.main.initalize.ProxyInitializable;

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

	private File credentialFile;

	@Before
	public void credential() throws IOException {

		credentialFile = new File(System.getProperty("user.home"), ".github");
		//credential 설정 디렉토리 생성.
		if (credentialFile.createNewFile()) {

			try (FileWriter writer = new FileWriter(credentialFile)) {
				writer.write("login=callakrsos@naver.com");
				writer.write("password=zkffk88");
				writer.flush();
			}
		}
	}

	@After
	public void clear() {
		if (credentialFile != null && credentialFile.exists()) {
			credentialFile.deleteOnExit();
		}
	}

	@Test
	public void connectTest() throws IOException {
		GitHub github = GitHub.connect();
		github.checkApiUrlValidity();

	}

	@Test
	public void listRepositories() throws IOException {
		GitHub github = GitHub.connect();
		PagedIterable<GHRepository> listAllPublicRepositories = github.listAllPublicRepositories();
		PagedIterator<GHRepository> iterator = listAllPublicRepositories.iterator();
		while (iterator.hasNext()) {
			GHRepository next = iterator.next();
			System.out.println(next);
		}

	}

	@Test
	public void getGargoyleRepository() throws IOException {
		GitHub github = GitHub.connect();
		GHUser user = github.getUser("callakrsos");
		Map<String, GHRepository> repositories = user.getRepositories();

		//Repository를 모두 출력
		Iterator<Entry<String, GHRepository>> iterator = repositories.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, GHRepository> next = iterator.next();
			System.out.println(next.getKey() + " - " + next.getValue().getName());
		}

		//Gargoyle Repository 정보 출력
		GHRepository repository = user.getRepository("Gargoyle");
		System.out.println(repository);

		List<GHContent> directoryContent = repository.getDirectoryContent("/");

		//루트 디렉토리 정보 출력
		directoryContent.forEach(con -> {
			System.out.println(String.format("Path : %s\t\t\tsize : %d\t\t\t\tUrl:%s ", con.getPath(), con.getSize(), con.getUrl()));
		});
	}

	@Test
	public void importProject() throws IOException {
		//Can't

	}

	@Test
	public void readContent() throws IOException {

		GitHub github = GitHub.connect();
		GHUser user = github.getUser("callakrsos");

		//Gargoyle Repository 정보 출력
		GHRepository repository = user.getRepository("Gargoyle");
		System.out.println(repository);

		GHContent fileContent = repository.getFileContent(".project", "master");

		String string = fileContent.toString();
		System.out.println(string);

		String downloadUrl = fileContent.getDownloadUrl();
		System.out.println(downloadUrl);

		String gitUrl = fileContent.getGitUrl();
		System.out.println(gitUrl);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(fileContent.read()))) {
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				System.out.println(readLine);
			}
		}

	}
}
