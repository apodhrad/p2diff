package org.apodhrad.p2diff.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apodhrad.p2diff.file.Delta;
import org.apodhrad.p2diff.file.Folder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class FolderTest {

	private static File temp1;
	private static File temp2;

	@BeforeClass
	public static void prepareTestFolder() throws IOException {
		temp1 = Files.createTempDir();
		temp1.mkdir();

		new File(temp1, "dir1").mkdirs();
		new File(temp1, "dir2/dir2a").mkdirs();
		new File(temp1, "dir2/dir2b").mkdirs();

		new File(temp1, "test0.txt").createNewFile();
		new File(temp1, "dir1/test1.txt").createNewFile();
		new File(temp1, "dir2/test2.txt").createNewFile();
		new File(temp1, "dir2/dir2b/test2b.txt").createNewFile();

		temp2 = Files.createTempDir();
		temp2.mkdir();

		new File(temp2, "dir1").mkdirs();
		new File(temp2, "dir2/dir2a").mkdirs();
		new File(temp2, "dir2/dir2b").mkdirs();

		new File(temp2, "test0.txt").createNewFile();
		new File(temp2, "dir1/test1.txt").createNewFile();
		new File(temp2, "dir2/test2.txt").createNewFile();
		new File(temp2, "dir2/dir2a/test2a.txt").createNewFile();
	}

	@AfterClass
	public static void deleteTestFolder() {
		FileUtils.deleteQuietly(temp1);
		FileUtils.deleteQuietly(temp2);
	}

	@Test
	public void testGettingExistingFile() {
		assertEquals(new File(temp1, "dir1/test1.txt"), new Folder(temp1).getFile("dir1/test1.txt"));
	}

	@Test
	public void testGettingNonexistingFile() {
		assertNull(new Folder(temp1).getFile("dir1/test2.txt"));
	}

	@Test
	public void testListingRelativePaths() {
		Collection<String> relativePaths = new Folder(temp1).listRelativePaths();
		assertTrue(relativePaths.contains("test0.txt"));
		assertTrue(relativePaths.contains("dir1/test1.txt"));
		assertTrue(relativePaths.contains("dir2/test2.txt"));
		assertTrue(relativePaths.contains("dir2/dir2b/test2b.txt"));
		assertEquals(4, relativePaths.size());
	}

	@Test
	public void testCumputingDeltas() {
		Collection<Delta> deltas = new Folder(temp1).computeDeltas(new Folder(temp2));
		assertTrue(deltas.contains(delta("test0.txt", temp1, temp2)));
		assertTrue(deltas.contains(delta("dir1/test1.txt", temp1, temp2)));
		assertTrue(deltas.contains(delta("dir2/test2.txt", temp1, temp2)));
		assertTrue(deltas.contains(delta("dir2/dir2b/test2b.txt", temp1, null)));
		assertTrue(deltas.contains(delta("dir2/dir2a/test2a.txt", null, temp2)));
		assertEquals(5, deltas.size());
	}

	private static Delta delta(String path, File temp1, File temp2) {
		if (temp1 == null) {
			return new Delta(path, null, new File(temp2, path));
		}
		if (temp2 == null) {
			return new Delta(path, new File(temp1, path), null);
		}
		return new Delta(path, new File(temp1, path), new File(temp2, path));
	}

}
