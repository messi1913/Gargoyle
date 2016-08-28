package simple;
import java.util.StringTokenizer;

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

	}

}
