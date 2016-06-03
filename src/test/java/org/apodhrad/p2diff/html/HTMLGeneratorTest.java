package org.apodhrad.p2diff.html;

import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apodhrad.p2diff.file.Delta;
import org.apodhrad.p2diff.html.HTMLGenerator;
import org.junit.Before;
import org.junit.Test;

public class HTMLGeneratorTest {

	@Before
	public void deleteTargetDiffFolder() {
		FileUtils.deleteQuietly(new File("target/diff-reports"));
	}

	@Test
	public void testConvertingToHtml() throws Exception {
		List<String> diff = FileUtils.readLines(getResourceFile("/diff.txt"));
		List<String> expectedHtml = FileUtils.readLines(getResourceFile("/html_diff.txt"));

		assertEquals(expectedHtml, HTMLGenerator.convertToHTML(diff));
	}

	@Test
	public void testGeneratingDiffReport() throws Exception {
		File target = new File("target");

		List<String> diff = FileUtils.readLines(getResourceFile("/diff_jar.txt"));
		new HTMLGenerator().generateDiffReport(diff, target);

		String expectedHtml = FileUtils.readFileToString(getResourceFile("/report_diff.html"));
		assertEquals(expectedHtml, FileUtils.readFileToString(new File(target, "diff-reports/index.html")));
	}

	@Test
	public void testGeneratingDeltaReport() throws Exception {
		File target = new File("target");

		List<Delta> deltas = new ArrayList<Delta>();
		deltas.add(new Delta("test0.txt", new File("test0.txt"), new File("test0.txt")));
		deltas.add(new Delta("dir1/test1.txt", new File("test1.txt"), new File("test1.txt")));
		deltas.add(new Delta("dir2/test2.txt", new File("test2.txt"), null));
		deltas.add(new Delta("dir3/test3.txt", null, new File("test3.txt")));

		new HTMLGenerator().generateDiffReport2(deltas, target);

		String expectedHtml = FileUtils.readFileToString(getResourceFile("/report_delta.html"));
		assertEquals(expectedHtml, FileUtils.readFileToString(new File(target, "diff-reports/index.html")));
	}

}
