/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : simple
 *	작성일   : 2016. 9. 29.
 *	작성자   : KYJ
 *******************************/
package simple;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class LocalTimeTest {

	@Test
	public void simple() {

		DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.ENGLISH);

		LocalTime leetTime = LocalTime.parse("11:37 PM", germanFormatter);
		System.out.println(leetTime);

	}
}
