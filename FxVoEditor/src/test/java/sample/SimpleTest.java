package sample;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kyj.fx.voeditor.core.VoEditor;
import com.kyj.fx.voeditor.core.model.meta.ClassMeta;
import com.kyj.fx.voeditor.core.model.meta.FieldMeta;

/**
 * KYJ
 * 2015. 10. 11.
 */

/**
 * @author KYJ
 *
 */
public class SimpleTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

	@Test
	public void test() throws Exception {
		FieldMeta fieldMeta = new FieldMeta((Modifier.PRIVATE), "sample", String.class);
		fieldMeta.setPrimarykey(true);
		fieldMeta.setAlias("키속성");
		FieldMeta fieldMeta2 = new FieldMeta((Modifier.PRIVATE), "sample2", String.class);
		FieldMeta fieldMeta3 = new FieldMeta((Modifier.PRIVATE), "check", boolean.class);
		FieldMeta fieldMeta4 = new FieldMeta((Modifier.PRIVATE), "count", int.class);
		FieldMeta fieldMeta5 = new FieldMeta((Modifier.PRIVATE), "name", StringProperty.class, SimpleStringProperty.class);
		FieldMeta fieldMeta6 = new FieldMeta((Modifier.PRIVATE), "name2", StringProperty.class, SimpleStringProperty.class);
		/*
		 * , FxVo.class, new Class<?>[] { IExtractClass .class, IExtractField
		 * .class }
		 */
		ClassMeta classMeta = new ClassMeta("com.sample", "Simple");
		VoEditor voEditor = new VoEditor(classMeta, fieldMeta, fieldMeta2, fieldMeta3, fieldMeta4, fieldMeta5, fieldMeta6);
		voEditor.build();

		String text = voEditor.toText();
		LOGGER.debug(text);
	}

	@Test(expected = java.lang.ClassNotFoundException.class)
	public void vitualPathTest() throws Exception {

		String bathPath = "C:\\Users\\KYJ\\JAVA_FX\\workspace\\rotto.analysis\\";
		String classLocationPath = "target\\classes";// \\RottoAnalysis\\rotto\\analysis\\algorisms\\mailing\\";

		URL url = new File(bathPath + classLocationPath).toURI().toURL();
		System.out.println(url);
		URL classURL = new URL(url + "!/");
		URLClassLoader classLoader = new URLClassLoader(new URL[]{classURL});

		Class clazz1 = classLoader.loadClass("RottoAnalysis.rotto.analysis.algorisms.mailing.SendEachMail");
		classLoader.close();

		// Class.forName("RottoAnalysis.rotto.analysis.algorisms.mailing.SendEachMail");
	}
}
