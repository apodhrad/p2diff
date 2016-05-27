package org.apodhrad.p2diff;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class DiffForFileTest {

	@Test
	public void testGenerating() throws Exception {
		DiffForFile dff = new DiffForFile(new File(getClass().getResource("/text1.txt").getFile()).getPath(),
				new File(getClass().getResource("/text2.txt").getFile()).getPath());
		FileUtils.writeStringToFile(new File(getClass().getResource("/expected.txt").getFile()), dff.generate());
		String expected = FileUtils.readFileToString(new File(getClass().getResource("/expected.txt").getFile()));

		Assert.assertEquals(expected, dff.generate());
	}
}
