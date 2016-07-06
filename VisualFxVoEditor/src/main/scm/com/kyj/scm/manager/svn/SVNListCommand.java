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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.scm.manager.core.commons.IListCommand;

/**
 * SVN의 LIST명령어를 수행한다.
 *
 * @author KYJ
 *
 */
@Deprecated
class SVNListCommand extends AbstractSVNCommand implements IListCommand<String, List<String>> {

	private static Logger LOGGER = LoggerFactory.getLogger(SVNListCommand.class);

	public SVNListCommand(Properties properties) {
		super(properties);
	}

	/*
	 * @inheritDoc
	 */
	@Override
	public List<String> list(String param) {
		return list("EUC-KR", null, param);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param encode
	 * @param param
	 * @return
	 */
	public List<String> list(String encode, String param) {
		return list(encode, null, param);
	}

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 4. 3.
	 * @param encode
	 * @param revision
	 * @param param
	 * @return
	 */
	public List<String> list(String encode, String revision, String param) {
		List<String> args = new ArrayList<String>();

		args.add(getSvnPath());

		args.add("--username");
		args.add(getUserId());
		args.add("--password");
		args.add(getUserPassword());

		args.add("list");

		args.add(param);

		try {

			LOGGER.debug("command line user's argument : list command , revision : {}, param : {}", revision, param);

			return SvnCommand.getInstance().exe(encode, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
