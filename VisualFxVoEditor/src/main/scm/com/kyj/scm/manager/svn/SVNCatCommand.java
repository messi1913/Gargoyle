/********************************
 *	프로젝트 : ScmManager
 *	패키지   : kyj.Fx.scm.manager.command.svn.concreate
 *	작성일   : 2016. 3. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.scm.manager.svn;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.kyj.scm.manager.core.commons.ICatCommand;

/**
 * SVN의 CAT명령어를 수행한다.
 *
 * @author KYJ
 *
 */
@Deprecated
class SVNCatCommand extends AbstractSVNCommand implements ICatCommand<String, List<String>> {

	public SVNCatCommand(Properties properties) {
		super(properties);
	}

	@Override
	public List<String> cat(String param) {
		return cat("EUC-KR", null, param);
	}

	public List<String> cat(String revision, String param) {
		return cat("EUC-KR", revision, param);
	}

	// public List<String> cat(String encode, String param) {
	// return cat(encode, null, param);
	// }

	public List<String> cat(String encode, String revision, String param) {
		List<String> args = new ArrayList<String>();
		args.add(getSvnPath());
		args.add("--username");
		args.add(getUserId());
		args.add("--password");
		args.add(getUserPassword());

		args.add("cat");

		if (revision != null) {
			args.add("-r");
			args.add(revision);
		}

		args.add(param);

		try {
			return SvnCommand.getInstance().exe(encode, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
