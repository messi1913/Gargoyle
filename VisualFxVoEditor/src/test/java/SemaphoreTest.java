/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   :
 *	작성일   : 2016. 6. 22.
 *	작성자   : KYJ
 *******************************/

/**
 * @author KYJ
 *
 */
import java.util.Random;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {

	private static final Random rd = new Random(10000);

	static class Log {
		public static void debug(String strMessage) {
			System.out.println(Thread.currentThread().getName() + " : " + strMessage);
		}
	}

	class SemaphoreResource extends Semaphore {

		private static final long serialVersionUID = 1L;

		public SemaphoreResource(final int permits) {
			super(permits);
		}

		public void use() throws InterruptedException {

			acquire(); // 세마포어 리소스 확보

			try {
				Log.debug("Thread 실행전 남은  permits: " + this.availablePermits());
				doUse();
				Log.debug("Thread 실행후 남은  permits: " + this.availablePermits());
			} finally {
				release(); // 세마포어 리소스 해제
				Log.debug("Thread 종료 후 남은  permits: " + this.availablePermits());
			}
		}

		protected void doUse() throws InterruptedException {

			// 임의의 프로그램을 실행하는데 거리는 가상의 시간
			int sleepTime = rd.nextInt(500);
			Thread.sleep(sleepTime); // 런타임 시간 설정
			Log.debug(" Thread 실행..................." + sleepTime);

			/** something logic **/

		}

	}

	class MyThread extends Thread {

		private final SemaphoreResource resource;

		public MyThread(String threadName, SemaphoreResource resource) {
			this.resource = resource;
			this.setName(threadName);
		}

		@Override
		public void run() {
			try {
				resource.use();
			} catch (InterruptedException e) {
			} finally {
			}
		}

	}

	public static void main(String... s) {

		System.out.println("Test Start...");
		SemaphoreResource resource = new SemaphoreTest().new SemaphoreResource(2);

		for (int i = 0; i < 20; i++) {
			new SemaphoreTest().new MyThread("Thread-" + i, resource).start();
		}

	}

}