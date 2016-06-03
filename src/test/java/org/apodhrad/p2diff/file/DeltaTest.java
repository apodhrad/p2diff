package org.apodhrad.p2diff.file;

import org.junit.Test;

public class DeltaTest {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorWithNullObjects() {
		new Delta("dir1/test1.txt", null, null);
	}
}
