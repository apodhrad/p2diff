package org.apodhrad.p2diff;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apodhrad.p2diff.file.Delta;

public class P2DiffForJar {

	private P2Bundle originalBundle;
	private P2Bundle revisedBundle;

	public P2DiffForJar(File originalJar, File revisedJar) {
		this.originalBundle = new P2Bundle(originalJar);
		this.revisedBundle = new P2Bundle(revisedJar);
	}

	public List<String> generateDiff() throws IOException {
		List<String> diff = new ArrayList<String>();
		if (originalBundle.computeCRC32() == revisedBundle.computeCRC32()) {
			return diff;
		}

		Collection<String> originalPaths = originalBundle.listRelativePaths();
		Collection<String> revisedPaths = revisedBundle.listRelativePaths();

		Set<Delta> deltas = new HashSet<Delta>();

		for (String path : originalPaths) {
			deltas.add(new Delta(path, originalBundle.getFile(path), revisedBundle.getFile(path)));
		}
		for (String path : revisedPaths) {
			deltas.add(new Delta(path, originalBundle.getFile(path), revisedBundle.getFile(path)));
		}

		for (Delta delta : asSortedList(deltas)) {
			P2DiffForFile dff = new P2DiffForFile(delta.getOriginalFile(), delta.getRevisedFile());
			dff.setBaseDir(originalBundle.getExtractedJarFile().getParentFile());
			diff.addAll(dff.generateDiff());
		}

		return diff;
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

}
