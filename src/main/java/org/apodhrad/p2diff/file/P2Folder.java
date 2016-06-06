package org.apodhrad.p2diff.file;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apodhrad.p2diff.util.P2BundleUtils;

public class P2Folder extends Folder {

	private static final long serialVersionUID = 1L;
	private static IOFileFilter NONSOURCE_FILTER = new NonSourceBundle();

	public P2Folder(File folder) {
		super(folder);
	}

	@Override
	public File getFile(String relativePath) {
		File file = super.getFile(relativePath);
		if (file == null) {
			String path = P2BundleUtils.getBundleName(relativePath) + "_";
			if (relativePath.contains("/")) {
				int index = relativePath.lastIndexOf('/');
				path = path.substring(index + 1);
			}
			Collection<File> files = FileUtils.listFiles(this, new PrefixFileFilter(path), TrueFileFilter.INSTANCE);
			if (!files.isEmpty()) {
				return files.iterator().next();
			}
		}
		return file;
	}

	@Override
	public Collection<Delta> computeDeltas(Folder folder) {
		return computeDeltas(folder, NONSOURCE_FILTER);
	}

	@Override
	public Collection<Delta> computeDeltas(Folder folder, IOFileFilter filter) {
		Collection<String> originalPaths = this.listRelativePaths(filter);
		Collection<String> revisedPaths = folder.listRelativePaths(filter);

		Set<Delta> deltas = new HashSet<Delta>();

		for (String path : originalPaths) {
			deltas.add(new Delta(P2BundleUtils.getBundleName(path), this.getFile(path), folder.getFile(path)));
		}
		for (String path : revisedPaths) {
			deltas.add(new Delta(P2BundleUtils.getBundleName(path), this.getFile(path), folder.getFile(path)));
		}

		return deltas;
	}

	private static class NonSourceBundle implements IOFileFilter {

		@Override
		public boolean accept(File file) {
			return accept(null, file.getName());
		}

		@Override
		public boolean accept(File dir, String name) {
			return !name.contains(".source_");
		}

	}

}
