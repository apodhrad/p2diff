package org.apodhrad.p2diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apodhrad.jdownload.manager.JDownloadManager;
import org.apodhrad.jdownload.manager.hash.MD5Hash;
import org.apodhrad.jdownload.manager.util.UnpackUtils;
import org.apodhrad.p2diff.util.ResourceUtils;
import org.junit.Test;

public class P2DiffManagerTest {

	@Test
	public void testGeneratingComplexDiffReport() throws Exception {
		File target = new File("target");

		JDownloadManager jdm = new JDownloadManager();
		File zip1 = jdm.download(
				"http://download.jboss.org/jbosstools/mars/development/updates/integration-stack/jbosstools-integration-stack-4.3.0.Beta1-earlyaccess.zip",
				target, false, new MD5Hash("51d16f5daf6aab209a9f3fc7d4ce41c4"));
		File zip2 = jdm.download(
				"http://download.jboss.org/jbosstools/mars/stable/updates/integration-stack/jbosstools-integration-stack-4.3.0.Final.zip",
				target, false, new MD5Hash("85a2a5079de5e1525872e2537b9eb8e6"));
		File zip2ea = jdm.download(
				"http://download.jboss.org/jbosstools/mars/stable/updates/integration-stack/jbosstools-integration-stack-4.3.0.Final-earlyaccess.zip",
				target, false);

		File originalFile = new File("target/originalZip");
		File revisedFile = new File("target/revisedZip");

		UnpackUtils.unpack(zip1, originalFile);
		UnpackUtils.unpack(zip2, revisedFile);
		UnpackUtils.unpack(zip2ea, revisedFile);

		P2DiffManager.generateDiffReport(originalFile, revisedFile, target);

		File diffDir = new File(target, "diff-reports");
		assertTrue(diffDir.exists());
		assertTrue(new File(diffDir, "index.html").exists());
		String expectedReport = FileUtils.readFileToString(ResourceUtils.getResourceFile("/report_complex.html"));
		assertEquals(expectedReport, FileUtils.readFileToString(new File(diffDir, "index.html")));
		assertTrue(new File(diffDir, "diffs").exists());
	}
}
