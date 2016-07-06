/********************************
 *	프로젝트 : VisualFxVoEditor
 *	패키지   : external.diff
 *	작성일   : 2016. 1. 26.
 *	작성자   : KYJ
 *******************************/
package external.diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;

/**
 * @author KYJ
 *
 */
public class DiffPathText {

	@Test
	public void diffPathTest() throws IOException, URISyntaxException, PatchFailedException {
		URL test1 = ClassLoader.getSystemClassLoader().getResource("compare/Test1");
		URL test2 = ClassLoader.getSystemClassLoader().getResource("compare/Test2");
		List<String> test1List = Files.readAllLines(new File(test1.toURI()).toPath());
		List<String> test2List = Files.readAllLines(new File(test2.toURI()).toPath());
		Patch diff = DiffUtils.diff(test1List, test2List);
		List<Delta> deltas = diff.getDeltas();
		deltas.stream().forEach(d -> {
			Chunk original = d.getOriginal();
			List<?> lines = original.getLines();
			lines.stream().forEach(System.out::println);
			// System.out.println(original);
		});
		// List<?> patch = DiffUtils.patch(test1List, diff);
		// patch.stream().forEach(System.out::println);
		//
		// List<?> unpatch = DiffUtils.unpatch(test2List, diff);
		// unpatch.stream().forEach(System.out::println);
	}

	@Test
	public void patchTest() throws IOException, URISyntaxException, PatchFailedException {
		URL test1 = ClassLoader.getSystemClassLoader().getResource("compare/Test1");
		URL test2 = ClassLoader.getSystemClassLoader().getResource("compare/Test2");
		// 원본데이터 리스트
		List<String> test1List = Files.readAllLines(new File(test1.toURI()).toPath());
		List<String> test2List = Files.readAllLines(new File(test2.toURI()).toPath());

		List<String> tmpOrigin = new LinkedList<String>();
		test1List.forEach(str -> {
			tmpOrigin.add(str);
		});
		List<String> tmpRevice = new LinkedList<String>();
		test2List.forEach(str -> {
			tmpRevice.add(str);
		});

		Patch diff = DiffUtils.diff(test1List, test2List);
		System.out.println(diff.getDeltas());

		List<Delta> deltas = diff.getDeltas();
		Collections.reverse(deltas);

		System.out.println("before");
		System.out.println(tmpOrigin);

		// 공백 작업pos
		deltas.forEach(d -> {

			Chunk original = d.getOriginal();
			Chunk revised = d.getRevised();

			int position = original.getPosition();
			switch (d.getType()) {
			case INSERT: {
				List<String> lines = (List<String>) original.getLines();
				List<String> lines2 = (List<String>) revised.getLines();
				tmpOrigin.addAll(position, lines2);
				tmpRevice.addAll(position, lines);
			}
				break;
			}

		});
		System.out.println("origin");
		System.out.println(tmpOrigin);
		System.out.println("revice");
		System.out.println(tmpRevice);
		// tmpOrigin.forEach(System.out::println);

	}

	List<String> gen(String text, long count) {
		List<String> linkedList = new LinkedList<String>();
		for (int i = 0; i < count; i++)
			linkedList.add(text);
		return linkedList;
	}

	@Test
	public void diffPath() throws IOException, URISyntaxException, PatchFailedException {
		URL test1 = ClassLoader.getSystemClassLoader().getResource("compare/Test1");
		URL test2 = ClassLoader.getSystemClassLoader().getResource("compare/Test2");
		List<String> test1List = Files.readAllLines(new File(test1.toURI()).toPath());
		List<String> test2List = Files.readAllLines(new File(test2.toURI()).toPath());
		Patch diff = DiffUtils.diff(test1List, test2List);

		List<Delta> deltas = diff.getDeltas();

		deltas.stream().forEach(d -> {

			Chunk original = d.getOriginal();

			int position = original.getPosition();

			List<String> lines = (List<String>) original.getLines();
			for (String str : lines) {
				switch (d.getType()) {
				case INSERT:
					test1List.set(position++, "+".concat(str));
					break;
				case DELETE:
					test1List.set(position++, "-".concat(str));
					break;
				case CHANGE:
					test1List.set(position++, "*".concat(str));
					break;
				}
			}

			Chunk revised = d.getRevised();
			position = revised.getPosition();
			lines = (List<String>) revised.getLines();
			// for (String str : lines) {
			// switch (d.getType()) {
			// case INSERT:
			// test1List.add(position++, "\n");
			// position += revised.getPosition();
			// break;
			// // case DELETE:
			// // test1List.set(position++, "-".concat(str));
			// // break;
			// // case CHANGE:
			// // test1List.set(position++, "*".concat(str));
			// // break;
			// }
			// }
		});

		test1List.stream().forEach(System.out::println);
		// System.out.println(original);
		// List<?> patch = DiffUtils.patch(test1List, diff);
		// patch.stream().forEach(System.out::println);
		//
		// List<?> unpatch = DiffUtils.unpatch(test2List, diff);
		// unpatch.stream().forEach(System.out::println);

	}

}
