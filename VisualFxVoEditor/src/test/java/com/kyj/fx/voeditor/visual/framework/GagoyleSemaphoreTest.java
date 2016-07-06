/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : com.kyj.fx.voeditor.visual.framework
 *	작성일   : 2016. 6. 22.
 *	작성자   : KYJ
 *******************************/
package com.kyj.fx.voeditor.visual.framework;

import java.util.function.Consumer;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.framework.lock.GagoyleSemaphore;

import javafx.util.Callback;

/**
 *
 * 임계영역 테스트 여부를 확인하기 위한 테스트
 *
 * @author KYJ
 *
 */
public class GagoyleSemaphoreTest {

	@Test
	public void simple() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {

				// 임계영역 객체 생성
				GagoyleSemaphore<Void> sema = new GagoyleSemaphore<>(2);

				// 실행코드 작성
				sema.setCallback(new Callback<Void, Void>() {

					@Override
					public Void call(Void param) {

						// 코드 실행 시간이라 가정함. [시작]
						for (int i = 0; i < 5; i++) {

							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							System.out.println(Thread.currentThread().getName());
						}
						// 코드 실행 시간이라 가정함. [끝]

						return null;
					}
				});

				// 실행영역의 작업이 종료되면 호출되는 로직.
				sema.setOnFinish(new Consumer<Void>() {

					@Override
					public void accept(Void t) {

						System.out.println(Thread.currentThread().getName() + " Finish.... ");

					}
				});

				// 임계영역을 테스트하기 위해 20개의 스레드를 생성해서 즉각으로 실행시킴.
				for (int i = 0; i < 20; i++) {
					new Thread(new Runnable() {

						@Override
						public void run() {

							// 임계영역 코드 실행.

							sema.start();

						}
					}).start();

				}

			}
		});

		try {

			// 테스트를 위한 스레드는 시작시키고 메인스레드는 10초간 대기한다.
			thread.start();
			Thread.currentThread().join(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
