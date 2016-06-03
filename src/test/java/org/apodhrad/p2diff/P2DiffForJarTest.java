package org.apodhrad.p2diff;

import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class P2DiffForJarTest {

	public static final String JAR_FILE_1 = "/com.example.p2bundle_0.0.1.v20161954-1250.jar";
	public static final String JAR_FILE_2 = "/com.example.p2bundle_0.0.2.v20161958-1251.jar";

	@Test
	public void testGeneratingDiffForDifferentJars() throws Exception {
		File jarFile1 = getResourceFile(JAR_FILE_1);
		File jarFile2 = getResourceFile(JAR_FILE_2);

		List<String> expectedLines = FileUtils.readLines(getResourceFile("/diff_jar.txt"));
		assertEquals(expectedLines, new P2DiffForJar(jarFile1, jarFile2).generateDiff());
	}
	
	@Test
	public void testGeneratingDiffForSameJars() throws Exception {
		File jarFile1 = getResourceFile(JAR_FILE_1);
		File jarFile2 = getResourceFile(JAR_FILE_1);

		assertTrue(new P2DiffForJar(jarFile1, jarFile2).generateDiff().isEmpty());
	}
}
