package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import junit.framework.Assert;

public class DiffForFileTest {
	
	@Test
	public void testGenerating() throws Exception
	{
		DiffForFile dff = new DiffForFile(new File(getClass().getResource("/text1.txt").getFile()).getPath(), new File(getClass().getResource("/text2.txt").getFile()).getPath());
		String expected = FileUtils.readFileToString(new File(getClass().getResource("/expected.txt").getFile()));
		
		Assert.assertEquals(expected, dff.generate());
	}
}
