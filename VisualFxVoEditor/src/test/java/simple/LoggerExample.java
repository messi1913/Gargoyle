package simple;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : 
 *	작성일   : 2015. 10. 23.
 *	프로젝트 : SOS 미어캣 프로젝트
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
public class LoggerExample {
	private static Logger logger = LoggerFactory.getLogger(LoggerExample.class);

	@Test
	public void log() {
		logger.debug("dddd");
		logger.info("info");
		logger.warn("warn");
		logger.error("error");

	}
}
