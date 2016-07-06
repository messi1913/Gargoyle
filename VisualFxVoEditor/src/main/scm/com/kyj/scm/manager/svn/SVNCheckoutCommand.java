/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.kyj.scm.manager.core.commons.ICheckoutCommand;
import com.kyj.scm.manager.core.commons.SCMListener;

/**
 * SVN의 CHECKOUT 명령을 수행한다
 *
 * @author KYJ
 *
 */
@Deprecated
class SVNCheckoutCommand extends AbstractSVNCommand implements ICheckoutCommand<String, List<String>>, SCMListener {

	public SVNCheckoutCommand(Properties properties) {
		super(properties);

		// 완료까지 오랜 시간이 소요되는 경우가 있기때문에 리스너를 등록하여 메세지를 수신한다.
		// SvnCommand.getInstance().registListener(this);
	}

	@Override
	public List<String> checkout(String param) {
		return checkout("EUC-KR", null, "", param);
	}

	public List<String> checkout(File outDir, String param) {
		return checkout("EUC-KR", null, outDir.getAbsolutePath(), param);
	}

	public List<String> checkout(String revision, String param) {
		return checkout("EUC-KR", revision, "", param);
	}

	public List<String> checkout(String revision, String outDir, String param) {
		return checkout("EUC-KR", revision, outDir, param);
	}

	@Override
	public List<String> checkout(String param, File outDir) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> checkout(String encode, String revision, String outDir, String param) {
		List<String> args = new ArrayList<String>();
		args.add(getSvnPath());
		args.add("--username");
		args.add(getUserId());
		args.add("--password");
		args.add(getUserPassword());

		args.add("checkout");

		if (revision != null) {
			args.add("-r");
			args.add(revision);
		}

		args.add(param);

		args.add(outDir);

		try {
			return SvnCommand.getInstance().exe(encode, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void listen(String message) {

		System.out.println("listener ::: " + message);
	}

}
