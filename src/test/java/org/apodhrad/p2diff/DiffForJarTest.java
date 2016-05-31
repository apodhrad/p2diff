package org.apodhrad.p2diff;

import static org.apodhrad.p2diff.util.ResourceUtils.getResourceFile;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DiffForJarTest {

	public static final String JAR_FILE_1 = "/com.example.p2bundle_0.0.1.v20161954-1250.jar";
	public static final String JAR_FILE_2 = "/com.example.p2bundle_0.0.2.v20161958-1251.jar";
	
//	@Test
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
	
	@Test
	public void testGeneratingDiffForDifferentJars() throws Exception {
		File jarFile1 = getResourceFile(JAR_FILE_1);
		File jarFile2 = getResourceFile(JAR_FILE_2);
		
		List<String> diff = new DiffForJar(jarFile1, jarFile2).generateDiff();
		System.out.println(diff);
	}
}
