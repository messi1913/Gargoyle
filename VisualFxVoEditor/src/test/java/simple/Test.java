package simple;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Test {
	public static void main(String[] args) {
		new Test().testStringTokenizer();

	}

	private void testStringTokenizer() {

//		StringTokenizer st1 = new StringTokenizer("a b c d");
//		System.out.print(st1.countTokens());
//		StringTokenizer st3 = new StringTokenizer("a,,b,,c,,d", ",");
//		System.out.print(", " + st3.countTokens());
		StringTokenizer st4 = new StringTokenizer("a, |b,|c,d", ",|");

		System.out.println(", " + st4.countTokens());

		ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
		concurrentLinkedQueue.add("1");
		concurrentLinkedQueue.add("2");
		concurrentLinkedQueue.add("3");



		System.out.println(concurrentLinkedQueue.poll());
		System.out.println(concurrentLinkedQueue.poll());
		System.out.println(concurrentLinkedQueue.poll());
		System.out.println(concurrentLinkedQueue.size());
	}

}
