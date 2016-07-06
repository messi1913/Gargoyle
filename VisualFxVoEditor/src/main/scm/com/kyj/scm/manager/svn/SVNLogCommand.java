/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager.command.svn
 *	작성일   : 2016. 3. 23.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.kyj.scm.manager.core.commons.ILogCommand;

/**
 * SVN의 리비젼 정보 조회
 *
 * @author KYJ
 *
 */
@Deprecated
class SVNLogCommand extends AbstractSVNCommand implements ILogCommand<String, List<String>> {

	public SVNLogCommand(Properties properties) {
		super(properties);
	}

	@Override
	public List<String> log(String param) {
		return log("EUC-KR", null, param);
	}

	public List<String> log(String encode, String param) {
		return log(encode, null, param);
	}

	public List<String> log(String encode, String revision, String param) {
		List<String> args = new ArrayList<String>();

		args.add(getSvnPath());
		// args.add("Subversion\\window\\bin\\svn.exe");

		args.add("--username");
		args.add(getUserId());
		args.add("--password");
		args.add(getUserPassword());

		args.add("log");

		args.add(param);

		try {
			return SvnCommand.getInstance().exe(encode, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
