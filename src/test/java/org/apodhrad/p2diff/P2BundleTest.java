package org.apodhrad.p2diff;

import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apodhrad.p2diff.util.JarUtils;
import org.junit.Before;
import org.junit.Test;

public class P2BundleTest {

	private static final String JAR_FILE_1 = "/com.example.p2bundle_0.0.1.v20161954-1250.jar";
	private static final String JAR_FILE_2 = "/com.example.p2bundle_0.0.2.v20161958-1251.jar";
	private static final String JAR_DIR_1 = "/com.example.p2bundle_0.0.1.v20161954-1250";
	private static final String JAR_DIR_2 = "/com.example.p2bundle_0.0.2.v20161958-1251";

	@Before
	public void deleteExtractedDirs() {
		try {
			FileUtils.deleteQuietly(getResourceFile(JAR_DIR_1));
			FileUtils.deleteQuietly(getResourceFile(JAR_DIR_2));
		} catch (Exception e) {
			// that's ok, don't botter with that
		}
	}

	@Test
	public void testListingFilesOfOriginalJarFile() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_1));
		Collection<File> files = diffJar.listFiles();
		File dir = getResourceFile(JAR_DIR_1);
		assertTrue(files.contains(new File(dir, "META-INF/MANIFEST.MF")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/App.class")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/App.java")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/Hello.class")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/Hello.java")));
		assertEquals(5, files.size());
	}

	@Test
	public void testListingFilesOfRevisedJarFile() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_2));
		Collection<File> files = diffJar.listFiles();
		File dir = getResourceFile(JAR_DIR_2);
		assertTrue(files.contains(new File(dir, "META-INF/MANIFEST.MF")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/Hello.class")));
		assertTrue(files.contains(new File(dir, "com/example/p2bundle/utils/HelloUtils.class")));
		assertEquals(3, files.size());
	}

	@Test
	public void testListingRelativePaths() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_2));
		Collection<String> relativePaths = diffJar.listRelativePaths();
		assertTrue(relativePaths.contains("META-INF/MANIFEST.MF"));
		assertTrue(relativePaths.contains("com/example/p2bundle/Hello.class"));
		assertTrue(relativePaths.contains("com/example/p2bundle/utils/HelloUtils.class"));
		assertEquals(3, relativePaths.size());
	}

	@Test
	public void testGettingFileWithCorrectPath() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_2));
		assertNotNull(diffJar.getFile("META-INF/MANIFEST.MF"));
		assertTrue(diffJar.getFile("META-INF/MANIFEST.MF").exists());
		assertNotNull(diffJar.getFile("com/example/p2bundle/Hello.class"));
		assertTrue(diffJar.getFile("com/example/p2bundle/Hello.class").exists());
		assertNotNull(diffJar.getFile("com/example/p2bundle/utils/HelloUtils.class"));
		assertTrue(diffJar.getFile("com/example/p2bundle/utils/HelloUtils.class").exists());
	}

	@Test
	public void testGettingFileWithIncorrectPath() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_2));
		assertNull(diffJar.getFile("META-INF/MANIFEST.M"));
	}
	@Test
	public void testFindingInternalSourceFile() throws Exception {
		File jarFile = getResourceFile(JAR_FILE_1);
		File extraxtedJarFile = JarUtils.extractJarFile(jarFile);

		P2Bundle diffJar = new P2Bundle(jarFile);
		File classFile = new File(extraxtedJarFile, "com/example/p2bundle/Hello.class");
		File sourceFile = diffJar.getSourceFile(classFile);
		assertNotNull(sourceFile);
	}

	@Test
	public void testFindingExternalSourceFile() throws Exception {
		File jarFile = getResourceFile(JAR_FILE_2);
		File extraxtedJarFile = JarUtils.extractJarFile(jarFile);

		P2Bundle diffJar = new P2Bundle(jarFile);
		File classFile = new File(extraxtedJarFile, "com/example/p2bundle/Hello.class");
		File sourceFile = diffJar.getSourceFile(classFile);
		assertNotNull(sourceFile);
	}

	@Test
	public void testFindingNonExistingSourceFile() throws Exception {
		File jarFile = getResourceFile(JAR_FILE_2);
		File extraxtedJarFile = JarUtils.extractJarFile(jarFile);

		P2Bundle diffJar = new P2Bundle(jarFile);
		File classFile = new File(extraxtedJarFile, "com/example/p2bundle/App.class");
		File sourceFile = diffJar.getSourceFile(classFile);
		assertNull(sourceFile);
	}

	@Test
	public void testFindingNonJavaSourceFile() throws Exception {
		File jarFile = getResourceFile(JAR_FILE_1);
		File extraxtedJarFile = JarUtils.extractJarFile(jarFile);

		P2Bundle diffJar = new P2Bundle(jarFile);
		File classFile = new File(extraxtedJarFile, "com/example/p2bundle/Hello.java");
		try {
			diffJar.getSourceFile(classFile);
		} catch (IllegalArgumentException iae) {
			return;
		}
		fail("Expected IllegalAccessException");
	}

	@Test
	public void testComputingCRC32() throws Exception {
		P2Bundle diffJar = new P2Bundle(getResourceFile(JAR_FILE_1));
		assertEquals(927245408l, diffJar.computeCRC32());
	}
}
