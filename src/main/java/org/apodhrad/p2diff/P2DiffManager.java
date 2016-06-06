package org.apodhrad.p2diff;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apodhrad.p2diff.file.Delta;
import org.apodhrad.p2diff.file.P2Folder;
import org.apodhrad.p2diff.html.HTMLGenerator;

public class P2DiffManager {

	public static void generateDiffReport(File originalFile, File revisedFile, File target) throws Exception {
		HTMLGenerator htmlGenerator = new HTMLGenerator(target);
		if (originalFile.isFile() && revisedFile.isFile()) {
			P2DiffForFile diff = new P2DiffForFile(originalFile, revisedFile);
			diff.setBaseDir(originalFile.getParentFile());
			htmlGenerator.generateDiffReport(diff.generateDiff());
			return;
		}
		if (originalFile.isDirectory() && originalFile.isDirectory()) {
			P2Folder originalFolder = new P2Folder(originalFile);
			P2Folder revisedFolder = new P2Folder(revisedFile);

			Collection<Delta> deltas = originalFolder.computeDeltas(revisedFolder);
			for (Delta delta : deltas) {
				List<String> diffLines = new ArrayList<String>();
				if (delta.getPath().endsWith(".jar")) {
					diffLines = new P2DiffForJar(delta.getOriginalFile(), delta.getRevisedFile()).generateDiffLines();
				} else {
					diffLines = new P2DiffForFile(delta.getOriginalFile(), delta.getRevisedFile()).generateDiff();
				}
				if (!diffLines.isEmpty()) {
					htmlGenerator.generateDiff(diffLines, delta.getPath());
				}
			}
			htmlGenerator.generateDeltaReport(P2DiffForJar.asSortedList(deltas));
			return;
		}
	}

}
