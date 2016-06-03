package org.apodhrad.p2diff.file;

import java.io.File;

import org.junit.Test;

public class DeltaTest {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullObjects() {
		new Delta("dir1/test1.txt", null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullPath() {
		new Delta(null, new File("dir1/test1.txt"), new File("dir2/test1.txt"));
	}
}
