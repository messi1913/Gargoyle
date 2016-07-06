/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.poi.word.diff
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package external.poi.word.diff;

import java.io.FileInputStream;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;

/**
 * @author KYJ
 *
 */
public class ReadDoc {

	/**
	 * @작성자 : KYJ
	 * @작성일 : 2016. 1. 25.
	 * @param args
	 */
	public static void main(String[] args) {
		String filesname = "(APP)(사양서)AprvSampleApp.docx";

		try {

			XWPFDocument doc = new XWPFDocument(new FileInputStream(filesname));

			doc.getBodyElements().forEach(b -> {

				BodyElementType elementType = b.getElementType();

				if (BodyElementType.TABLE.name().equals(elementType.name())) {
					XWPFTable table = (XWPFTable) b;
					System.out.println(table);
				} else if (BodyElementType.PARAGRAPH.name().equals(elementType.name())) {
					System.out.println(b);
				} else if (BodyElementType.CONTENTCONTROL.name().equals(elementType.name())) {
					System.out.println(b);
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
