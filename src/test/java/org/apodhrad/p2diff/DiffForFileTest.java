package org.apodhrad.p2diff;

import static org.apodhrad.p2diff.DiffForJarTest.JAR_FILE_1;
import static org.apodhrad.p2diff.DiffForJarTest.JAR_FILE_2;
import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DiffForFileTest {

	@Test
	public void testGeneratingDiffForSameFiles() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile("/text1.txt"), getResourceFile("/text1.txt"));
		List<String> diffLines = dff.generateDiff();
		assertTrue(diffLines.isEmpty());
	}

	@Test
	public void testGeneratingDiffForDifferentFiles() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile("/text1.txt"), getResourceFile("/text2.txt"));
		dff.setBaseDir(getResourceFile("/"));

		List<String> expectedLines = FileUtils.readLines(getResourceFile("/diff.txt"));
		assertEquals(expectedLines, dff.generateDiff());
	}

	@Test
	public void testGeneratingDiffForNewFile() throws Exception {
		DiffForFile dff = new DiffForFile(null, getResourceFile("/text2.txt"));
		dff.setBaseDir(getResourceFile("/"));

		List<String> expectedLines = FileUtils.readLines(getResourceFile("/diff_new.txt"));
		assertEquals(expectedLines, dff.generateDiff());
	}

	@Test
	public void testGeneratingDiffForDeletedFile() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile("/text1.txt"), null);
		dff.setBaseDir(getResourceFile("/"));

		List<String> expectedLines = FileUtils.readLines(getResourceFile("/diff_deleted.txt"));
		assertEquals(expectedLines, dff.generateDiff());
	}

	@Test
	public void testGeneratingDiffForNullObjects() throws Exception {
		try {
			new DiffForFile(null, null);
		} catch (IllegalArgumentException iae) {
			return;
		}
		fail("Expected IllegalArgumentException when setting two NULL objects");
	}

	@Test
	public void testGeneratingDiffForDifferentBinaryFiles() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile(JAR_FILE_1), getResourceFile(JAR_FILE_2));
		List<String> diffLines = dff.generateDiff();
		// assertTrue(diffLines.contains(""));
		assertEquals(1, diffLines.size());
	}

	@Test
	public void testOfSettingBaseDir() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile("/text1.txt"), getResourceFile("/text2.txt"));
		dff.setBaseDir(getResourceFile("/"));

		assertEquals(getResourceFile("/").getPath(), dff.getBaseDir().getPath());
	}

	@Test
	public void testOfSettingWrongBaseDir() throws Exception {
		DiffForFile dff = new DiffForFile(getResourceFile("/text1.txt"), getResourceFile("/text2.txt"));
		Exception exception = null;
		try {
			dff.setBaseDir(new File("foo"));
		} catch (Exception e) {
			exception = e;
		}
		if (!(exception != null && exception instanceof IllegalArgumentException)) {
			fail("Expected IllegalArgumentException when setting non-existing base dir");
		}

		exception = null;
		try {
			dff.setBaseDir(getResourceFile("/text1.txt"));
		} catch (Exception e) {
			exception = e;
		}
		if (!(exception != null && exception instanceof IllegalArgumentException)) {
			fail("Expected IllegalArgumentException when setting a file as a base dir");
		}
	}
}
