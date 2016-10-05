/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : classloader
 *	작성일   : 2016. 10. 5.
 *	작성자   : KYJ
 *******************************/
package classloader;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.junit.Test;

import com.kyj.fx.voeditor.visual.component.config.item.node.IRunableItem;

import net.sourceforge.pmd.lang.Language;

/**
 * @author KYJ
 *
 */
public class ServiceLoaderTest {

	@Test
	public void test() {
		{
			ServiceLoader<Language> languageLoader = ServiceLoader.load(Language.class);
			Iterator<Language> iterator = languageLoader.iterator();
			while (iterator.hasNext()) {
				try {
					Language language = iterator.next();
					System.out.println(language);
				} catch (UnsupportedClassVersionError e) {
					// Some languages require java8 and are therefore only available
					// if java8 or later is used as runtime.
					System.err.println("Ignoring language for PMD: " + e.toString());
				}
			}
		}
		{
			ServiceLoader<IRunableItem> languageLoader = ServiceLoader.load(IRunableItem.class, IRunableItem.class.getClassLoader());
			Iterator<IRunableItem> iterator = languageLoader.iterator();
			while (iterator.hasNext()) {
				System.out.println("Z");
				System.out.println(iterator.next());
			}
		}

	}
}
