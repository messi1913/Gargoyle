/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework.lock
 *	작성일   : 2016. 6. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework.lock;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.Callback;
import kyj.Fx.dao.wizard.core.util.ValueUtil;

/**
 *
 *
 * 세마포어 처리 클래스.
 *
 *
 * 임계영역처리담당.
 *
 * @author KYJ
 *
 */
public class GagoyleSemaphore<R> extends Semaphore {

	private static final Logger LOGGER = LoggerFactory.getLogger(GagoyleSemaphore.class);
	/**
	 * @최초생성일 2016. 6. 22.
	 */
	private static final long serialVersionUID = 1080109119859993223L;

	private Callback<Void, R> callback;

	private Consumer<R> onFinish;

	public GagoyleSemaphore(final int permits) {
		super(permits);
	}

	public GagoyleSemaphore(Callback<Void, R> callback, final int permits) {
		super(permits);
		this.callback = callback;

	}

	/**
	 * 세마포어 실행코드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 * @throws InterruptedException
	 */
	private void use() throws InterruptedException {
		acquire(); // 세마포어 리소스 확보

		R result = null;
		try {
			LOGGER.debug("Thread 실행전 남은  permits: " + this.availablePermits());
			result = doUse();
			LOGGER.debug("Thread 실행후 남은  permits: " + this.availablePermits());
		} finally {
			release(); // 세마포어 리소스 해제
			if (onFinish != null)
				onFinish.accept(result);
			LOGGER.debug("Thread 종료 후 남은  permits: " + this.availablePermits());
		}
	}

	/**
	 * 실행메소드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 */
	public final void start() {
		start(error -> {
			LOGGER.error(ValueUtil.toString(error));
		});
	}

	/**
	 * 실행메소드
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 * @param errorCallback
	 */
	public final void start(Consumer<Exception> errorCallback) {
		try {
			use();
		} catch (InterruptedException e) {
			errorCallback.accept(e);
		}
	}

	/**
	 * @return the callback
	 */
	public final Callback<Void, R> getCallback() {
		return callback;
	}

	/**
	 * @param callback
	 *            the callback to set
	 */
	public final void setCallback(Callback<Void, R> callback) {
		this.callback = callback;
	}

	/**
	 * @return the onFinish
	 */
	public final Consumer<R> getOnFinish() {
		return onFinish;
	}

	/**
	 * @param onFinish
	 *            the onFinish to set
	 */
	public final void setOnFinish(Consumer<R> onFinish) {
		this.onFinish = onFinish;
	}

	/**
	 * 콜백 메소드 실행.
	 *
	 * @작성자 : KYJ
	 * @작성일 : 2016. 6. 22.
	 * @throws InterruptedException
	 */
	protected R doUse() throws InterruptedException {
		return callback.call(null);
	}

}
