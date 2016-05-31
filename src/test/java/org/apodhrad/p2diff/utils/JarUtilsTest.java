package org.apodhrad.p2diff.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apodhrad.p2diff.util.JarUtils;
import org.apodhrad.p2diff.util.ResourceUtils;
import org.junit.Test;

public class JarUtilsTest {

	@Test
	public void testFindingSourceJarFile() throws Exception {
		File jarFile = ResourceUtils.getResourceFile("/com.example.p2bundle_0.0.2.v20161958-1251.jar");
		File sourceJarFile = JarUtils.getSourceJarFile(jarFile);
		assertNotNull(sourceJarFile);
		assertEquals("com.example.p2bundle.source_0.0.2.v20161958-1251.jar", sourceJarFile.getName());
		assertTrue(sourceJarFile.exists());
	}
}
