/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.velocity
 *	작성일   : 2016. 9. 20.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.velocity;

import java.util.Date;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.kyj.fx.voeditor.visual.util.DateUtil;

/**
 * 날짜 데이터가 기본적으로 입력되는 컨텍스트 플러그인
 *
 * 기본 날짜포멧 : YYYYMMDDHHMMSS
 *
 * @author KYJ
 *
 */
public class ExtensionDateFormatVelocityContext extends VelocityContext {

	private String dateFormat = DateUtil.SYSTEM_DATEFORMAT_YYYYMMDDHHMMSS;

	public ExtensionDateFormatVelocityContext() {
		super();
	}

	public ExtensionDateFormatVelocityContext(String dateFormat) {
		super();
		this.dateFormat = dateFormat;
	}

	public ExtensionDateFormatVelocityContext(Context innerContext) {
		super(innerContext);
	}

	public ExtensionDateFormatVelocityContext(@SuppressWarnings("rawtypes") Map context, Context innerContext) {
		super(context, innerContext);
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.VelocityContext#internalGet(java.lang.String)
	 */
	@Override
	public Object internalGet(String key) {

		if ("date".equals(key))
			return DateUtil.getDateAsStr(new Date(), this.dateFormat);

		return super.internalGet(key);
	}
}
