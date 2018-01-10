/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external
 *	작성일   : 2018. 1. 10.
 *	작성자   : KYJ
 *******************************/
package external;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.openxml4j.opc.ZipPackage;
import org.junit.Test;

import com.kyj.fx.voeditor.visual.util.ValueUtil;

/**
 * @author KYJ
 *
 */
public class RaxReaderTest {

	@Test
	public void readTest() throws InvalidFormatException, IOException {

		InputStream in = RaxReaderTest.class.getResource("kyj.rax").openStream();

		// ZipPackage zipPackage = new
		// org.apache.poi.openxml4j.opc.ZipPackage();
		// zipPackage.open(file, access)
		// zipPackage.op

		ZipPackage pkg = (ZipPackage) OPCPackage.open(in);
		Enumeration<? extends ZipEntry> entries = pkg.getZipArchive().getEntries();

		while (entries.hasMoreElements()) {
			ZipEntry nextElement = entries.nextElement();
			System.out.println(nextElement.getName());
//			InputStream stream = pkg.getZipArchive().getInputStream(nextElement);
//			
//			if (stream != null) {
//				System.out.println("########################");
//				System.out.println(ValueUtil.toString(stream));
//			}
			

		}
		//
		//
		// PackageRelationship corePropsRel =
		// pkg.getRelationshipsByType(PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);
		//
		// if (corePropsRel == null)
		// return;
		//
		// ZipEntry zipEntry = new
		// ZipEntry(corePropsRel.getTargetURI().getPath());

	}

}
