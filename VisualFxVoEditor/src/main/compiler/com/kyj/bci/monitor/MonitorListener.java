/********************************
 *	프로젝트 : system.monitor
 *	패키지   :  com.kyj.monitor
 *	작성일   :  2016. 2. 24.
 *	작성자   :  KYJ
 *******************************/
package com.kyj.bci.monitor;

/**
 * @author KYJ
 *
 */
public interface MonitorListener
{
	/********************************
	 * 패키지 : 작성일 : 2016. 2. 24. 작성자 : KYJ
	 *
	 * 어플리케이션이 새로 띄워지는경우
	 *
	 * @param model
	 *******************************/
	public void onApplicationLoaded(ApplicationModel model);

	/********************************
	 * 패키지 : 작성일 : 2016. 2. 24. 작성자 :
	 *
	 * KYJ 어플리케이션이 닫히는 경우
	 *
	 * @param model
	 *******************************/
	public void onApplicationTerminated(ApplicationModel model);
}
