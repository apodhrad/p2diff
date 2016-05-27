package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.util.UnpackUtils;
import org.junit.Test;

public class DiffManagerTest {
	
	@Test
	public void general() throws IOException, Exception
	{
		JDownloadManager jdm = new JDownloadManager();
		
		File target1 = new File("target/beta");
		File target2 = new File("target/final");
		
		File testZip1 = new File("target/test-classes/data.zip");
		File testZip2 = new File("target/test-classes/data2.zip");
		
		File zip1 = jdm.download("file://" + testZip1.getAbsolutePath(), target1, false);
		File zip2 = jdm.download("file://" + testZip2.getAbsolutePath(), target2, false);
		
		File unpackZip1 = new File(target1.getPath() + "/zip1-ext");
		File unpackZip2 = new File(target2.getPath() + "/zip2-ext");
		
		UnpackUtils.unpack(zip1, unpackZip1);
		UnpackUtils.unpack(zip2, unpackZip2);
		//File zip2 = jdm.download("http://java-sourcecode.weebly.com/uploads/7/3/2/6/7326864/library_ma218756952010.zip", new File("target/beta"), true);
		//File zip1 = jdm.download("http://java-sourcecode.weebly.com/uploads/7/3/2/6/7326864/memory_mon2191991142010.zip", new File("target/final"), true);
		
		File diff = new File("target/diffs/diff.html");
		DiffManager dm = new DiffManager(unpackZip1, unpackZip2, "(.*)", diff);
		dm.generate();
	}
}
