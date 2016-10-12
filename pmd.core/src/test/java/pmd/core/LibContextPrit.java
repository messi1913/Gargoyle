/********************************
 *	프로젝트 : pmd.core
 *	패키지   : pmd.core
 *	작성일   : 2016. 10. 12.
 *	작성자   : KYJ
 *******************************/
package pmd.core;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * @author KYJ
 *
 */
public class LibContextPrit {

	@Test
	public void print() {

		File file = new File("lib");
		System.out.println(file.getAbsolutePath());
		if (file.exists()) {
			StringBuffer sb = new StringBuffer();
			sb.append("<dependency>\n");
			sb.append("	<groupId>%s</groupId>\n");
			sb.append("	<artifactId>%s</artifactId>\n");
			sb.append("	<version>%s</version>\n");
			sb.append("	<systemPath>${project.basedir}/lib/%s</systemPath> \n");
			sb.append("</dependency>\n");

			final String dependencyFormat = sb.toString();

			List<String> collect = Stream.of(file.listFiles()).map(f -> {

				String name = f.getName();
				int indexOf = name.indexOf(".jar");
				int versionIndex = -1;
				for (int i = indexOf; i >= 0; i--) {
					if (name.charAt(i) == '-') {
						versionIndex = i;
						break;
					}
				}

				if (versionIndex == -1) {
					System.err.println("error");
					return null;
				}

				String libName = name.substring(0, versionIndex);
				String version = name.substring(versionIndex + 1, indexOf);

				//				System.out.println(String.format("name ::: %s\nversion ::: %s", libName, version));

				return String.format(dependencyFormat, libName, libName, version, name);
			}).collect(Collectors.toList());

			collect.forEach(System.out::println);
		}

	}

}
