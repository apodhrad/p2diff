package org.apodhrad.p2diff.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apodhrad.p2diff.util.JarUtils;
import org.apodhrad.p2diff.util.ResourceUtils;
import org.junit.Test;

public class JarUtilsTest {

	@Test
	public void testFindingSourceJarFileForSimpleBundle() throws Exception {
		File bundle = new File("com.example.bundle.jar");
		File sourceBundle = new File("com.example.bundle.source.jar");
		File sourceJarFile = JarUtils.getSourceJarFile(bundle);
		assertNotNull(sourceJarFile);
		assertEquals(sourceBundle, sourceJarFile);
	}

	@Test
	public void testFindingSourceJarFileForP2Bundle() throws Exception {
		File jarFile = ResourceUtils.getResourceFile("/com.example.p2bundle_0.0.2.v20161958-1251.jar");
		File sourceJarFile = JarUtils.getSourceJarFile(jarFile);
		assertNotNull(sourceJarFile);
		assertEquals("com.example.p2bundle.source_0.0.2.v20161958-1251.jar", sourceJarFile.getName());
		assertTrue(sourceJarFile.exists());
	}

	@Test
	public void testGettingSimpleBundleName() {
		assertEquals("com.example.bundle", JarUtils.getBundleName(new File("com.example.bundle.jar")));
	}

	@Test
	public void testGettingP2BundleName() {
		assertEquals("com.example.p2bundle", JarUtils.getBundleName(new File("com.example.p2bundle_1.2.3.jar")));
	}

	@Test
	public void testGettingSimpleBundleVersion() {
		assertNull(JarUtils.getBundleVersion(new File("com.example.bundle.jar")));
	}

	@Test
	public void testGettingP2BundleVersion() {
		assertEquals("1.2.3", JarUtils.getBundleVersion(new File("com.example.p2bundle_1.2.3.jar")));
	}
}
