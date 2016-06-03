package org.apodhrad.p2diff.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * 
 * @author apodhrad
 *
 */
public class Folder extends File {

	private static final long serialVersionUID = 1L;

	public Folder(String pathname) {
		super(pathname);
	}

	public Folder(File folder) {
		super(folder, "");
	}

	public Folder(File parent, String child) {
		super(parent, child);
	}

	public File getFile(String relativePath) {
		File file = new File(this, relativePath);
		if (!file.exists()) {
			return null;
		}
		return file;
	}

	public Collection<String> listRelativePaths() {
		return listRelativePaths(TrueFileFilter.INSTANCE);
	}

	public Collection<String> listRelativePaths(IOFileFilter filter) {
		List<String> relativePaths = new ArrayList<String>();
		for (File file : FileUtils.listFiles(this, filter, TrueFileFilter.INSTANCE)) {
			relativePaths.add(this.toURI().relativize(file.toURI()).getPath());
		}
		return relativePaths;
	}

	public Collection<Delta> computeDeltas(Folder folder) {
		Collection<String> originalPaths = this.listRelativePaths();
		Collection<String> revisedPaths = folder.listRelativePaths();

		Set<Delta> deltas = new HashSet<Delta>();

		for (String path : originalPaths) {
			deltas.add(new Delta(path, this.getFile(path), folder.getFile(path)));
		}
		for (String path : revisedPaths) {
			deltas.add(new Delta(path, this.getFile(path), folder.getFile(path)));
		}

		return deltas;
	}
}
