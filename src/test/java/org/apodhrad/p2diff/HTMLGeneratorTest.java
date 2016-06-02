package org.apodhrad.p2diff;

import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class HTMLGeneratorTest {

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

		String expectedHtml = FileUtils.readFileToString(getResourceFile("/diff_report.html"));
		assertEquals(expectedHtml, FileUtils.readFileToString(new File(target, "diff-reports/index.html")));
	}
}
