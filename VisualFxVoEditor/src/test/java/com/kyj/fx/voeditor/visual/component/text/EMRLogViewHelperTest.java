package com.kyj.fx.voeditor.visual.component.text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.kyj.fx.voeditor.visual.framework.logview.helper.core.EMRLogViewHelper;

public class EMRLogViewHelperTest {

	private EMRLogViewHelper h;

	@Before
	public void init() {
		h = new EMRLogViewHelper();
	}

	@Test
	public void test() throws IOException {

		// Input Location.
		List<String> readLines = Files.readLines(new File("EMRService Location "), StandardCharsets.UTF_8);

		int idx = 1;
		for (int i = 0; i < readLines.size(); i++) {
			String text = readLines.get(i);
			h.setText(text);
			boolean read = h.read();
			if (read) {
				System.out.println(String.format("[%d] %s", idx, h.getFxValue()));
				idx++;
			}
		}
	}

}
