/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.bci.monitor
 *	작성일   : 2017. 7. 17.
 *	프로젝트 : OPERA 
 *	작성자   : KYJ
 *******************************/
package com.kyj.bci.monitor;

import org.json.simple.JSONObject;

/**
 * @author KYJ
 *
 */
public interface SnapShotListener {

	public void onListen(JSONObject obj);

}
