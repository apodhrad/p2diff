package org.apodhrad.p2diff;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class DiffForJarTest {

	@Test
	public void generatingTest() throws Exception {
		File target = new File("target/dfjtest.html");

		DiffForJar dfj = new DiffForJar(new File(getClass().getResource("/small_package.jar").getFile()).getPath(),
				new File(getClass().getResource("/small_package2.jar").getFile()).getPath(), target);

		String expected = FileUtils
				.readFileToString(new File(getClass().getResource("/dfjtest_expected.html").getFile()));
		String actual = dfj.generateHTML("inner_layout");
		FileUtils.writeStringToFile(target, actual);

//		Assert.assertEquals(expected, actual);
	}
}
