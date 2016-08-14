/********************************
 *	프로젝트 : Gagoyle
 *	패키지   : external.poi.word.converter
 *	작성일   : 2016. 1. 25.
 *	작성자   : KYJ
 *******************************/
package external.poi.word.converter;

import java.io.File;
import java.net.ConnectException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.kyj.fx.voeditor.visual.words.spec.auto.msword.core.CustomDocFormatRegistry;

/**
 * @author KYJ
 *
 */
public class ConverterSampleTest {

	/**
	 * @param args
	 * @throws ConnectException
	 */
	public static void main(String[] args) throws ConnectException {



//
		File inputFile = new File("C:\\Users\\KYJ\\Desktop\\convert\\memojava.doc");
		File outputFile = new File("C:\\Users\\KYJ\\Desktop\\convert\\memojav2.html");

		// connect to an OpenOffice.org instance running on port 8100
		OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
		connection.connect();

		// convert
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection, new CustomDocFormatRegistry());
		converter.convert(inputFile, outputFile);

		// close the connection
		connection.disconnect();

	}

}
